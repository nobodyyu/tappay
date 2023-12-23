package risingtide.com.tappay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.HashMap;

import tech.cherri.tpdirect.api.TPDJkoPay;
import tech.cherri.tpdirect.api.TPDJkoPayResult;
import tech.cherri.tpdirect.callback.TPDJkoPayResultListener;
import tech.cherri.tpdirect.exception.TPDJkoPayException;

public class JkoPayReturnActivity extends Activity implements TPDJkoPayResultListener {

    private TPDJkoPay tpdJkoPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jko_pay_return);
        Intent intent = getIntent();
        String paymentUrl = intent.getStringExtra("paymentUrl");
        try {
            tpdJkoPay = new TPDJkoPay(getApplicationContext(), "jkopay://risingtide.com");
            tpdJkoPay.redirectWithUrl(paymentUrl);
        } catch (TPDJkoPayException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onParseSuccess(TPDJkoPayResult tpdJkoPayResult) {
        if (tpdJkoPayResult != null) {
            int status = tpdJkoPayResult.getStatus();
            String recTradeId = tpdJkoPayResult.getRecTradeId();
            String bankTransactionId = tpdJkoPayResult.getBankTransactionId();
            String orderNumber = tpdJkoPayResult.getOrderNumber();
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
        if (intent.getDataString() != null && intent.getDataString().contains("jkopay://risingtide.com")) {
            tpdJkoPay.parseToJkoPayResult(getApplicationContext(), intent.getData(), this);
        }
    }
}