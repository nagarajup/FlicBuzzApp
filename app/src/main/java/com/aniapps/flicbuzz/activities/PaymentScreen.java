package com.aniapps.flicbuzz.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import com.aniapps.flicbuzz.R;

public class PaymentScreen extends Activity {
    Button full,limited;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_payment);
        full =(Button)findViewById(R.id.full_access);
        limited=(Button)findViewById(R.id.limited_access);


        full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentScreen.this,PaymentScreenFullAccess.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
            }
        });

        limited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentScreen.this,PaymentScreenLimitedAccess.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
            }
        });
    }

   }