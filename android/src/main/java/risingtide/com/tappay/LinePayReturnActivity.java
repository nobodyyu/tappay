package risingtide.com.tappay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

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
            Toast.makeText(this, "status:" + tpdLinePayResult.getStatus() + "\nrec_trade_id:" + tpdLinePayResult.getRecTradeId() + "\nbank_transaction_id:" + tpdLinePayResult.getBankTransactionId() + "\norder_number:" + tpdLinePayResult.getOrderNumber(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onParseFail(int status, String msg) {
        Toast.makeText(this, "Parse LINE Pay result failed  status : " + status + " , msg : " + msg, Toast.LENGTH_SHORT).show();
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