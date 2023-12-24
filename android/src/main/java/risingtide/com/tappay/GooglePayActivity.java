package risingtide.com.tappay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.WalletConstants;

import java.util.HashMap;
import java.util.Objects;

import tech.cherri.tpdirect.api.TPDCard;
import tech.cherri.tpdirect.api.TPDConsumer;
import tech.cherri.tpdirect.api.TPDGooglePay;
import tech.cherri.tpdirect.api.TPDMerchant;
import tech.cherri.tpdirect.api.TPDServerType;
import tech.cherri.tpdirect.api.TPDSetup;
import tech.cherri.tpdirect.callback.TPDGetPrimeFailureCallback;
import tech.cherri.tpdirect.callback.TPDGooglePayGetPrimeSuccessCallback;
import tech.cherri.tpdirect.callback.TPDGooglePayListener;
import tech.cherri.tpdirect.callback.dto.TPDCardInfoDto;
import tech.cherri.tpdirect.callback.dto.TPDMerchantReferenceInfoDto;
import tech.cherri.tpdirect.exception.TPDGooglePayException;

public class GooglePayActivity extends Activity implements TPDGooglePayListener, TPDGetPrimeFailureCallback, TPDGooglePayGetPrimeSuccessCallback {

    private final TPDCard.CardType[] allowedNetworks = new TPDCard.CardType[]{TPDCard.CardType.Visa
            , TPDCard.CardType.MasterCard
            , TPDCard.CardType.JCB
            , TPDCard.CardType.AmericanExpress};
    private final TPDCard.AuthMethod[] allowedAuthMethods = new TPDCard.AuthMethod[]{TPDCard.AuthMethod.Cryptogram3DS};
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 102;
    private TPDGooglePay tpdGooglePay;
    private HashMap<String, Object> tpPayByPrimeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_pay);
        Intent intent = getIntent();
        int appId = intent.getIntExtra("appId", 0);
        String appKey = intent.getStringExtra("appKey");
        TPDServerType serverType = Objects.equals(intent.getStringExtra("serverType"), "sandbox") ? TPDServerType.Sandbox : TPDServerType.Production;
        tpPayByPrimeModel = (HashMap<String, Object>) intent.getSerializableExtra("tpPayByPrimeModel");
        //Setup environment.
        TPDSetup.initInstance(getApplicationContext(), appId, appKey, serverType);
        prepareGooglePay();
    }

    public void prepareGooglePay() {
        TPDMerchant tpdMerchant = new TPDMerchant();
        tpdMerchant.setSupportedNetworks(allowedNetworks);
        tpdMerchant.setMerchantName("測試店家");
        tpdMerchant.setSupportedAuthMethods(allowedAuthMethods);

        TPDConsumer tpdConsumer = new TPDConsumer();
        tpdConsumer.setPhoneNumberRequired(false);
        tpdConsumer.setShippingAddressRequired(false);
        tpdConsumer.setEmailRequired(false);

        tpdGooglePay = new TPDGooglePay(this, tpdMerchant, tpdConsumer);
        tpdGooglePay.isGooglePayAvailable(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE) {
            Intent resultIntent;
            switch (resultCode) {
                case Activity.RESULT_OK:
                    PaymentData paymentData = PaymentData.getFromIntent(data);
                    tpdGooglePay.getPrime(paymentData, this, this);
                    break;
                case Activity.RESULT_CANCELED:
                    finish();
                    break;
                case AutoResolveHelper.RESULT_ERROR:
                    Status status = AutoResolveHelper.getStatusFromIntent(data);
                    resultIntent = new Intent();
                    assert status != null;
                    resultIntent.putExtra("error", "AutoResolveHelper.RESULT_ERROR : " + status.getStatusCode() + " , message = " + status.getStatusMessage());
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                    break;
                default:
                    // Do nothing.
            }
        }
    }

    @Override
    public void onReadyToPayChecked(boolean isReadyToPay, String msg) {
        try {
            if (isReadyToPay) {
                tpdGooglePay.requestPayment(TransactionInfo.newBuilder()
                        .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                        .setTotalPrice("1")
                        .setCurrencyCode("TWD")
                        .build(), LOAD_PAYMENT_DATA_REQUEST_CODE);
            } else {
                throw new TPDGooglePayException("Google Pay is not available");
            }
        } catch (TPDGooglePayException e) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("error", e.getMessage());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    @Override
    public void onSuccess(String prime, TPDCardInfoDto tpdCardInfoDto, TPDMerchantReferenceInfoDto tpdMerchantReferenceInfoDto) {
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