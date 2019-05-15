package com.aniapps.flicbuzzapp.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.aniapps.flicbuzzapp.AppConstants;
import com.aniapps.flicbuzzapp.R;
import com.aniapps.flicbuzzapp.utils.PrefManager;

public class SettingsActivity extends AppConstants {
    TextView changePswd, cancelSubscription, logout;
    TextView header_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        header_title = (TextView) findViewById(R.id.title);
        header_title.setText("Settings");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
    }

    void initViews() {

        changePswd = (TextView) findViewById(R.id.change_psd);
        cancelSubscription = (TextView) findViewById(R.id.cancel_subscripiton);
        logout = (TextView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trackEvent(SettingsActivity.this,"MainPage","Settings|LogOut");
                alertDialog(SettingsActivity.this, "Logout", "Are you sure to logout.");
            }
        });
        cancelSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trackEvent(SettingsActivity.this,"MainPage","Settings|Cancel Subscription");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/account/subscriptions"));
                startActivity(browserIntent);
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
            }
        });

        changePswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trackEvent(SettingsActivity.this,"MainPage","Change Password");
                Intent intent = new Intent(SettingsActivity.this,UpdatePasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
            }
        });
    }
    public  void alertDialog(final Context context, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                trackEvent(SettingsActivity.this,"MainPage","Settings|LogOut|Cancel");
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                trackEvent(SettingsActivity.this,"MainPage","Settings|LogOut|Ok");
                dialog.dismiss();
                PrefManager.getIn().clearLogins();
                //PrefManager.getIn().setLogin(false);
                Intent intent =new Intent(SettingsActivity.this,SignIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);

            }
        });
        //builder.setNegativeButton("NO", null);
        builder.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
        return super.onOptionsItemSelected(item);
    }
}
