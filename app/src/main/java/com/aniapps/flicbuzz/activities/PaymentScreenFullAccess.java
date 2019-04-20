package com.aniapps.flicbuzz.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import com.aniapps.flicbuzz.player.LandingPage;
import com.aniapps.flicbuzz.R;
import com.aniapps.flicbuzz.util.IabHelper;
import com.aniapps.flicbuzz.util.IabResult;
import com.aniapps.flicbuzz.util.Inventory;
import com.aniapps.flicbuzz.util.Purchase;
import com.aniapps.flicbuzz.utils.PrefManager;
import com.aniapps.flicbuzz.utils.Utility;

public class PaymentScreenFullAccess extends Activity {
    Button proceed;
    public static IabHelper mHelper;
    private final int PURCHSE_REQUEST = 3;
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {

                        Intent intent = new Intent(PaymentScreenFullAccess.this, LandingPage.class);
                        intent.putExtra("Url", "http://google.com");
                        startActivity(intent);


                    } else {
                        // handle error

                        Toast.makeText(PaymentScreenFullAccess.this, "", Toast.LENGTH_SHORT).show();
                    }
                }
            };
    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) throws IabHelper.IabAsyncInProgressException {


            if (result.isFailure()) {
                // Handle failure
            }
            Purchase gasPurchase = inventory.getPurchase(Utility.threemonths);
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                mHelper.consumeAsync(inventory.getPurchase(Utility.threemonths), mConsumeFinishedListener);
                return;
            } else {
                mHelper.consumeAsync(inventory.getPurchase(Utility.threemonths),
                        mConsumeFinishedListener);
            }
        }
    };
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                // Handle error
                Log.e("failure", "failure");

            } else {
                PrefManager.getIn().setPackage(true);
                Toast.makeText(PaymentScreenFullAccess.this,""+purchase.getPurchaseTime(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PaymentScreenFullAccess.this, LandingPage.class);
                startActivity(intent);

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Making notification bar transparent
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_payment_full);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.app_name));
        mToolbar.setNavigationIcon(R.drawable.arrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        proceed = (Button) findViewById(R.id.proceed);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mHelper.flagEndAsync();
                    mHelper.launchPurchaseFlow(PaymentScreenFullAccess.this, Utility.threemonths, PURCHSE_REQUEST, mPurchaseFinishedListener, null);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                    Toast.makeText(PaymentScreenFullAccess.this, "Please retry in a few seconds.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mHelper = new IabHelper(PaymentScreenFullAccess.this, Utility.sharedKey);

        mHelper.startSetup(new
                                   IabHelper.OnIabSetupFinishedListener() {
                                       public void onIabSetupFinished(IabResult result) {
                                           if (!result.isSuccess()) {
                                               Log.e("limited", "In-app Billing is not set up OK");
                                           } else {


                                               Log.e("limited", "In-app Billing is set up OK");
                                           }

                                       }


                                   });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
        mHelper = null;
    }

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

    public void consumeItem() throws IabHelper.IabAsyncInProgressException {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();


        return true;
    }

}