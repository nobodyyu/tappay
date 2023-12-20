package risingtide.com.tappay;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import tech.cherri.tpdirect.api.TPDLinePay;
import tech.cherri.tpdirect.api.TPDLinePayResult;
import tech.cherri.tpdirect.api.TPDServerType;
import tech.cherri.tpdirect.api.TPDSetup;
import tech.cherri.tpdirect.callback.TPDGetPrimeFailureCallback;
import tech.cherri.tpdirect.callback.TPDLinePayGetPrimeSuccessCallback;
import tech.cherri.tpdirect.callback.TPDLinePayResultListener;
import tech.cherri.tpdirect.exception.TPDLinePayException;

public class LinePayActivity extends Activity implements TPDGetPrimeFailureCallback, TPDLinePayGetPrimeSuccessCallback, View.OnClickListener, TPDLinePayResultListener {
    private static final String TAG = "LinePayActivity";
    private static final int REQUEST_READ_PHONE_STATE = 101;
    private RelativeLayout linePayBTN;
    private TPDLinePay tpdLinePay;
    private TextView getPrimeResultStateTV;
    private TextView linePayResultTV;
    private final FirebaseFunctions functions = FirebaseFunctions.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_pay);
        setupViews();
        Intent intent = getIntent();
        int appId = intent.getIntExtra("appId", 0);
        String appKey = intent.getStringExtra("appKey");
        TPDServerType serverType = Objects.equals(intent.getStringExtra("serverType"), "sandbox") ? TPDServerType.Sandbox : TPDServerType.Production;
        Log.d(TAG, "SDK version is " + TPDSetup.getVersion());
        //Setup environment.
        TPDSetup.initInstance(getApplicationContext(), appId, appKey, serverType);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions();
        } else {
            prepareLinePay();
        }
        handleIncomingIntent(getIntent());
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "PERMISSION IS ALREADY GRANTED");
            prepareLinePay();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_PHONE_STATE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.d(TAG, "PERMISSION_GRANTED");
            }
            prepareLinePay();
        }
    }

    private void prepareLinePay() {
        boolean isLinePayAvailable = TPDLinePay.isLinePayAvailable(this.getApplicationContext());
        Toast.makeText(this, "isLinePayAvailable : " + isLinePayAvailable, Toast.LENGTH_SHORT).show();
        try {
            if (true) {
                tpdLinePay = new TPDLinePay(getApplicationContext(), "linepay://risingtide.com");
                linePayBTN.setEnabled(true);
            } else {
                throw new TPDLinePayException("LinePay is not available");
            }
        } catch (TPDLinePayException e) {
            showMessage(e.getMessage());
            linePayBTN.setEnabled(false);
        }
    }

    private void setupViews() {
        TextView totalAmountTV = findViewById(R.id.totalAmountTV);
        totalAmountTV.setText("Total amount : 1.00 元");
        getPrimeResultStateTV = findViewById(R.id.getPrimeResultStateTV);
        linePayResultTV = findViewById(R.id.linePayResultTV);
        linePayBTN = findViewById(R.id.linePayBTN);
        linePayBTN.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        tpdLinePay.getPrime(this, this);
    }


    @Override
    public void onSuccess(String prime) {
        String resultStr = "Your prime is " + prime + "\n\nUse below cURL to get payment url with Pay-by-Prime API on your server side: \n" + ApiUtil.generatePayByPrimeCURLForSandBox(prime, Constants.PARTNER_KEY, Constants.MERCHANT_ID);

        showMessage(resultStr);
        Log.d(TAG, resultStr);

        sendTransactionDataToServer();

        //Proceed LINE Pay with below function.
//        tpdLinePay.redirectWithUrl("Your payment url ");

        Intent resultIntent = new Intent();
        resultIntent.putExtra("prime", prime);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onFailure(int status, String msg) {
        showMessage("GetPrime failed , status = " + status + ", msg : " + msg);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("error", status + ": " + msg);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingIntent(intent);
    }

    private void handleIncomingIntent(Intent intent) {
        if (intent.getDataString() != null && intent.getDataString().contains("linepay://risingtide.com")) {
            if (tpdLinePay == null) {
                prepareLinePay();
            }
            tpdLinePay.parseToLinePayResult(getApplicationContext(), intent.getData(), this);
        }
    }

    private void showMessage(String s) {
        getPrimeResultStateTV.setText(s);
    }

    @Override
    public void onParseSuccess(TPDLinePayResult tpdLinePayResult) {
        if (tpdLinePayResult != null) {
            linePayResultTV.setText("status:" + tpdLinePayResult.getStatus() + "\nrec_trade_id:" + tpdLinePayResult.getRecTradeId() + "\nbank_transaction_id:" + tpdLinePayResult.getBankTransactionId() + "\norder_number:" + tpdLinePayResult.getOrderNumber());
        }
    }

    @Override
    public void onParseFail(int status, String msg) {
        linePayResultTV.setText("Parse LINE Pay result failed  status : " + status + " , msg : " + msg);
    }

    private Task<String> sendTransactionDataToServer() {
        Map<String, Object> data = new HashMap<>();
        data.put("text", "");
        data.put("push", true);
        return functions.getHttpsCallable("receiveTappayTransactionData").call().continueWith(task -> (String) task.getResult().getData()).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Exception e = task.getException();
                if (e instanceof FirebaseFunctionsException) {
                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                    FirebaseFunctionsException.Code code = ffe.getCode();
                    Object details = ffe.getDetails();
                    Log.d(TAG, code.toString());
                }
            }
        });
    }
}