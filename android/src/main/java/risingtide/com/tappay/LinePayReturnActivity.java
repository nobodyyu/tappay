package risingtide.com.tappay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.HashMap;

import tech.cherri.tpdirect.api.TPDLinePay;
import tech.cherri.tpdirect.api.TPDLinePayResult;
import tech.cherri.tpdirect.callback.TPDLinePayResultListener;
import tech.cherri.tpdirect.exception.TPDLinePayException;

public class LinePayReturnActivity extends Activity implements TPDLinePayResultListener {

    private TPDLinePay tpdLinePay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_pay_return);
        Intent intent = getIntent();
        String paymentUrl = intent.getStringExtra("paymentUrl");
        try {
            tpdLinePay = new TPDLinePay(getApplicationContext(), "linepay://risingtide.com");
            tpdLinePay.redirectWithUrl(paymentUrl);
        } catch (TPDLinePayException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onParseSuccess(TPDLinePayResult tpdLinePayResult) {
        if (tpdLinePayResult != null) {
            int status = tpdLinePayResult.getStatus();
            String recTradeId = tpdLinePayResult.getRecTradeId();
            String bankTransactionId = tpdLinePayResult.getBankTransactionId();
            String orderNumber = tpdLinePayResult.getOrderNumber();
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
        if (intent.getDataString() != null && intent.getDataString().contains("linepay://risingtide.com")) {
            tpdLinePay.parseToLinePayResult(getApplicationContext(), intent.getData(), this);
        }
    }
}