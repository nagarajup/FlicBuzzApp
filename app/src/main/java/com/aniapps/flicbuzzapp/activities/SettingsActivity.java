package com.aniapps.flicbuzzapp.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.aniapps.flicbuzzapp.AppConstants;
import com.aniapps.flicbuzzapp.R;
import com.aniapps.flicbuzzapp.networkcall.APIResponse;
import com.aniapps.flicbuzzapp.networkcall.RetrofitClient;
import com.aniapps.flicbuzzapp.utils.PrefManager;
import com.aniapps.flicbuzzapp.utils.Utility;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppConstants {
    TextView changePswd, cancelSubscription, logout;
    TextView header_title;
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        header_title = (TextView) findViewById(R.id.tvheader);
        header_title.setText("Settings");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initViews();
    }

    void initViews() {

        changePswd = (TextView) findViewById(R.id.change_psd);
        cancelSubscription = (TextView) findViewById(R.id.cancel_subscripiton);
        if (PrefManager.getIn().getSubscription_auto_renew().equalsIgnoreCase("yes")) {
            cancelSubscription.setText("Cancel Subscription");
        } else {
            cancelSubscription.setText("Subscription Cancelled");
        }
        logout = (TextView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trackEvent(SettingsActivity.this, "MainPage", "Settings|LogOut");
                alertDialog(SettingsActivity.this, "Logout", "Are you sure to logout.", 1);
            }
        });
        cancelSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cancelSubscription.getText().toString().equals("Cancel Subscription")) {
                    trackEvent(SettingsActivity.this, "MainPage", "Settings|Cancel Subscription");
               /* Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/account/subscriptions"));
                startActivity(browserIntent);
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);*/
                    alertDialog(SettingsActivity.this, "Alert", "Are you sure to cancel subscription", 2);
                } else {
                    Toast.makeText(SettingsActivity.this, "Already subscription cancelled.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        changePswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trackEvent(SettingsActivity.this, "MainPage", "Change Password");
                Intent intent = new Intent(SettingsActivity.this, UpdatePasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
            }
        });
    }

    public void alertDialog(final Context context, String title, String msg, final int from) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (from == 1) {
                    trackEvent(SettingsActivity.this, "MainPage", "Settings|LogOut|Cancel");
                } else {
                    trackEvent(SettingsActivity.this, "MainPage", "Settings|Cancel Subscription popup|Cancel");
                }
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (from == 1) {
                    trackEvent(SettingsActivity.this, "MainPage", "Settings|LogOut|Ok");
                    dialog.dismiss();
                    PrefManager.getIn().clearLogins();
                    //PrefManager.getIn().setLogin(false);
                    Intent intent = new Intent(SettingsActivity.this, SignIn.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                } else {
                    trackEvent(SettingsActivity.this, "MainPage", "Settings|Cancel Subscription popup|Ok");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String canceldate = sdf.format(Calendar.getInstance().getTime());
                    HashMap<String, String> params = new HashMap<>();
                    params.put("from_source", "android");
                    if (PrefManager.getIn().getGateway().equalsIgnoreCase("razorpay")) {
                        params.put("action", "subscription_cancelled");
                    }else {
                        params.put("action", "googlepay_cancel");
                    }
                    params.put("user_id", PrefManager.getIn().getUserId());
                    params.put("cancel_date", canceldate);
                    ApiCall(params);
                }

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

    public void ApiCall(Map<String, String> params) {

        RetrofitClient.getInstance().doBackProcess(SettingsActivity.this, params, "", new APIResponse() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.e("result", "result" + result);
                    jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        PrefManager.getIn().setSubscription_auto_renew("no");
                        cancelSubscription.setText("Subscription Cancelled");
                        Utility.alertDialog(SettingsActivity.this, "Alert", "Successfully subscription cancelled.");
                    } else {
                        Utility.alertDialog(SettingsActivity.this, "Alert", jsonObject.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utility.alertDialog(SettingsActivity.this, "Alert", e.getMessage());
                }
            }

            @Override
            public void onFailure(String res) {
                Utility.alertDialog(SettingsActivity.this, "Alert", res);
            }
        });
    }

}
