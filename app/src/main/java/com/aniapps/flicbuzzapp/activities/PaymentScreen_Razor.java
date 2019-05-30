package com.aniapps.flicbuzzapp.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.aniapps.flicbuzzapp.R;
import com.aniapps.flicbuzzapp.networkcall.APIResponse;
import com.aniapps.flicbuzzapp.networkcall.RetrofitClient;
import com.aniapps.flicbuzzapp.utils.PrefManager;
import com.aniapps.flicbuzzapp.utils.Utility;
import com.google.gson.Gson;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import org.json.JSONObject;

import java.util.HashMap;

public class PaymentScreen_Razor extends AppCompatActivity implements PaymentResultWithDataListener {

    ConstraintLayout threemonths, sixmonths, oneyear;
    private final String TAG = "PaymentScreen_New";
    LinearLayout plan_details;
    TextView plan_text, plan_expiry_date;
    String subscription_id = "",subscription_ids_id="",transaction_id="",payment_details="";
    String payment_status="",plan="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_new);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView header_title = (TextView) findViewById(R.id.tvheader);
        header_title.setText("Packages");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        plan_details = (LinearLayout) findViewById(R.id.plan_details);
        plan_expiry_date = (TextView) findViewById(R.id.plan_expiry_text);
        // expirylabel = (TextView) findViewById(R.id.expirylabel);
        plan_text = (TextView) findViewById(R.id.plan_text);
        plan_expiry_date.setVisibility(View.GONE);
        //expirylabel.setVisibility(View.GONE);
        plan_details.setVisibility(View.VISIBLE);


        threemonths = (ConstraintLayout) findViewById(R.id.threemonths);
        sixmonths = (ConstraintLayout) findViewById(R.id.sixmonths);
        oneyear = (ConstraintLayout) findViewById(R.id.oneyear);

        threemonths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plan="3";
                planCall();
            }
        });
        sixmonths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plan="6";
                planCall();
            }
        });
        oneyear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plan="12";
                planCall();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public void alertDialog(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentScreen_Razor.this);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        //builder.setNegativeButton("NO", null);
        builder.show();
    }

    public void planCall() {
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "create_subscription2");
        params.put("plan", plan);

        ApiCall(params);
    }

    JSONObject jsonObject;

    public void ApiCall(final HashMap<String, String> params) {

        RetrofitClient.getInstance().doBackProcess(PaymentScreen_Razor.this, params, "", new APIResponse() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.e("res", "res:" + result);
                    jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        subscription_id = jsonObject.getString("subscription_id");
                        subscription_ids_id = jsonObject.getString("subscription_ids_id");
                        startPayment();
                    } else {
                        Utility.alertDialog(PaymentScreen_Razor.this, "Alert", jsonObject.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utility.alertDialog(PaymentScreen_Razor.this, "Alert", e.getMessage());
                }
            }

            @Override
            public void onFailure(String res) {
                Utility.alertDialog(PaymentScreen_Razor.this, "Alert", res);
            }
        });
    }
    public void transactionCall() {
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "update_transaction2");
        params.put("plan", plan);
        params.put("transaction_id", transaction_id);
        params.put("subscription_id", subscription_id);
        params.put("subscription_ids_id", subscription_ids_id);
        params.put("payment_data", payment_details);
        params.put("status", payment_status);

        transactionApiCall(params);
    }


    public void transactionApiCall(final HashMap<String, String> params) {

        RetrofitClient.getInstance().doBackProcess(PaymentScreen_Razor.this, params, "", new APIResponse() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.e("res", "res:" + result);
                    jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {

                    } else {
                        Utility.alertDialog(PaymentScreen_Razor.this, "Alert", jsonObject.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utility.alertDialog(PaymentScreen_Razor.this, "Alert", e.getMessage());
                }
            }

            @Override
            public void onFailure(String res) {
                Utility.alertDialog(PaymentScreen_Razor.this, "Alert", res);
            }
        });
    }

    public void startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "FlicBuzz");
            options.put("description", "Subscription Charges");
            // options.put("image", getResources().getDrawable(R.mipmap.ic_launcher));
            options.put("subscription_id", subscription_id);
            options.put("recurring", 1);

            JSONObject preFill = new JSONObject();
            preFill.put("email", PrefManager.getIn().getEmail());
            preFill.put("contact", PrefManager.getIn().getMobile());

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }


    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        try {
            transaction_id=paymentData.getPaymentId();
            payment_details=paymentData.getData().toString();
            payment_status="success";
            transactionCall();
            Log.e("res", s + "res" +  paymentData.getData());
            Toast.makeText(this, "Payment Successful: " +  paymentData.getData(), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        try {
            Log.e("res", s + "res" + paymentData.getData());
            Toast.makeText(this, "Payment Successful: " +  paymentData.getData(), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
        }
    }
}