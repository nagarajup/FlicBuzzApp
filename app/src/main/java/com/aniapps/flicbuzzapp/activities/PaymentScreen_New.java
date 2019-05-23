package com.aniapps.flicbuzzapp.activities;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.aniapps.flicbuzzapp.R;
import com.aniapps.flicbuzzapp.networkcall.APIResponse;
import com.aniapps.flicbuzzapp.networkcall.RetrofitClient;
import com.aniapps.flicbuzzapp.player.LandingPage;
import com.aniapps.flicbuzzapp.util.IabHelper;
import com.aniapps.flicbuzzapp.util.IabResult;
import com.aniapps.flicbuzzapp.util.Inventory;
import com.aniapps.flicbuzzapp.util.Purchase;
import com.aniapps.flicbuzzapp.utils.PrefManager;
import com.aniapps.flicbuzzapp.utils.Utility;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PaymentScreen_New extends AppCompatActivity {

    ConstraintLayout threemonths, sixmonths, oneyear;
    public static IabHelper mHelper;
    private final int PURCHSE_REQUEST = 3;
    private final String TAG = "PaymentScreen_New";
    private boolean threemonthsflag, sixmonthsflag, oneyearflag, trailFlag;
    LinearLayout plan_details;
    TextView plan_text, plan_expiry_date;
    String subDate = "";


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
        if (PrefManager.getIn().getShow_splash_message().equalsIgnoreCase("yes")) {
            plan_text.setText(PrefManager.getIn().getSplash_message());
            plan_expiry_date.setVisibility(View.GONE);
           // expirylabel.setVisibility(View.GONE);
        }
        if (!PrefManager.getIn().getPlan().equals("") && !PrefManager.getIn().getPlan().equalsIgnoreCase("expired")) {
            plan_expiry_date.setVisibility(View.VISIBLE);
           // expirylabel.setVisibility(View.VISIBLE);
            if (PrefManager.getIn().getPlan().equals("3")) {
                plan_text.setText("Three Months Subscription");
            } else if (PrefManager.getIn().getPlan().equals("6")) {
                plan_text.setText("Six Months Subscription");
            } else if (PrefManager.getIn().getPlan().equals("12")) {
                plan_text.setText("One Year Subscription");
            } else if (PrefManager.getIn().getPlan().equals("expired")) {
                plan_text.setText("Plan Expired");
            } else if (PrefManager.getIn().getPlan().equals("trail")) {
                plan_text.setText("Trail");
            }
            plan_expiry_date.setText(PrefManager.getIn().getSplash_message());
           /* SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = format.parse(PrefManager.getIn().getSubscription_end_date());
                SimpleDateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm a");
                plan_expiry_date.setText(simple.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }*/
        }

        threemonths = (ConstraintLayout) findViewById(R.id.threemonths);
        sixmonths = (ConstraintLayout) findViewById(R.id.sixmonths);
        oneyear = (ConstraintLayout) findViewById(R.id.oneyear);
        mHelper = new IabHelper(PaymentScreen_New.this, Utility.sharedKey);
        if (PrefManager.getIn().getPlan().equals("3")) {
            threemonthsflag = true;
        } else if (PrefManager.getIn().getPlan().equals("6")) {
            sixmonthsflag = true;
        } else if (PrefManager.getIn().getPlan().equals("12")) {
            oneyearflag = true;
        } else if (PrefManager.getIn().getPlan().equalsIgnoreCase("trail")) {
            trailFlag = true;
        }
        threemonths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!trailFlag) {
                    if (!threemonthsflag) {
                        if (oneyearflag) {
                            alertDialog("Subscription", "You already subscribed for One Year Plan.");
                        } else {
                            if (sixmonthsflag) {
                                alertDialog("Subscription", "You already subscribed for Six Months Plan.");
                            } else {
                                Map<String, Object> eventValue = new HashMap<String, Object>();
                                eventValue.put(AFInAppEventParameterName.REVENUE, 190);
                                eventValue.put(AFInAppEventParameterName.CONTENT_TYPE, "Three Months Subscription");
                                eventValue.put(AFInAppEventParameterName.CONTENT_ID, "Package 1");
                                eventValue.put(AFInAppEventParameterName.CURRENCY, "INR");
                                AppsFlyerLib.getInstance().trackEvent(getApplicationContext(), AFInAppEventType.PURCHASE, eventValue);

                               // if (PrefManager.getIn().getDeveloper_mode().equalsIgnoreCase("0")) {
                                    mHelper.flagEndAsync();
                                    mHelper.launchSubscriptionPurchaseFlow(PaymentScreen_New.this, Utility.threemonths, PURCHSE_REQUEST, mPurchaseFinishedListener, null);
                                /*} else {
                                    mHelper.flagEndAsync();
                                    mHelper.launchSubscriptionPurchaseFlow(PaymentScreen_New.this, Utility.threemonths_threedaytrail, PURCHSE_REQUEST, mPurchaseFinishedListener, null);
                                }*/
                            }
                        }
                    } else {
                        alertDialog("Subscription", "You already subscribed for Three Months Plan, No need to buy again.");
                    }
                } else {
                    alertDialog("Subscription", "You already in trail Period, Please wait until trial period completion.");
                }
            }
        });
        sixmonths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!trailFlag) {
                    if (!sixmonthsflag) {
                        if (oneyearflag) {
                            alertDialog("Subscription", "You already subscribed for One Year Plan.");
                        } else {
                            if (threemonthsflag) {
                                alertDialog("Subscription", "You already subscribed for Three Months Plan.");
                            } else {
                                Map<String, Object> eventValue = new HashMap<String, Object>();
                                eventValue.put(AFInAppEventParameterName.REVENUE, 280);
                                eventValue.put(AFInAppEventParameterName.CONTENT_TYPE, "Six Months Subscription");
                                eventValue.put(AFInAppEventParameterName.CONTENT_ID, "Package 2");
                                eventValue.put(AFInAppEventParameterName.CURRENCY, "INR");
                                AppsFlyerLib.getInstance().trackEvent(getApplicationContext(), AFInAppEventType.PURCHASE, eventValue);
                                mHelper.flagEndAsync();
                                mHelper.launchSubscriptionPurchaseFlow(PaymentScreen_New.this, Utility.six_months, PURCHSE_REQUEST, mPurchaseFinishedListener, null);
                            }
                        }
                    } else {
                        alertDialog("Subscription", "You already subscribed for Six Months Plan, No need to buy again.");
                    }
                } else {
                    alertDialog("Subscription", "You already in trail Period, Please wait until trial period completion.");
                }
            }
        });
        oneyear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!trailFlag) {
                    if (!oneyearflag) {
                        if (sixmonthsflag) {
                            alertDialog("Subscription", "You already subscribed for Six Months Plan.");
                        } else {
                            if (threemonthsflag) {
                                alertDialog("Subscription", "You already subscribed for Three Months Plan.");
                            } else {
                                Map<String, Object> eventValue = new HashMap<String, Object>();
                                eventValue.put(AFInAppEventParameterName.REVENUE, 460);
                                eventValue.put(AFInAppEventParameterName.CONTENT_TYPE, "One Year Subscription");
                                eventValue.put(AFInAppEventParameterName.CONTENT_ID, "Package 3");
                                eventValue.put(AFInAppEventParameterName.CURRENCY, "INR");
                                AppsFlyerLib.getInstance().trackEvent(getApplicationContext(), AFInAppEventType.PURCHASE, eventValue);
                                mHelper.flagEndAsync();
                                mHelper.launchSubscriptionPurchaseFlow(PaymentScreen_New.this, Utility.one_year, PURCHSE_REQUEST, mPurchaseFinishedListener, null);
                            }
                        }
                    } else {
                        alertDialog("Subscription", "You already subscribed for One year Plan, No need to buy again.");
                    }
                } else {
                    alertDialog("Subscription", "You already in trail Period, Please wait until trial period completion.");
                }
            }
        });

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.e("limited", "In-app Billing is not set up OK");
                } else {
                    Log.v("Limites", "YAY, in app billing set up! " + result);
                    if (PrefManager.getIn().getPayment_mode().equals("3")) {
                        if (Utility.getMilliSeconds(PrefManager.getIn().getSubscription_end_date()) > Utility.getMilliSeconds(PrefManager.getIn().getServer_date_time())) {
                            mHelper.queryInventoryAsync(mGotInventoryListener); //Getting inventory of purchases and assigning listener
                        }
                    }
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //if (!PrefManager.getIn().getPlan().equals("expired")) {
        finish();
        overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
       /* } else {
            Intent intent = new Intent(PaymentScreen_New.this, PaymentScreen_New.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            finish();
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {


            if (result.isFailure()) {
                // handle error here

                Log.v(TAG, "failure in checking if user has purchases");
            } else {
                consumeItem();
                PrefManager.getIn().setPackage(false);
                // does the user have the premium upgrade?
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar calender = Calendar.getInstance();
                Calendar calenderend = Calendar.getInstance();
                Date start_date = null;
                Date end_date = null;
                boolean trailFlag = false;
                try {
                    start_date = sdf.parse(PrefManager.getIn().getSubscription_start_date());
                    end_date = sdf.parse(PrefManager.getIn().getSubscription_end_date());
                    calender.setTime(start_date);
                   // if (PrefManager.getIn().getDeveloper_mode().equalsIgnoreCase("0")) {
                        calender.add(Calendar.DATE, 7);
                   /* } else {
                        calender.add(Calendar.DATE, 3);
                    }*/
                    calenderend.setTime(end_date);
                    if (calender.getTimeInMillis() == calenderend.getTimeInMillis()) {
                        trailFlag = true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                // if (PrefManager.getIn().getPlan().equals("3")) {
//                if (inventory.hasPurchase(Utility.threemonths_threedaytrail)) {
//
//                    if (PrefManager.getIn().getPayment_mode().equals("3")) {
//
//                        try {
//                            start_date = sdf.parse(PrefManager.getIn().getSubscription_start_date());
//                            subDate = sdf.format(start_date);
//                            threemonthsflag = true;
//                            Calendar c = Calendar.getInstance();
//                            c.setTime(start_date);
//                            Calendar c1 = Calendar.getInstance();
//                            if (!trailFlag) {
//                                c.add(Calendar.MONTH, 3);
//                                c1.setTime(start_date);
//                                c1.add(Calendar.MONTH, 6);
//                            } else {
//                                c1.setTime(start_date);
//                                c1.add(Calendar.MONTH, 3);
//                            }
//                            String endDate1 = sdf.format(c.getTime());
//                            String endDate2 = sdf.format(c1.getTime());
//                            planUpdate(inventory.getPurchase(Utility.threemonths_threedaytrail).getSku(), endDate1, endDate2, inventory.getPurchase(Utility.threemonths_threedaytrail).toString(), 1);
//
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } else
                if (inventory.hasPurchase(Utility.threemonths)) {

                    if (PrefManager.getIn().getPayment_mode().equals("3")) {

                        try {
                            start_date = sdf.parse(PrefManager.getIn().getSubscription_start_date());
                            subDate = sdf.format(start_date);
                            threemonthsflag = true;
                            Calendar c = Calendar.getInstance();
                            c.setTime(start_date);
                            Calendar c1 = Calendar.getInstance();
                            if (!trailFlag) {
                                c.add(Calendar.MONTH, 3);
                                c1.setTime(start_date);
                                c1.add(Calendar.MONTH, 6);
                            } else {
                                c1.setTime(start_date);
                                c1.add(Calendar.MONTH, 3);
                            }
                            String endDate1 = sdf.format(c.getTime());
                            String endDate2 = sdf.format(c1.getTime());
                            planUpdate(inventory.getPurchase(Utility.threemonths).getSku(), endDate1, endDate2, inventory.getPurchase(Utility.threemonths).toString(), 1);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } /*else {
                        plan_text.setText("Plan Expired");
                        plan_expiry_date.setVisibility(View.GONE);
                        expirylabel.setVisibility(View.GONE);
                        planUpdate("expired", PrefManager.getIn().getSubscription_start_date(), PrefManager.getIn().getSubscription_end_date(), PrefManager.getIn().getPayment_data(), 1);
                    }*/
                //  } else if (PrefManager.getIn().getPlan().equals("6")) {
                else if (inventory.hasPurchase(Utility.six_months)) {
                    // if (Utility.getMilliSeconds(PrefManager.getIn().getSubscription_end_date()) < calender.getTimeInMillis()) {
                    if (PrefManager.getIn().getPayment_mode().equals("3")) {
                        try {
                            start_date = sdf.parse(PrefManager.getIn().getSubscription_start_date());
                            subDate = sdf.format(start_date);
                            threemonthsflag = true;
                            Calendar c = Calendar.getInstance();
                            c.setTime(start_date);
                            Calendar c1 = Calendar.getInstance();
                            if (!trailFlag) {
                                c.add(Calendar.MONTH, 6);
                                c1.setTime(start_date);
                                c1.add(Calendar.MONTH, 12);
                            } else {
                                c1.setTime(start_date);
                                c1.add(Calendar.MONTH, 6);
                            }

                               /* c.add(Calendar.MONTH, 6);
                                Calendar c1 = Calendar.getInstance();
                                c1.setTime(start_date);
                                c1.add(Calendar.MONTH, 12);*/
                            String endDate1 = sdf.format(c.getTime());
                            String endDate2 = sdf.format(c1.getTime());
                            planUpdate(inventory.getPurchase(Utility.six_months).getSku(), endDate1, endDate2, inventory.getPurchase(Utility.six_months).toString(), 1);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } /*else {
                        plan_text.setText("Plan Expired");
                        plan_expiry_date.setVisibility(View.GONE);
                        planUpdate("expired", PrefManager.getIn().getSubscription_start_date(), PrefManager.getIn().getSubscription_end_date(), PrefManager.getIn().getPayment_data(), 1);
                    }*/
                // } else if (PrefManager.getIn().getPlan().equals("12")) {
                else if (inventory.hasPurchase(Utility.one_year)) {
                    //  if (Utility.getMilliSeconds(PrefManager.getIn().getSubscription_end_date()) < calender.getTimeInMillis()) {
                    if (PrefManager.getIn().getPayment_mode().equals("3")) {
                        try {
                            start_date = sdf.parse(PrefManager.getIn().getSubscription_start_date());
                            subDate = sdf.format(start_date);
                            threemonthsflag = true;
                            Calendar c = Calendar.getInstance();
                            c.setTime(start_date);

                            Calendar c1 = Calendar.getInstance();
                            if (!trailFlag) {
                                c.add(Calendar.MONTH, 12);
                                c1.setTime(start_date);
                                c1.add(Calendar.MONTH, 24);
                            } else {
                                c1.setTime(start_date);
                                c1.add(Calendar.MONTH, 12);
                            }


                                /*c.add(Calendar.MONTH, 12);
                                Calendar c1 = Calendar.getInstance();
                                c1.setTime(start_date);
                                c1.add(Calendar.MONTH, 24);*/
                            String endDate1 = sdf.format(c.getTime());
                            String endDate2 = sdf.format(c1.getTime());
                            planUpdate(inventory.getPurchase(Utility.one_year).getSku(), endDate1, endDate2, inventory.getPurchase(Utility.one_year).toString(), 1);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    plan_text.setText("Plan Expired");
                    plan_expiry_date.setVisibility(View.GONE);
                    planUpdate("expired", PrefManager.getIn().getSubscription_start_date(), PrefManager.getIn().getSubscription_end_date(), PrefManager.getIn().getPayment_data(), 1);
                }
                //}
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }


    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            //  Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
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
            if (PrefManager.getIn().getSubscription_start_date().equals("0000-00-00 00:00:00")) {
              //  if (PrefManager.getIn().getDeveloper_mode().equalsIgnoreCase("0")) {
                    c.add(Calendar.DATE, 7);
               /* } else {
                    c.add(Calendar.DATE, 3);
                }*/
                String endDate = sdf.format(c.getTime());
                planUpdate("trail", startdate, endDate, purchase.toString(), 0);
            } else {
               /* if (purchase.getSku().equals(Utility.threemonths_threedaytrail)) {
                    c.add(Calendar.MONTH, 3);
                } else*/ if (purchase.getSku().equals(Utility.threemonths)) {
                    c.add(Calendar.MONTH, 3);
                } else if (purchase.getSku().equals(Utility.six_months)) {
                    c.add(Calendar.MONTH, 6);
                } else if (purchase.getSku().equals(Utility.one_year)) {
                    c.add(Calendar.MONTH, 12);
                }
                String endDate = sdf.format(c.getTime());
                planUpdate(purchase.getSku(), startdate, endDate, purchase.toString(), 0);
            }


        }
    };

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        Log.e("", "" + payload);
        return true;
    }

    public void alertDialog(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentScreen_New.this);
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
        plan_expiry_date.setText("Expires On "+sub_end_date);
        ApiCall(params, renewal);
    }

    JSONObject jsonObject;

    public void ApiCall(final HashMap<String, String> params, final int renewal) {

        RetrofitClient.getInstance().doBackProcess(PaymentScreen_New.this, params, renewal == 0 ? "" : "online", new APIResponse() {
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
                        try {
                            PrefManager.getIn().setSplash_message(jsonObject.getString("splash_message"));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        PrefManager.getIn().setPlan(params.get("package"));
                        if (PrefManager.getIn().getPlan().equals("3")) {
                            threemonthsflag = true;
                            PrefManager.getIn().setPayment_mode("1");
                            plan_text.setText("Three Months Subscription");
                            plan_expiry_date.setVisibility(View.VISIBLE);
                            //expirylabel.setVisibility(View.VISIBLE);
                        } else if (PrefManager.getIn().getPlan().equals("6")) {
                            PrefManager.getIn().setPayment_mode("1");
                            plan_text.setText("Six Months Subscription");
                            plan_expiry_date.setVisibility(View.VISIBLE);
                           // expirylabel.setVisibility(View.VISIBLE);
                            sixmonthsflag = true;
                        } else if (PrefManager.getIn().getPlan().equals("12")) {
                            PrefManager.getIn().setPayment_mode("1");
                            plan_text.setText("One Year Subscription");
                            oneyearflag = true;
                            plan_expiry_date.setVisibility(View.VISIBLE);
                           // expirylabel.setVisibility(View.VISIBLE);
                        } else if (PrefManager.getIn().getPlan().equals("expired")) {
                            PrefManager.getIn().setPayment_mode("3");
                            plan_text.setText("Plan Expired");
                            plan_expiry_date.setVisibility(View.GONE);
                            //expirylabel.setVisibility(View.GONE);
                        } else if (PrefManager.getIn().getPlan().equals("trail")) {
                            PrefManager.getIn().setPayment_mode("1");
                            plan_text.setText("Trail");
                            plan_expiry_date.setVisibility(View.VISIBLE);
                            //expirylabel.setVisibility(View.VISIBLE);
                            trailFlag = true;
                        }
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date date = format.parse(PrefManager.getIn().getSubscription_end_date());
                            SimpleDateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm a");
                            plan_expiry_date.setText("Expires On "+simple.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //if (renewal == 0) {
                        if (!PrefManager.getIn().getPlan().equals("expired")) {
                            Intent intent = new Intent(PaymentScreen_New.this, LandingPage.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                        }
                       // }
                    }else if(status==14){
                        Utility.alertDialog(PaymentScreen_New.this,  jsonObject.getString("message"));
                    } else {
                        Utility.alertDialog(PaymentScreen_New.this, "Alert", jsonObject.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utility.alertDialog(PaymentScreen_New.this, "Alert", e.getMessage());
                }
            }

            @Override
            public void onFailure(String res) {
                Utility.alertDialog(PaymentScreen_New.this, "Alert", res);
            }
        });
    }

    public void consumeItem() {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {


            if (result.isFailure()) {
                // Handle failure
            }

        }
    };


}