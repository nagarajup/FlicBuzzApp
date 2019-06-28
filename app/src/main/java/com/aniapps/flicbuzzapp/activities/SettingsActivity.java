package com.aniapps.flicbuzzapp.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.aniapps.flicbuzzapp.AppConstants;
import com.aniapps.flicbuzzapp.R;
import com.aniapps.flicbuzzapp.networkcall.APIResponse;
import com.aniapps.flicbuzzapp.networkcall.RetrofitClient;
import com.aniapps.flicbuzzapp.utils.PrefManager;
import com.aniapps.flicbuzzapp.utils.Utility;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppConstants {
    TextView changePswd, cancelSubscription, logout;
    TextView header_title;
    JSONObject jsonObject;
    SimpleDateFormat sdf;
    SimpleDateFormat sdf1;

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
        sdf = new SimpleDateFormat("dd MMM yyyy");
        sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
                // alertDialog(SettingsActivity.this, "Logout", "Are you sure to logout.", 1);
                cancelSubPopUp(2);
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
                    //alertDialog(SettingsActivity.this, "Alert", "Are you sure to cancel subscription", 2);
                    cancelSubPopUp(1);
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

    public void cancelSubPopUp(final int from) {

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        final Dialog alert_dialog = new Dialog(SettingsActivity.this);
        alert_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert_dialog.setContentView(R.layout.alert_dialog);
        alert_dialog.setCanceledOnTouchOutside(false);
        alert_dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout main = (LinearLayout) alert_dialog.findViewById(R.id.main);
        TextView txt_alert_title = (TextView) alert_dialog.findViewById(R.id.custom_alert_dialog_title);
        TextView txt_alert_description = (TextView) alert_dialog.findViewById(R.id.custom_alert_dialog_msg);
        Button txt_ok = (Button) alert_dialog.findViewById(R.id.ok);
        Button txt_cancel = (Button) alert_dialog.findViewById(R.id.cancel);
        main.setVisibility(View.VISIBLE);
        if (from == 1) {
            try {
                Date date = sdf1.parse(PrefManager.getIn().getSubscription_end_date());
                txt_alert_title.setText("Are you sure you want to proceed with your cancellation");
                txt_alert_description.setText("If you confirm and end your subscription now, you can still access it until \n" + sdf.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
                txt_alert_title.setText("Are you sure you want to proceed with your cancellation");
                txt_alert_description.setText("If you confirm and end your subscription now, you can still access it until end date.");
            }
        }else{
            txt_alert_title.setText("LOGOUT");
            txt_alert_description.setText("Are you sure you want to logout?");
            txt_cancel.setText("OK");
            txt_ok.setText("CANCEL");
        }
        txt_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                alert_dialog.dismiss();
                if(from==2){
                    trackEvent(SettingsActivity.this, "MainPage", "Settings|LogOut|Ok");
                    PrefManager.getIn().clearLogins();
                    //PrefManager.getIn().setLogin(false);
                    Intent intent = new Intent(SettingsActivity.this, SignIn.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                }
            }
        });

        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert_dialog.dismiss();
                if(from==1) {
                    trackEvent(SettingsActivity.this, "MainPage", "Settings|Cancel Subscription popup|Ok");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String canceldate = sdf.format(Calendar.getInstance().getTime());
                    HashMap<String, String> params = new HashMap<>();
                    params.put("from_source", "android");
                    if (PrefManager.getIn().getGateway().equalsIgnoreCase("razorpay")) {
                        params.put("action", "subscription_cancelled");
                    } else {
                        params.put("action", "googlepay_cancel");
                    }
                    params.put("user_id", PrefManager.getIn().getUserId());
                    params.put("cancel_date", canceldate);
                    ApiCall(params);
                }
            }
        });

        alert_dialog.show();
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
