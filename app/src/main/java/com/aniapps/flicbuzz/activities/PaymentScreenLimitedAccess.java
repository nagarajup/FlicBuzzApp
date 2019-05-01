package com.aniapps.flicbuzz.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import com.aniapps.flicbuzz.player.LandingPage;
import com.aniapps.flicbuzz.R;
import com.aniapps.flicbuzz.util.IabHelper;
import com.aniapps.flicbuzz.util.IabResult;
import com.aniapps.flicbuzz.util.Inventory;
import com.aniapps.flicbuzz.util.Purchase;
import com.aniapps.flicbuzz.utils.PrefManager;
import com.aniapps.flicbuzz.utils.Utility;

public class PaymentScreenLimitedAccess extends Activity {
    Button proceed;
    CheckBox termsofservice;
    public static IabHelper mHelper;
    private final int PURCHSE_REQUEST = 3;

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory)  {


            if (result.isFailure()) {
                // Handle failure
            }
            if(inventory.hasPurchase(Utility.tendaysubs)) {
                Toast.makeText(PaymentScreenLimitedAccess.this,inventory.getPurchase("10days").toString() ,Toast.LENGTH_SHORT).show();
                Log.e("inventory", inventory.getPurchase("10days").toString() + "inventory" + inventory.toString());
            }else if(inventory.hasPurchase(Utility.threemonths)) {
                Toast.makeText(PaymentScreenLimitedAccess.this,inventory.getPurchase(Utility.threemonths).toString() ,Toast.LENGTH_SHORT).show();
            }
        }
    };
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                // Handle error
                Log.e("failure", "failure");

            } else {
                Toast.makeText(PaymentScreenLimitedAccess.this,""+purchase.getPurchaseTime(),Toast.LENGTH_SHORT).show();
                Log.e("",""+purchase);
                PrefManager.getIn().setPackage(true);
                Intent intent = new Intent(PaymentScreenLimitedAccess.this, LandingPage.class);
                startActivity(intent);
                // consumeItem();

            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Making notification bar transparent
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_payment_limited);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.app_name));
        mToolbar.setNavigationIcon(R.drawable.arrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
            }
        });
        proceed = (Button) findViewById(R.id.proceed);
        termsofservice = (CheckBox) findViewById(R.id.termsofservice);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!termsofservice.isChecked()) {
                    Toast.makeText(PaymentScreenLimitedAccess.this, "Please check terms of service", Toast.LENGTH_SHORT).show();
                }else{
                        mHelper.flagEndAsync();
                        mHelper.launchPurchaseFlow(PaymentScreenLimitedAccess.this, Utility.tendaysubs, PURCHSE_REQUEST, mPurchaseFinishedListener, null);

                }

            }
        });
        mHelper = new IabHelper(PaymentScreenLimitedAccess.this, Utility.sharedKey);
        mHelper.startSetup(new
                                   IabHelper.OnIabSetupFinishedListener() {
                                       public void onIabSetupFinished(IabResult result) {
                                           if (!result.isSuccess()) {
                                               Log.e("limited", "In-app Billing is not set up OK");
                                           } else {

                                               Log.v("Limites", "YAY, in app billing set up! " + result);
                                                   mHelper.queryInventoryAsync(mReceivedInventoryListener); //Getting inventory of purchases and assigning listener

                                           }

                                       }


                                   });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        Log.d("", "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("", "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d("", "onActivityResult handled by IABUtil.");
        }
    }
    public void consumeItem() {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();


        return true;
    }

}