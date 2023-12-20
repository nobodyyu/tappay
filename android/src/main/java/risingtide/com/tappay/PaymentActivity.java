package risingtide.com.tappay;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Objects;

import tech.cherri.tpdirect.api.TPDCard;
import tech.cherri.tpdirect.api.TPDCcv;
import tech.cherri.tpdirect.api.TPDCcvForm;
import tech.cherri.tpdirect.api.TPDForm;
import tech.cherri.tpdirect.api.TPDServerType;
import tech.cherri.tpdirect.api.TPDSetup;
import tech.cherri.tpdirect.callback.TPDCardGetPrimeSuccessCallback;
import tech.cherri.tpdirect.callback.TPDCcvGetPrimeSuccessCallback;
import tech.cherri.tpdirect.callback.TPDGetPrimeFailureCallback;
import tech.cherri.tpdirect.model.TPDCcvStatus;
import tech.cherri.tpdirect.model.TPDStatus;


public class PaymentActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "PaymentActivity";
    private static final int REQUEST_READ_PHONE_STATE = 101;

    private TextView tipsTV;
    private Button payBTN;
    private TPDCard tpdCard;
    private TPDCcv tpdCcv;
    private TextView statusTV;
    private Button getCcvPrimeBTN;
    private Integer appId;
    private String appKey;
    private TPDServerType serverType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setupViews();
        Intent intent = getIntent();
        appId = intent.getIntExtra("appId", 0);
        appKey = intent.getStringExtra("appKey");
        serverType = Objects.equals(intent.getStringExtra("serverType"), "sandbox") ? TPDServerType.Sandbox : TPDServerType.Production;
        Log.d(TAG, "SDK version is " + TPDSetup.getVersion());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            requestPermissions();
        } else {
            startTapPaySetting();
        }
    }

    private void setupViews() {
        statusTV = findViewById(R.id.statusTV);
        tipsTV = findViewById(R.id.tipsTV);
        payBTN = findViewById(R.id.payBTN);
        payBTN.setOnClickListener(this);
        payBTN.setEnabled(false);

        getCcvPrimeBTN = findViewById(R.id.getCcvPrimeBTN);
        getCcvPrimeBTN.setOnClickListener(v -> tpdCcv.getPrime());
        getCcvPrimeBTN.setEnabled(false);
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "PERMISSION IS ALREADY GRANTED");
            startTapPaySetting();
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
            startTapPaySetting();
        }
    }


    private void startTapPaySetting() {
        Log.d(TAG, "startTapPaySetting");
        //1.Setup environment.
        TPDSetup.initInstance(getApplicationContext(),
                appId, appKey, serverType);
        //2.Setup input form
        TPDForm tpdForm = findViewById(R.id.tpdCardInputForm);
        tpdForm.setTextErrorColor(Color.RED);
        tpdForm.setOnFormUpdateListener(tpdStatus -> {
            tipsTV.setText("");
            if (tpdStatus.getCardNumberStatus() == TPDStatus.STATUS_ERROR) {
                tipsTV.setText("Invalid Card Number");
            } else if (tpdStatus.getExpirationDateStatus() == TPDStatus.STATUS_ERROR) {
                tipsTV.setText("Invalid Expiration Date");
            } else if (tpdStatus.getCcvStatus() == TPDStatus.STATUS_ERROR) {
                tipsTV.setText("Invalid CCV");
            }
            payBTN.setEnabled(tpdStatus.isCanGetPrime());
        });


        //3.Setup TPDCard with form and callbacks.
        TPDCardGetPrimeSuccessCallback tpdCardGetPrimeSuccessCallback = (prime, cardInfo, cardIdentifier, merchantReferenceInfo) -> {

            Log.d("TPDirect getPrime", "prime:  " + prime);
            Log.d("TPDirect getPrime", "cardInfo:  " + cardInfo);
            Log.d("TPDirect getPrime", "cardIdentifier:  " + cardIdentifier);
            Log.d("TPDirect getPrime", "merchantReferenceInfo:  " + merchantReferenceInfo);

            Toast.makeText(PaymentActivity.this,
                    "Get Prime Success",
                    Toast.LENGTH_SHORT).show();

            String resultStr = "prime is " + prime + "\n\n" +
                    "cardInfo is " + cardInfo + "\n\n" +
                    "cardIdentifier is " + cardIdentifier + "\n\n" +
                    "merchantReferenceInfo is " + merchantReferenceInfo + "\n\n" +
                    "Use below cURL to proceed the payment : \n"
                    + ApiUtil.generatePayByPrimeDataForSandBox(prime, Constants.PARTNER_KEY,
                    Constants.MERCHANT_ID);
            statusTV.setText(resultStr);
            Log.d(TAG, resultStr);

            HashMap<String, Object> data = ApiUtil.generatePayByPrimeDataForSandBox(prime, Constants.PARTNER_KEY, Constants.MERCHANT_ID);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("data", data);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        };
        TPDGetPrimeFailureCallback tpdGetPrimeFailureCallback = (status, msg) -> {
            Log.d("TPDirect createToken", "failure: " + status + ": " + msg);
            Toast.makeText(PaymentActivity.this,
                    "Create Token Failed\n" + status + ": " + msg,
                    Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("error", status + ": " + msg);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        };

        tpdCard = TPDCard.setup(tpdForm).onSuccessCallback(tpdCardGetPrimeSuccessCallback)
                .onFailureCallback(tpdGetPrimeFailureCallback);

        //For getDeviceId
        Button getDeviceIdBTN = findViewById(R.id.getDeviceIdBTN);
        getDeviceIdBTN.setOnClickListener(this);

        //For getCcvPrime

        TPDCcvForm tpdCcvForm = findViewById(R.id.tpdCcvInputForm);
        tpdCcvForm.setTextErrorColor(Color.RED);
        tpdCcvForm.setOnFormUpdateListener(tpdCcvStatus -> {
            tipsTV.setText("");
            if (tpdCcvStatus.getCcvStatus() == TPDCcvStatus.Status.ERROR) {
                tipsTV.setText("Invalid CCV");
            }
            getCcvPrimeBTN.setEnabled(tpdCcvStatus.isCanGetPrime());
        });


        TPDCcvGetPrimeSuccessCallback tpdCcvGetPrimeSuccessCallback = ccvPrime -> {

            Log.d("TPDirect ccvPrime", "prime:  " + ccvPrime);

            Toast.makeText(PaymentActivity.this,
                    "Get Ccv Prime Success",
                    Toast.LENGTH_SHORT).show();

            String resultStr = "ccv prime is " + ccvPrime + "\n\n";

            statusTV.setText(resultStr);
            Log.d(TAG, resultStr);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("ccvPrime", ccvPrime);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        };
        TPDGetPrimeFailureCallback tpdGetCcvPrimeFailureCallback = (status, msg) -> {
            Log.d("TPDirect Get Ccv Prime", "failure: " + status + ": " + msg);
            Toast.makeText(PaymentActivity.this,
                    "Get Ccv Prime Failed\n" + status + ": " + msg,
                    Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("error", status + ": " + msg);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        };

        tpdCcv = TPDCcv.setup(tpdCcvForm).onSuccessCallback(tpdCcvGetPrimeSuccessCallback)
                .onFailureCallback(tpdGetCcvPrimeFailureCallback);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.getDeviceIdBTN) {
            //GetFraudId for PayByToken
            String deviceId = TPDSetup.getInstance(getApplicationContext()).getRbaDeviceId();
            Toast.makeText(this, "DeviceId is:" + deviceId, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.payBTN) {
            //4. Calling API for obtaining prime.
            if (tpdCard != null) {
                tpdCard.getPrime();
            }
        } else if (id == R.id.getCcvPrimeBTN) {
            if (tpdCard != null) {
                tpdCard.getPrime();
            }
        }

    }


}