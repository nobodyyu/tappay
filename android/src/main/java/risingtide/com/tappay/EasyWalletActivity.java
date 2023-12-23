package risingtide.com.tappay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Objects;

import tech.cherri.tpdirect.api.TPDEasyWallet;
import tech.cherri.tpdirect.api.TPDServerType;
import tech.cherri.tpdirect.api.TPDSetup;
import tech.cherri.tpdirect.callback.TPDEasyWalletGetPrimeSuccessCallback;
import tech.cherri.tpdirect.callback.TPDGetPrimeFailureCallback;
import tech.cherri.tpdirect.exception.TPDEasyWalletException;

public class EasyWalletActivity extends Activity implements TPDGetPrimeFailureCallback, TPDEasyWalletGetPrimeSuccessCallback {

    private HashMap<String, Object> tpPayByPrimeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_wallet);
        Intent intent = getIntent();
        int appId = intent.getIntExtra("appId", 0);
        String appKey = intent.getStringExtra("appKey");
        TPDServerType serverType = Objects.equals(intent.getStringExtra("serverType"), "sandbox") ? TPDServerType.Sandbox : TPDServerType.Production;
        tpPayByPrimeModel = (HashMap<String, Object>) intent.getSerializableExtra("tpPayByPrimeModel");
        //Setup environment.
        TPDSetup.initInstance(getApplicationContext(), appId, appKey, serverType);
        startEasyWallet();
    }

    private void startEasyWallet() {
        boolean isEasyWalletAvailable = TPDEasyWallet.isAvailable(getApplicationContext());
        try {
            if (isEasyWalletAvailable) {
                TPDEasyWallet tpdEasyWallet = new TPDEasyWallet(getApplicationContext(), "easywallet://risingtide.com");
                tpdEasyWallet.getPrime(this, this);
            } else {
                throw new TPDEasyWalletException("EasyWallet is not available");
            }
        } catch (TPDEasyWalletException e) {
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