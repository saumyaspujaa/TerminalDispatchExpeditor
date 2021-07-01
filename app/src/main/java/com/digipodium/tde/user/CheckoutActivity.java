package com.digipodium.tde.user;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.digipodium.tde.databinding.CheckoutAcitivityBinding;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.AutoResolveHelper;

import java.util.ArrayList;

/**
 * Checkout implementation for the app
 */
public class CheckoutActivity extends AppCompatActivity {
    // Arbitrarily-picked constant integer you define to track a request for payment data activity.

    String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
    int GOOGLE_PAY_REQUEST_CODE = 123;
    private CheckoutAcitivityBinding layoutBinding;
    private View googlePayButton;
    private int amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        amount = getIntent().getIntExtra("amount", 100);
        initializeUi();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // value passed in AutoResolveHelper
        if (requestCode == GOOGLE_PAY_REQUEST_CODE) {
            switch (resultCode) {

                case Activity.RESULT_OK:
                    String value = data.getStringExtra("response");
                    ArrayList<String> list = new ArrayList<>();
                    list.add(value);
                    getStatus(list.get(0));
                    break;

                case Activity.RESULT_CANCELED:
                    Intent dat = new Intent();
                    setResult(RESULT_CANCELED, dat.putExtra("msg", false));
                    finish();
                    break;

                case AutoResolveHelper.RESULT_ERROR:
                    Status status = AutoResolveHelper.getStatusFromIntent(data);
                    Intent dat2 = new Intent();
                    setResult(RESULT_CANCELED, dat2.putExtra("msg", false));
                    finish();
                    break;
            }

            // Re-enables the Google Pay payment button.
            googlePayButton.setClickable(true);
        }
    }

    private void getStatus(String data) {
        boolean isPaymentCancelled = false;
        boolean isPaymentSuccess = false;
        boolean paymentFailed = false;
        String[] value = data.split("&");
        for (int i = 0; i < value.length; i++) {
            String[] checkString = value[i].split("=");
            if (checkString.length >= 2) {
                if (checkString[0].toLowerCase().equals("status")) {
                    isPaymentSuccess = true;
                } else {
                    paymentFailed = true;
                }
            } else {
                isPaymentCancelled = true;
            }
        }
        Intent dat = new Intent();
        if (isPaymentSuccess) {
            setResult(RESULT_OK, dat.putExtra("msg", true));
        } else if (isPaymentCancelled) {
            setResult(RESULT_CANCELED, dat.putExtra("msg", false));
        } else if (paymentFailed) {
            setResult(RESULT_CANCELED, dat.putExtra("msg", false));
        }
    }


    private void initializeUi() {
        layoutBinding = CheckoutAcitivityBinding.inflate(getLayoutInflater());
        setContentView(layoutBinding.getRoot());
        googlePayButton = layoutBinding.googlePayButton.getRoot();
        googlePayButton.setOnClickListener(view -> {
            Uri uri =
                    new Uri.Builder()
                            .scheme("upi")
                            .authority("pay")
                            .appendQueryParameter("pa", "xaidmetamorphos@oksbi")
                            .appendQueryParameter("pn", "TDE app")
                            .appendQueryParameter("mc", "your-merchant-code")
                            .appendQueryParameter("tr", "your-transaction-ref-id")
                            .appendQueryParameter("tn", "the minimum service pay")
                            .appendQueryParameter("am", String.valueOf(amount))
                            .appendQueryParameter("cu", "INR")
                            .build();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
            startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);
        });
    }


}
