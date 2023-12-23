package risingtide.com.tappay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.HashMap;

import tech.cherri.tpdirect.api.TPDEasyWallet;
import tech.cherri.tpdirect.api.TPDEasyWalletResult;
import tech.cherri.tpdirect.callback.TPDEasyWalletResultListener;
import tech.cherri.tpdirect.exception.TPDEasyWalletException;

public class EasyWalletReturnActivity extends Activity implements TPDEasyWalletResultListener {

    private TPDEasyWallet tpdEasyWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_wallet_return);
        Intent intent = getIntent();
        String paymentUrl = intent.getStringExtra("paymentUrl");
        try {
            tpdEasyWallet = new TPDEasyWallet(getApplicationContext(), "easywallet://risingtide.com");
            tpdEasyWallet.redirectWithUrl(paymentUrl);
        } catch (TPDEasyWalletException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onParseSuccess(TPDEasyWalletResult tpdEasyWalletResult) {
        if (tpdEasyWalletResult != null) {
            int status = tpdEasyWalletResult.getStatus();
            String recTradeId = tpdEasyWalletResult.getRecTradeId();
            String bankTransactionId = tpdEasyWalletResult.getBankTransactionId();
            String orderNumber = tpdEasyWalletResult.getOrderNumber();
            HashMap<String, Object> data = new HashMap<>();
            data.put("status", status);
            data.put("recTradeId", recTradeId);
            data.put("bankTransactionId", bankTransactionId);
            data.put("orderNumber", orderNumber);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("data", data);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    @Override
    public void onParseFail(int status, String msg) {
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
        if (intent.getDataString() != null && intent.getDataString().contains("easywallet://risingtide.com")) {
            tpdEasyWallet.parseToEasyWalletResult(getApplicationContext(), intent.getData(), this);
        }
    }
}