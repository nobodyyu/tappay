package risingtide.com.tappay;

import java.util.HashMap;

public class ApiUtil {
    public static HashMap<String, Object> generatePayByPrimeDataForSandBox(String prime, HashMap<String, Object> tpPayByPrimeModel) {
        HashMap<String, Object> data = new HashMap<>();
        String partnerKey = (String) tpPayByPrimeModel.get("partnerKey");
        String merchantId = (String) tpPayByPrimeModel.get("merchantId");
        int amount = (int) tpPayByPrimeModel.get("amount");
        int currency = -1;
        if (tpPayByPrimeModel.containsKey("currency")) {
            currency = (int) tpPayByPrimeModel.get("currency");
        }
        String orderNumber = null;
        if (tpPayByPrimeModel.containsKey("orderNumber")) {
            orderNumber = (String) tpPayByPrimeModel.get("orderNumber");
        }
        String details = (String) tpPayByPrimeModel.get("details");

        HashMap<String, String> cardHolder = (HashMap<String, String>) tpPayByPrimeModel.get("cardHolder");
        assert cardHolder != null;
        String phoneNumber = (String) cardHolder.get("phoneNumber");
        String name = (String) cardHolder.get("name");
        String email = (String) cardHolder.get("email");
        String zipCode = null;
        if (cardHolder.containsKey("zipCode")) {
            zipCode = (String) cardHolder.get("zipCode");
        }
        String address = null;
        if (cardHolder.containsKey("address")) {
            address = (String) cardHolder.get("address");
        }
        String nationalId = null;
        if (cardHolder.containsKey("nationalId")) {
            nationalId = (String) cardHolder.get("nationalId");
        }
        String memberId = null;
        if (cardHolder.containsKey("memberId")) {
            memberId = (String) cardHolder.get("memberId");
        }
        cardHolder = new HashMap<String, String>();
        cardHolder.put("phone_number", phoneNumber);
        cardHolder.put("name", name);
        cardHolder.put("email", email);
        if (zipCode != null) {
            cardHolder.put("zip_code", zipCode);
        }
        if (address != null) {
            cardHolder.put("address", address);
        }
        if (nationalId != null) {
            cardHolder.put("national_id", nationalId);
        }
        if (memberId != null) {
            cardHolder.put("member_id", memberId);
        }

        HashMap<String, String> resultUrl = null;
        if (tpPayByPrimeModel.containsKey("resultUrl")) {
            resultUrl = (HashMap<String, String>) tpPayByPrimeModel.get("resultUrl");
        }
        if (resultUrl != null) {
            String frontendRedirectUrl = null;
            if (resultUrl.containsKey("frontendRedirectUrl")) {
                frontendRedirectUrl = (String) resultUrl.get("frontendRedirectUrl");
            }
            String backendNotifyUrl = null;
            if (resultUrl.containsKey("backendNotifyUrl")) {
                backendNotifyUrl = (String) resultUrl.get("backendNotifyUrl");
            }
            String goBackUrl = null;
            if (resultUrl.containsKey("goBackUrl")) {
                goBackUrl = (String) resultUrl.get("goBackUrl");
            }
            resultUrl = new HashMap<String, String>();
            if (frontendRedirectUrl != null) {
                resultUrl.put("frontend_redirect_url", frontendRedirectUrl);
            }
            if ( backendNotifyUrl != null) {
                resultUrl.put("backend_notify_url",  backendNotifyUrl);
            }
            if (goBackUrl != null) {
                resultUrl.put("go_back_url", goBackUrl);
            }
        }

        data.put("prime", prime);
        data.put("partner_key", partnerKey);
        data.put("merchant_id", merchantId);
        data.put("amount", amount);
        if (currency != -1) {
            data.put("currency", currency);
        }
        if (orderNumber != null) {
            data.put("order_number", orderNumber);
        }
        data.put("details", details);
        data.put("cardholder", cardHolder);
        if (resultUrl != null) {
            data.put("result_url", resultUrl);
        }
        return data;
    }

}
