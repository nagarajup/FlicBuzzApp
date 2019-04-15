package com.aniapps.flicbuzz.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import com.aniapps.flicbuzz.R;

public class PaymentScreenLimitedAccess extends Activity {
    Button proceed;
    CheckBox termsofservice;
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
            }
        });
        proceed =(Button)findViewById(R.id.proceed);
        termsofservice =(CheckBox) findViewById(R.id.termsofservice);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!termsofservice.isChecked()){
                    Toast.makeText(PaymentScreenLimitedAccess.this,"Please check terms of service",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

   }