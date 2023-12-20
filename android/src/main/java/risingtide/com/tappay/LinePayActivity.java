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

import java.util.HashMap;
import java.util.Objects;

import tech.cherri.tpdirect.api.TPDLinePay;
import tech.cherri.tpdirect.api.TPDServerType;
import tech.cherri.tpdirect.api.TPDSetup;
import tech.cherri.tpdirect.callback.TPDGetPrimeFailureCallback;
import tech.cherri.tpdirect.callback.TPDLinePayGetPrimeSuccessCallback;
import tech.cherri.tpdirect.exception.TPDLinePayException;

public class LinePayActivity extends Activity implements TPDGetPrimeFailureCallback, TPDLinePayGetPrimeSuccessCallback, View.OnClickListener {
    private static final String TAG = "LinePayActivity";
    private static final int REQUEST_READ_PHONE_STATE = 101;
    private RelativeLayout linePayBTN;
    private TPDLinePay tpdLinePay;
    private TextView getPrimeResultStateTV;

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
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            prepareLinePay();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_PHONE_STATE) {
            prepareLinePay();
        }
    }

    private void prepareLinePay() {
        boolean isLinePayAvailable = TPDLinePay.isLinePayAvailable(getApplicationContext());
        Toast.makeText(this, "isLinePayAvailable : " + isLinePayAvailable, Toast.LENGTH_SHORT).show();
        try {
            if (isLinePayAvailable) {
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
        linePayBTN = findViewById(R.id.linePayBTN);
        linePayBTN.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        tpdLinePay.getPrime(this, this);
    }


    @Override
    public void onSuccess(String prime) {
        HashMap<String, Object> data = ApiUtil.generatePayByPrimeDataForSandBox(prime, Constants.PARTNER_KEY, Constants.MERCHANT_ID);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("data", data);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onFailure(int status, String msg) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("error", status + ": " + msg);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void showMessage(String s) {
        getPrimeResultStateTV.setText(s);
    }

}