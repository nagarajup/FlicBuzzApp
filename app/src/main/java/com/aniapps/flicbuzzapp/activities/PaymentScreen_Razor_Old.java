package com.aniapps.flicbuzzapp.activities;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.aniapps.flicbuzzapp.R;
import com.aniapps.flicbuzzapp.networkcall.APIResponse;
import com.aniapps.flicbuzzapp.networkcall.RetrofitClient;
import com.aniapps.flicbuzzapp.player.LandingPage;
import com.aniapps.flicbuzzapp.utils.PrefManager;
import com.aniapps.flicbuzzapp.utils.Utility;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import org.json.JSONObject;

import java.util.HashMap;

public class PaymentScreen_Razor_Old extends AppCompatActivity implements PaymentResultWithDataListener {

    ConstraintLayout threemonths, sixmonths, oneyear;
    private final String TAG = "PaymentScreen_New";
    LinearLayout plan_details;
    TextView plan_text;
    String subscription_id = "", subscription_ids_id = "", transaction_id = "", payment_details = "";
    String payment_status = "", plan = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_razor);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView header_title = (TextView) findViewById(R.id.tvheader);
        header_title.setText("Packages");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        plan_details = (LinearLayout) findViewById(R.id.plan_details);
        plan_text = (TextView) findViewById(R.id.plan_text);
        plan_details.setVisibility(View.VISIBLE);


        threemonths = (ConstraintLayout) findViewById(R.id.threemonths);
        sixmonths = (ConstraintLayout) findViewById(R.id.sixmonths);
        oneyear = (ConstraintLayout) findViewById(R.id.oneyear);
        plan_text.setText(PrefManager.getIn().getSplash_message());
        threemonths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PrefManager.getIn().getPayment_mode().equals("1") && !PrefManager.getIn().getPlan().equalsIgnoreCase("trail")) {
                    String message = "";
                    if (PrefManager.getIn().getPlan().equals("3")) {
                        message = "Three Months";
                    } else if (PrefManager.getIn().getPlan().equals("6")) {
                        message = "Six Months";
                    } else if (PrefManager.getIn().getPlan().equals("12")) {
                        message = "One Year";
                    }
                    alertDialog("Subscription", "You already subscribed for " + message + " Plan.");
                } else {
                    plan = "3";
                    planCall();
                }
            }
        });
        sixmonths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PrefManager.getIn().getPayment_mode().equals("1") && !PrefManager.getIn().getPlan().equalsIgnoreCase("trail")) {
                    String message = "";
                    if (PrefManager.getIn().getPlan().equals("3")) {
                        message = "Three Months";
                    } else if (PrefManager.getIn().getPlan().equals("6")) {
                        message = "Six Months";
                    } else if (PrefManager.getIn().getPlan().equals("12")) {
                        message = "One Year";
                    }
                    alertDialog("Subscription", "You already subscribed for " + message + " Plan.");
                } else {
                    plan = "6";
                    planCall();
                }
            }
        });
        oneyear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PrefManager.getIn().getPayment_mode().equals("1") && !PrefManager.getIn().getPlan().equalsIgnoreCase("trail")) {
                    String message = "";
                    if (PrefManager.getIn().getPlan().equals("3")) {
                        message = "Three Months";
                    } else if (PrefManager.getIn().getPlan().equals("6")) {
                        message = "Six Months";
                    } else if (PrefManager.getIn().getPlan().equals("12")) {
                        message = "One Year";
                    }
                    alertDialog("Subscription", "You already subscribed for " + message + " Plan.");
                } else {
                    plan = "12";
                    planCall();
                }
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
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        final Dialog alert_dialog = new Dialog(PaymentScreen_Razor_Old.this);
        alert_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert_dialog.setContentView(R.layout.alert_dialog);
        alert_dialog.setCanceledOnTouchOutside(false);
        alert_dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView txt_alert_title = (TextView) alert_dialog.findViewById(R.id.custom_alert_dialog_title);
        txt_alert_title.setText(title);
        TextView txt_alert_description = (TextView) alert_dialog.findViewById(R.id.custom_alert_dialog_msg);
        LinearLayout main = (LinearLayout) alert_dialog.findViewById(R.id.main);
        Button txt_ok = (Button) alert_dialog.findViewById(R.id.ok);
        txt_ok.setVisibility(View.GONE);
        Button txt_cancel = (Button) alert_dialog.findViewById(R.id.cancel);
        main.setVisibility(View.VISIBLE);
        txt_alert_description.setText(msg);
        txt_cancel.setText("OK");
        txt_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alert_dialog.dismiss();

            }
        });

        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert_dialog.dismiss();

            }
        });

        alert_dialog.show();
    }

    public void planCall() {
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "create_subscription2");
        params.put("plan", plan);

        ApiCall(params);
    }

    JSONObject jsonObject;

    public void ApiCall(final HashMap<String, String> params) {

        RetrofitClient.getInstance().doBackProcess(PaymentScreen_Razor_Old.this, params, "", new APIResponse() {
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
                        Utility.alertDialog(PaymentScreen_Razor_Old.this, "Alert", jsonObject.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utility.alertDialog(PaymentScreen_Razor_Old.this, "Alert", e.getMessage());
                }
            }

            @Override
            public void onFailure(String res) {
                Utility.alertDialog(PaymentScreen_Razor_Old.this, "Alert", res);
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

        RetrofitClient.getInstance().doBackProcess(PaymentScreen_Razor_Old.this, params, "", new APIResponse() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.e("res", "res:" + result);
                    jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        PrefManager.getIn().setPayment_data(jsonObject.getString("payment_data"));
                        PrefManager.getIn().setSubscription_start_date(jsonObject.getString("subscription_start_date"));
                        PrefManager.getIn().setSubscription_end_date(jsonObject.getString("subscription_end_date"));
                        PrefManager.getIn().setPayment_mode(jsonObject.getString("payment_mode"));
                        PrefManager.getIn().setPlan(jsonObject.getString("plan"));
                        PrefManager.getIn().setDeveloper_mode(jsonObject.getString("developer_mode"));
                        PrefManager.getIn().setServer_version_mode(jsonObject.getString("server_version_mode"));
                        PrefManager.getIn().setShow_splash_message(jsonObject.getString("show_splash_message"));
                        PrefManager.getIn().setSplash_message(jsonObject.getString("splash_message"));
                        PrefManager.getIn().setSubscription_auto_renew(jsonObject.getString("subscription_auto_renew"));
                        Intent intent = new Intent(PaymentScreen_Razor_Old.this, LandingPage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                    } else {
                        Utility.alertDialog(PaymentScreen_Razor_Old.this, "Alert", jsonObject.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utility.alertDialog(PaymentScreen_Razor_Old.this, "Alert", e.getMessage());
                }
            }

            @Override
            public void onFailure(String res) {
                Utility.alertDialog(PaymentScreen_Razor_Old.this, "Alert", res);
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
            transaction_id = paymentData.getPaymentId();
            payment_details = paymentData.getData().toString();
            payment_status = "success";
            transactionCall();
           // Log.e("res", s + "res" + paymentData.getData());
           // Toast.makeText(this, "Payment Successful: " + paymentData.getData(), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        try {
            Log.e("res", s + "res" + paymentData.getData());
           // Toast.makeText(this, "Payment Successful: " + paymentData.getData(), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
        }
    }
}