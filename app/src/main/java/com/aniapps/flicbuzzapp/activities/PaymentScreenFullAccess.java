package com.aniapps.flicbuzzapp.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import com.aniapps.flicbuzzapp.player.LandingPage;
import com.aniapps.flicbuzzapp.R;
import com.aniapps.flicbuzzapp.util.IabHelper;
import com.aniapps.flicbuzzapp.util.IabResult;
import com.aniapps.flicbuzzapp.util.Purchase;
import com.aniapps.flicbuzzapp.utils.PrefManager;
import com.aniapps.flicbuzzapp.utils.Utility;

public class PaymentScreenFullAccess extends Activity {
    Button proceed;
    public static IabHelper mHelper;
    private final int PURCHSE_REQUEST = 3;

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
                    mHelper.flagEndAsync();
                    mHelper.launchPurchaseFlow(PaymentScreenFullAccess.this, Utility.threemonths, PURCHSE_REQUEST, mPurchaseFinishedListener, null);

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



}