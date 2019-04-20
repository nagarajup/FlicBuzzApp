package com.aniapps.flicbuzz.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.aniapps.flicbuzz.player.LandingPage;
import com.aniapps.flicbuzz.R;
import com.aniapps.flicbuzz.util.IabHelper;
import com.aniapps.flicbuzz.util.IabResult;
import com.aniapps.flicbuzz.util.Inventory;
import com.aniapps.flicbuzz.util.Purchase;
import com.aniapps.flicbuzz.utils.PrefManager;
import com.aniapps.flicbuzz.utils.Utility;

public class PaymentScreen_New extends Activity {
    ConstraintLayout threemonths, sixmonths, oneyear;
    public static IabHelper mHelper;
    private final int PURCHSE_REQUEST = 3;
    private final String TAG = "PaymentScreen_New";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_payment_new);
        threemonths = (ConstraintLayout) findViewById(R.id.threemonths);
        sixmonths = (ConstraintLayout) findViewById(R.id.sixmonths);
        oneyear = (ConstraintLayout) findViewById(R.id.oneyear);
        mHelper = new IabHelper(PaymentScreen_New.this, Utility.sharedKey);
        threemonths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mHelper.flagEndAsync();
                    mHelper.launchPurchaseFlow(PaymentScreen_New.this, Utility.threemonths, PURCHSE_REQUEST, mPurchaseFinishedListener, null);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                    Toast.makeText(PaymentScreen_New.this, "Please retry in a few seconds.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        sixmonths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mHelper.flagEndAsync();
                    mHelper.launchPurchaseFlow(PaymentScreen_New.this, Utility.six_months, PURCHSE_REQUEST, mPurchaseFinishedListener, null);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                    Toast.makeText(PaymentScreen_New.this, "Please retry in a few seconds.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        oneyear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mHelper.flagEndAsync();
                    mHelper.launchPurchaseFlow(PaymentScreen_New.this, Utility.one_year, PURCHSE_REQUEST, mPurchaseFinishedListener, null);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                    Toast.makeText(PaymentScreen_New.this, "Please retry in a few seconds.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mHelper.startSetup(new
                                   IabHelper.OnIabSetupFinishedListener() {
                                       public void onIabSetupFinished(IabResult result) {
                                           if (!result.isSuccess()) {
                                               Log.e("limited", "In-app Billing is not set up OK");
                                           } else {

                                               Log.v("Limites", "YAY, in app billing set up! " + result);
                                               try {
                                                   mHelper.queryInventoryAsync(mGotInventoryListener); //Getting inventory of purchases and assigning listener
                                               } catch (IabHelper.IabAsyncInProgressException e) {
                                                   e.printStackTrace();
                                               }
                                           }

                                       }


                                   });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null)
            try {
                mHelper.dispose();
                mHelper = null;

            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
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
                    Toast.makeText(PaymentScreen_New.this,"3 Months Subscription is enabled" ,Toast.LENGTH_SHORT).show();
                    Toast.makeText(PaymentScreen_New.this,inventory.getPurchase(Utility.threemonths).toString() ,Toast.LENGTH_SHORT).show();
                    Log.e("inventory", inventory.getPurchase(Utility.threemonths).toString() + "inventory" + inventory.toString());
                    PrefManager.getIn().setPackage(true);

                } if (inventory.hasPurchase(Utility.six_months)) {
                    Toast.makeText(PaymentScreen_New.this,"6 Months Subscription is enabled" ,Toast.LENGTH_SHORT).show();
                    Toast.makeText(PaymentScreen_New.this,inventory.getPurchase(Utility.six_months).toString() ,Toast.LENGTH_SHORT).show();
                    Log.e("inventory", inventory.getPurchase(Utility.six_months).toString() + "inventory" + inventory.toString());
                    PrefManager.getIn().setPackage(true);

                } if (inventory.hasPurchase(Utility.one_year)) {
                    Toast.makeText(PaymentScreen_New.this,"1year Subscription is enabled" ,Toast.LENGTH_SHORT).show();
                    Toast.makeText(PaymentScreen_New.this,inventory.getPurchase(Utility.one_year).toString() ,Toast.LENGTH_SHORT).show();
                    Log.e("inventory", inventory.getPurchase(Utility.one_year).toString() + "inventory" + inventory.toString());
                    PrefManager.getIn().setPackage(true);

                }



                    Log.v(TAG, "Doesn't have purchase, saving in storage");


            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        try {
            if (!mHelper.handleActivityResult(requestCode,
                    resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                // Handle error
                Log.e("failure", "failure");

            } else {
                Toast.makeText(PaymentScreen_New.this,purchase.toString()+"::"+purchase.getPurchaseTime(),Toast.LENGTH_SHORT).show();
                Log.e("",""+purchase);
                PrefManager.getIn().setPackage(true);
                Intent intent = new Intent(PaymentScreen_New.this, LandingPage.class);
                startActivity(intent);
                // consumeItem();

            }

        }
    };
}