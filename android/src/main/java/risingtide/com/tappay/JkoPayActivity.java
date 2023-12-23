package risingtide.com.tappay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Objects;

import tech.cherri.tpdirect.api.TPDJkoPay;
import tech.cherri.tpdirect.api.TPDServerType;
import tech.cherri.tpdirect.api.TPDSetup;
import tech.cherri.tpdirect.callback.TPDGetPrimeFailureCallback;
import tech.cherri.tpdirect.callback.TPDJkoPayGetPrimeSuccessCallback;
import tech.cherri.tpdirect.exception.TPDJkoPayException;

public class JkoPayActivity extends Activity implements TPDGetPrimeFailureCallback, TPDJkoPayGetPrimeSuccessCallback {

    private HashMap<String, Object> tpPayByPrimeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jko_pay);
        Intent intent = getIntent();
        int appId = intent.getIntExtra("appId", 0);
        String appKey = intent.getStringExtra("appKey");
        TPDServerType serverType = Objects.equals(intent.getStringExtra("serverType"), "sandbox") ? TPDServerType.Sandbox : TPDServerType.Production;
        tpPayByPrimeModel = (HashMap<String, Object>) intent.getSerializableExtra("tpPayByPrimeModel");
        //Setup environment.
        TPDSetup.initInstance(getApplicationContext(), appId, appKey, serverType);
        startJkoPay();
    }

    private void startJkoPay() {
        boolean isJkoPayAvailable = TPDJkoPay.isJkoPayAvailable(getApplicationContext());
        try {
            if (isJkoPayAvailable) {
                TPDJkoPay tpdJkoPay = new TPDJkoPay(getApplicationContext(), "jkopay://risingtide.com");
                tpdJkoPay.getPrime(this, this);
            } else {
                throw new TPDJkoPayException("JkoPay is not available");
            }
        } catch (TPDJkoPayException e) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("error", e.getMessage());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    @Override
    public void onSuccess(String prime) {
        HashMap<String, Object> data = ApiUtil.generatePayByPrimeDataForSandBox(prime, tpPayByPrimeModel);
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
}