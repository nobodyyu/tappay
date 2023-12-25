package risingtide.com.tappay;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Objects;

import tech.cherri.tpdirect.api.TPDCard;
import tech.cherri.tpdirect.api.TPDForm;
import tech.cherri.tpdirect.api.TPDServerType;
import tech.cherri.tpdirect.api.TPDSetup;
import tech.cherri.tpdirect.callback.TPDCardGetPrimeSuccessCallback;
import tech.cherri.tpdirect.callback.TPDGetPrimeFailureCallback;
import tech.cherri.tpdirect.model.TPDStatus;


public class PaymentActivity extends Activity implements View.OnClickListener {
    private TextView tipsTV;
    private Button payBTN;
    private TPDCard tpdCard;
    private Integer appId;
    private String appKey;
    private TPDServerType serverType;
    private HashMap<String, Object> tpPayByPrimeModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setupViews();
        Intent intent = getIntent();
        appId = intent.getIntExtra("appId", 0);
        appKey = intent.getStringExtra("appKey");
        serverType = Objects.equals(intent.getStringExtra("serverType"), "sandbox") ? TPDServerType.Sandbox : TPDServerType.Production;
        tpPayByPrimeModel = (HashMap<String, Object>) intent.getSerializableExtra("tpPayByPrimeModel");
        startTapPaySetting();

    }

    private void setupViews() {
        tipsTV = findViewById(R.id.tipsTV);
        payBTN = findViewById(R.id.payBTN);
        payBTN.setOnClickListener(this);
        payBTN.setEnabled(false);
    }

    private void startTapPaySetting() {
        //1.Setup environment.
        TPDSetup.initInstance(getApplicationContext(),
                appId, appKey, serverType);
        //2.Setup input form
        TPDForm tpdForm = findViewById(R.id.tpdCardInputForm);
        tpdForm.setTextErrorColor(Color.RED);
        tpdForm.setOnFormUpdateListener(tpdStatus -> {
            tipsTV.setText("");
            if (tpdStatus.getCardNumberStatus() == TPDStatus.STATUS_ERROR) {
                tipsTV.setText("無效的卡號");
            } else if (tpdStatus.getExpirationDateStatus() == TPDStatus.STATUS_ERROR) {
                tipsTV.setText("無效的到期日");
            } else if (tpdStatus.getCcvStatus() == TPDStatus.STATUS_ERROR) {
                tipsTV.setText("無效的 CCV");
            }
            payBTN.setEnabled(tpdStatus.isCanGetPrime());
        });

        TPDCardGetPrimeSuccessCallback tpdCardGetPrimeSuccessCallback = (prime, cardInfo, cardIdentifier, merchantReferenceInfo) -> {
            HashMap<String, Object> data = ApiUtil.generatePayByPrimeDataForSandBox(prime, tpPayByPrimeModel);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("data", data);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        };
        TPDGetPrimeFailureCallback tpdGetPrimeFailureCallback = (status, msg) -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("error", status + ": " + msg);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        };

        tpdCard = TPDCard.setup(tpdForm).onSuccessCallback(tpdCardGetPrimeSuccessCallback)
                .onFailureCallback(tpdGetPrimeFailureCallback);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.payBTN) {
            if (tpdCard != null) {
                tpdCard.getPrime();
            }
        }

    }
}