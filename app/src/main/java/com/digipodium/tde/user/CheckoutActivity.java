package com.digipodium.tde.user;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.digipodium.tde.R;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CheckoutActivity extends AppCompatActivity {
    private static final String BACKEND_URL = "https://military-rightful-ton.glitch.me/checkout";
    private static final String STRIPE_PUBLISHABLE_KEY = "pk_test_sSSEzfmRLvGndJHKhtppbbb5";

    private PaymentSheet paymentSheet;

    private String paymentIntentClientSecret;
    private String customerId;
    private String ephemeralKeySecret;

    private Button buyButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.checkout_acitivity);
        buyButton = findViewById(R.id.btnBu);
        buyButton.setEnabled(false);

        PaymentConfiguration.init(this, STRIPE_PUBLISHABLE_KEY);

        paymentSheet = new PaymentSheet(this, result -> {
            onPaymentSheetResult(result);
        });

        buyButton.setOnClickListener(v -> presentPaymentSheet());

        fetchInitData();
    }

    private void fetchInitData() {
        final String requestJson = "{}";
        final RequestBody requestBody = RequestBody.create(MediaType.get("application/json; charset=utf-8"), requestJson);

        final Request request = new Request.Builder()
                .url(BACKEND_URL + "payment-sheet")
                .post(requestBody)
                .build();

        new OkHttpClient()
                .newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Toast.makeText(CheckoutActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(
                            @NotNull Call call,
                            @NotNull Response response
                    ) throws IOException {
                        if (!response.isSuccessful()) {

                        } else {
                            final JSONObject responseJson = parseResponse(response.body());

                            paymentIntentClientSecret = responseJson.optString("paymentIntent");
                            customerId = responseJson.optString("customer");
                            ephemeralKeySecret = responseJson.optString("ephemeralKey");

                            runOnUiThread(() -> buyButton.setEnabled(true));
                        }
                    }
                });
    }

    private JSONObject parseResponse(ResponseBody responseBody) {
        if (responseBody != null) {
            try {
                return new JSONObject(responseBody.string());
            } catch (IOException | JSONException e) {
                Log.e("App", "Error parsing response", e);
            }
        }
        return new JSONObject();
    }

    // continued from above

    private void presentPaymentSheet() {
        paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret,
                new PaymentSheet.Configuration(
                        "TDE",
                        new PaymentSheet.CustomerConfiguration(
                                customerId,
                                ephemeralKeySecret
                        )
                )
        );
    }

    private void onPaymentSheetResult(
            final PaymentSheetResult paymentSheetResult
    ) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(
                    this,
                    "Payment Canceled",
                    Toast.LENGTH_LONG
            ).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Toast.makeText(
                    this,
                    "Payment Failed. See logcat for details.",
                    Toast.LENGTH_LONG
            ).show();

            Log.e("App", "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(
                    this,
                    "Payment Complete",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

}
