package com.aniapps.flicbuzzapp.activities;


import android.app.Activity;
import android.app.Dialog;
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
import com.aniapps.flicbuzzapp.util.IabHelper;
import com.aniapps.flicbuzzapp.util.IabResult;
import com.aniapps.flicbuzzapp.util.Purchase;
import com.aniapps.flicbuzzapp.utils.PrefManager;
import com.aniapps.flicbuzzapp.utils.Utility;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class PaymentScreen_Razor extends AppCompatActivity implements PaymentResultWithDataListener {

    ConstraintLayout threemonths, sixmonths, oneyear;
    LinearLayout plan_details;
    TextView plan_text;
    String subscription_id = "", subscription_ids_id = "", transaction_id = "", payment_details = "";
    String payment_status = "", plan = "";
    public static IabHelper mHelper;
    private final int PURCHSE_REQUEST = 3;
    private final String TAG = "PaymentScreen_New";
    private boolean threemonthsflag, sixmonthsflag, oneyearflag, trailFlag;
    String subDate = "";
    String selection = "";

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

        mHelper = new IabHelper(PaymentScreen_Razor.this, Utility.sharedKey);
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
                    paymentDialog("3");

                    // planCall();
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
                    paymentDialog("6");

                    //planCall();
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
                    paymentDialog("12");
                    //planCall();
                }
            }
        });
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.e("limited", "In-app Billing is not set up OK");
                } else {
                    Log.v("Limites", "YAY, in app billing set up! " + result);

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
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    public void alertDialog(String title, String msg) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        final Dialog alert_dialog = new Dialog(PaymentScreen_Razor.this);
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


    public void paymentDialog(final String plan) {
        this.plan = plan;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        final Dialog alert_dialog = new Dialog(PaymentScreen_Razor.this);
        alert_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert_dialog.setContentView(R.layout.payment_dialog);
        alert_dialog.setCanceledOnTouchOutside(false);
        alert_dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView txt_cancel = (TextView) alert_dialog.findViewById(R.id.btn_cancel);
        LinearLayout google = (LinearLayout) alert_dialog.findViewById(R.id.lay_google);
        LinearLayout razor = (LinearLayout) alert_dialog.findViewById(R.id.lay_razor);
        txt_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alert_dialog.dismiss();
            }
        });
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert_dialog.dismiss();
                selection = "google";
                if (plan.equalsIgnoreCase("3")) {
                    mHelper.flagEndAsync();
                    mHelper.launchSubscriptionPurchaseFlow(PaymentScreen_Razor.this, Utility.threemonths, PURCHSE_REQUEST, mPurchaseFinishedListener, null);
                } else if (plan.equalsIgnoreCase("6")) {
                    mHelper.flagEndAsync();
                    mHelper.launchSubscriptionPurchaseFlow(PaymentScreen_Razor.this, Utility.six_months, PURCHSE_REQUEST, mPurchaseFinishedListener, null);
                } else if (plan.equalsIgnoreCase("12")) {
                    mHelper.flagEndAsync();
                    mHelper.launchSubscriptionPurchaseFlow(PaymentScreen_Razor.this, Utility.one_year, PURCHSE_REQUEST, mPurchaseFinishedListener, null);
                }
            }
        });
        razor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert_dialog.dismiss();
                selection = "razor";
                planCall();
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
                        PrefManager.getIn().setPayment_data(jsonObject.getString("payment_data"));
                        PrefManager.getIn().setSubscription_start_date(jsonObject.getString("subscription_start_date"));
                        PrefManager.getIn().setSubscription_end_date(jsonObject.getString("subscription_end_date"));
                        PrefManager.getIn().setPayment_mode(jsonObject.getString("payment_mode"));
                        PrefManager.getIn().setPlan(jsonObject.getString("plan"));
                        PrefManager.getIn().setDeveloper_mode(jsonObject.getString("developer_mode"));
                        PrefManager.getIn().setServer_version_mode(jsonObject.getString("server_version_mode"));
                        PrefManager.getIn().setShow_splash_message(jsonObject.getString("show_splash_message"));
                        PrefManager.getIn().setSplash_message(jsonObject.getString("splash_message"));
                        PrefManager.getIn().setGateway("razorpay");
                        PrefManager.getIn().setSubscription_auto_renew("yes");
                        Intent intent = new Intent(PaymentScreen_Razor.this, PaymentSuccuss.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
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
        } catch (Exception e) {
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {

            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }


    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

            if (mHelper == null) return;

            if (result.isFailure()) {
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                return;
            }

            Log.d(TAG, "Purchase successful.");

            // Toast.makeText(PaymentScreen_New.this, purchase.toString() + "::" + purchase.getPurchaseTime(), Toast.LENGTH_SHORT).show();
            Log.e("", "" + purchase);
            //2019-04-20 21:54:06
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(purchase.getPurchaseTime());
            String startdate = sdf.format(date);
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(sdf.parse(startdate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (purchase.getSku().equals(Utility.threemonths)) {
                c.add(Calendar.MONTH, 3);
            } else if (purchase.getSku().equals(Utility.six_months)) {
                c.add(Calendar.MONTH, 6);
            } else if (purchase.getSku().equals(Utility.one_year)) {
                c.add(Calendar.MONTH, 12);
            }
            String endDate = sdf.format(c.getTime());
            planUpdate(purchase.getSku(), startdate, endDate, purchase.toString(), 0);


        }
    };

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        Log.e("", "" + payload);
        return true;
    }

    public void planUpdate(String package_data, String sub_start_date, String sub_end_date, String payment_data, int renewal) {
        HashMap<String, String> params = new HashMap<>();
        params.put("subscription_start_date", sub_start_date);
        params.put("subscription_end_date", sub_end_date);
        params.put("payment_data", payment_data);
        params.put("action", "update_package");
        if (package_data.equals(Utility.threemonths_threedaytrail)) {
            params.put("package", "3");
        } else if (package_data.equals(Utility.threemonths)) {
            params.put("package", "3");
        } else if (package_data.equals(Utility.six_months)) {
            params.put("package", "6");
        } else if (package_data.equals(Utility.one_year)) {
            params.put("package", "12");
        } else if (package_data.equals("trail")) {
            params.put("package", "trail");
        } else if (package_data.equals("expired")) {
            params.put("package", "expired");
        }
        params.put("language", PrefManager.getIn().getLanguage().toLowerCase());
        ApiCall(params, renewal);
    }


    public void ApiCall(final HashMap<String, String> params, final int renewal) {

        RetrofitClient.getInstance().doBackProcess(PaymentScreen_Razor.this, params, renewal == 0 ? "" : "online", new APIResponse() {
            @Override
            public void onSuccess(String result) {
                try {
                    jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        if (params.get("package").equals("expired")) {
                            threemonthsflag = false;
                            sixmonthsflag = false;
                            oneyearflag = false;
                            trailFlag = false;
                        }
                        PrefManager.getIn().setPayment_data(params.get("payment_data"));
                        PrefManager.getIn().setSubscription_start_date(params.get("subscription_start_date"));
                        PrefManager.getIn().setSubscription_renewal_date(params.get("subscription_renewal_date"));
                        PrefManager.getIn().setSubscription_end_date(params.get("subscription_end_date"));
                        PrefManager.getIn().setGateway("googlepay");
                        PrefManager.getIn().setSubscription_auto_renew("yes");
                        try {
                            PrefManager.getIn().setSplash_message(jsonObject.getString("splash_message"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        PrefManager.getIn().setPlan(params.get("package"));
                        if (PrefManager.getIn().getPlan().equals("3")) {
                            threemonthsflag = true;
                            PrefManager.getIn().setPayment_mode("1");
                            plan_text.setText(jsonObject.getString("splash_message"));
                        } else if (PrefManager.getIn().getPlan().equals("6")) {
                            PrefManager.getIn().setPayment_mode("1");
                            plan_text.setText(jsonObject.getString("splash_message"));
                            // expirylabel.setVisibility(View.VISIBLE);
                            sixmonthsflag = true;
                        } else if (PrefManager.getIn().getPlan().equals("12")) {
                            PrefManager.getIn().setPayment_mode("1");
                            plan_text.setText(jsonObject.getString("splash_message"));
                            oneyearflag = true;
                            // expirylabel.setVisibility(View.VISIBLE);
                        } else if (PrefManager.getIn().getPlan().equals("expired")) {
                            PrefManager.getIn().setPayment_mode("3");
                            plan_text.setText(jsonObject.getString("splash_message"));
                            //expirylabel.setVisibility(View.GONE);
                        } else if (PrefManager.getIn().getPlan().equals("trail")) {
                            PrefManager.getIn().setPayment_mode("1");
                            plan_text.setText(jsonObject.getString("splash_message"));
                            //expirylabel.setVisibility(View.VISIBLE);
                            trailFlag = true;
                        }
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date date = format.parse(PrefManager.getIn().getSubscription_end_date());
                            SimpleDateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm a");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //if (renewal == 0) {
                        if (!PrefManager.getIn().getPlan().equals("expired")) {
                            Intent intent = new Intent(PaymentScreen_Razor.this, PaymentSuccuss.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                        }
                        // }
                    } else if (status == 14) {
                        Utility.alertDialog(PaymentScreen_Razor.this, jsonObject.getString("message"));
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


}