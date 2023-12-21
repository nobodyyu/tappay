package risingtide.com.tappay;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiUtil {
    public static HashMap<String, Object> generatePayByPrimeDataForSandBox(String prime, String partnerKey, String merchantId) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("partner_key", partnerKey);
        data.put("prime", prime);
        data.put("merchant_id", merchantId);
        data.put("amount", 499);
        data.put("currency", "TWD");
        data.put("order_number", "SN0001");
        data.put("details", "應用程式內購");
        Map<String, String> cardholder = new HashMap<>();
        cardholder.put("phone_number", "+886958499748");
        cardholder.put("name", "CHANG YI SHENG");
        cardholder.put("email", "eason4best@email.com");
        data.put("cardholder", cardholder);
        return data;
    }

}
