package com.aniapps.flicbuzz.activities;


import android.app.Activity;
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
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.aniapps.flicbuzz.R;
import com.aniapps.flicbuzz.networkcall.APIResponse;
import com.aniapps.flicbuzz.networkcall.RetrofitClient;
import com.aniapps.flicbuzz.player.LandingPage;
import com.aniapps.flicbuzz.util.IabHelper;
import com.aniapps.flicbuzz.util.IabResult;
import com.aniapps.flicbuzz.util.Inventory;
import com.aniapps.flicbuzz.util.Purchase;
import com.aniapps.flicbuzz.utils.PrefManager;
import com.aniapps.flicbuzz.utils.Utility;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class PaymentScreen_New extends AppCompatActivity {
    ConstraintLayout threemonths, sixmonths, oneyear;
    public static IabHelper mHelper;
    private final int PURCHSE_REQUEST = 3;
    private final String TAG = "PaymentScreen_New";
    private boolean threemonthsflag, sixmonthsflag, oneyearflag;
    LinearLayout plan_details;
    TextView plan_text, plan_expiry_date;
    String subDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Making notification bar transparent

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_payment_new);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.mytoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Packages");

        plan_details = (LinearLayout) findViewById(R.id.plan_details);
        plan_expiry_date = (TextView) findViewById(R.id.plan_expiry_text);
        plan_text = (TextView) findViewById(R.id.plan_text);

        if (!PrefManager.getIn().getPlan().equals("") && !PrefManager.getIn().getPlan().equalsIgnoreCase("expired") && !PrefManager.getIn().getPlan().equalsIgnoreCase("trail")) {
            plan_details.setVisibility(View.VISIBLE);
            if (PrefManager.getIn().getPlan().equals("3")) {
                plan_text.setText("Three Months Subscription");
            } else if (PrefManager.getIn().getPlan().equals("6")) {
                plan_text.setText("Six Months Subscription");
            } else if (PrefManager.getIn().getPlan().equals("12")) {
                plan_text.setText("One Year Subscription");
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = format.parse(PrefManager.getIn().getSubscription_end_date());
                SimpleDateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm a");
                plan_expiry_date.setText(simple.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        threemonths = (ConstraintLayout) findViewById(R.id.threemonths);
        sixmonths = (ConstraintLayout) findViewById(R.id.sixmonths);
        oneyear = (ConstraintLayout) findViewById(R.id.oneyear);
        mHelper = new IabHelper(PaymentScreen_New.this, Utility.sharedKey);
        if(PrefManager.getIn().getPlan().equals("3")){
            threemonthsflag=true;
        }else if(PrefManager.getIn().getPlan().equals("6")){
            sixmonthsflag=true;
        }else if(PrefManager.getIn().getPlan().equals("12")){
            oneyearflag=true;
        }
        threemonths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!threemonthsflag) {
                    if (oneyearflag) {
                        alertDialog("Subscription", "You already subscribed for One Year Plan.");
                    } else {
                        if (sixmonthsflag) {
                            alertDialog("Subscription", "You already subscribed for Six Months Plan.");
                        } else {
                            mHelper.flagEndAsync();
                            mHelper.launchSubscriptionPurchaseFlow(PaymentScreen_New.this, Utility.threemonths, PURCHSE_REQUEST, mPurchaseFinishedListener, null);
                        }
                    }
                } else {
                    alertDialog("Subscription", "You already subscribed for Three Months Plan, No need to buy again.");
                }
            }
        });
        sixmonths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sixmonthsflag) {
                    if (oneyearflag) {
                        alertDialog("Subscription", "You already subscribed for One Year Plan.");
                    } else {
                        if (threemonthsflag) {
                            alertDialog("Subscription", "You already subscribed for Three Months Plan.");
                        } else {
                            mHelper.flagEndAsync();
                            mHelper.launchSubscriptionPurchaseFlow(PaymentScreen_New.this, Utility.six_months, PURCHSE_REQUEST, mPurchaseFinishedListener, null);
                        }
                    }
                } else {
                    alertDialog("Subscription", "You already subscribed for Six Months Plan, No need to buy again.");
                }
            }
        });
        oneyear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!oneyearflag) {
                    if (sixmonthsflag) {
                        alertDialog("Subscription", "You already subscribed for Six Months Plan.");
                    } else {
                        if (threemonthsflag) {
                            alertDialog("Subscription", "You already subscribed for Three Months Plan.");
                        } else {
                            mHelper.flagEndAsync();
                            mHelper.launchSubscriptionPurchaseFlow(PaymentScreen_New.this, Utility.one_year, PURCHSE_REQUEST, mPurchaseFinishedListener, null);
                        }
                    }
                } else {
                    alertDialog("Subscription", "You already subscribed for One year Plan, No need to buy again.");
                }
            }
        });
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.e("limited", "In-app Billing is not set up OK");
                } else {
                    Log.v("Limites", "YAY, in app billing set up! " + result);
                    mHelper.queryInventoryAsync(mGotInventoryListener); //Getting inventory of purchases and assigning listener
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
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
                PrefManager.getIn().setPackage(false);
                // does the user have the premium upgrade?
                if (inventory.hasPurchase(Utility.threemonths)) {
                    //  Toast.makeText(PaymentScreen_New.this, "3 Months Subscription is enabled", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(PaymentScreen_New.this, inventory.getPurchase(Utility.threemonths).toString(), Toast.LENGTH_SHORT).show();
                    // Log.e("inventory", inventory.getPurchase(Utility.threemonths).toString() + "inventory" + inventory.getPurchase(Utility.threemonths).getPurchaseTime());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(inventory.getPurchase(Utility.threemonths).getPurchaseTime());
                    subDate = sdf.format(date);
                    threemonthsflag = true;
                    Calendar c = Calendar.getInstance();
                    try {
                        c.setTime(sdf.parse(subDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    c.add(Calendar.MONTH, 3);
                    String endDate = sdf.format(c.getTime());
                    Log.e("inventory", subDate + ":::" + PrefManager.getIn().getSubscription_start_date() + ":::" + endDate);
                    if (!PrefManager.getIn().getSubscription_start_date().equals(subDate)) {
                        planUpdate(inventory.getPurchase(Utility.threemonths).getSku(), subDate, endDate, inventory.getPurchase(Utility.threemonths).toString(), 1);
                    }
                }
                if (inventory.hasPurchase(Utility.six_months)) {
                    // Toast.makeText(PaymentScreen_New.this, "6 Months Subscription is enabled", Toast.LENGTH_SHORT).show();
                    //  Toast.makeText(PaymentScreen_New.this, inventory.getPurchase(Utility.six_months).toString(), Toast.LENGTH_SHORT).show();
                    Log.e("inventory", inventory.getPurchase(Utility.six_months).toString() + "inventory" + inventory.toString());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(inventory.getPurchase(Utility.six_months).getPurchaseTime());
                    subDate = sdf.format(date);
                    sixmonthsflag = true;
                    Calendar c = Calendar.getInstance();
                    try {
                        c.setTime(sdf.parse(subDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    c.add(Calendar.MONTH, 6);
                    String endDate = sdf.format(c.getTime());
                    Log.e("inventory", subDate + ":::" + PrefManager.getIn().getSubscription_start_date() + ":::" + endDate);
                    if (!PrefManager.getIn().getSubscription_start_date().equals(subDate)) {
                        planUpdate(inventory.getPurchase(Utility.six_months).getSku(), subDate, endDate, inventory.getPurchase(Utility.six_months).toString(), 1);
                    }
                }
                if (inventory.hasPurchase(Utility.one_year)) {
                    // Toast.makeText(PaymentScreen_New.this, "1 year Subscription is enabled", Toast.LENGTH_SHORT).show();
                    // Toast.makeText(PaymentScreen_New.this, inventory.getPurchase(Utility.one_year).toString(), Toast.LENGTH_SHORT).show();
                    Log.e("inventory", inventory.getPurchase(Utility.one_year).toString() + "inventory" + inventory.toString());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(inventory.getPurchase(Utility.one_year).getPurchaseTime());
                    subDate = sdf.format(date);
                    oneyearflag = true;
                    Calendar c = Calendar.getInstance();
                    try {
                        c.setTime(sdf.parse(subDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    c.add(Calendar.MONTH, 12);
                    String endDate = sdf.format(c.getTime());
                    Log.e("inventory", subDate + ":::" + PrefManager.getIn().getSubscription_start_date() + ":::" + endDate);
                    if (!PrefManager.getIn().getSubscription_start_date().equals(subDate)) {
                        planUpdate(inventory.getPurchase(Utility.one_year).getSku(), subDate, endDate, inventory.getPurchase(Utility.one_year).toString(), 1);
                    }
                }


                Log.v(TAG, "Doesn't have purchase, saving in storage");


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
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

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
        if (package_data.equals(Utility.threemonths)) {
            params.put("package", "3");
        } else if (package_data.equals(Utility.six_months)) {
            params.put("package", "6");
        } else if (package_data.equals(Utility.one_year)) {
            params.put("package", "12");
        }
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
                        PrefManager.getIn().setPayment_data(params.get("payment_data"));
                        PrefManager.getIn().setSubscription_start_date(params.get("subscription_start_date"));
                        PrefManager.getIn().setSubscription_end_date(params.get("subscription_end_date"));
                        PrefManager.getIn().setPlan(params.get("package"));
                        if (renewal == 0) {
                            Intent intent = new Intent(PaymentScreen_New.this, LandingPage.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
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


}