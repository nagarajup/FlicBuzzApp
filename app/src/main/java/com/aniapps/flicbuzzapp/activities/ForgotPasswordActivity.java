package com.aniapps.flicbuzzapp.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.aniapps.flicbuzzapp.R;
import com.aniapps.flicbuzzapp.networkcall.APIResponse;
import com.aniapps.flicbuzzapp.networkcall.RetrofitClient;
import com.aniapps.flicbuzzapp.utils.PrefManager;
import com.aniapps.flicbuzzapp.utils.Utility;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {
    Button update_btn;
    TextView email_error,passwordError, confirmPasswordError, text_header;
    EditText passwordEditText, confirmPasswordEditText, emailMobileEditText;
    JSONObject jsonObject;
    LinearLayout email_ll, change_psd_ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView  header_title = (TextView) findViewById(R.id.title);
        header_title.setText("Forgot Password");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();
    }

    void initViews() {

        change_psd_ll = (LinearLayout) findViewById(R.id.change_psd_ll);
        email_ll = (LinearLayout) findViewById(R.id.email_ll);
        text_header = (TextView) findViewById(R.id.text_header);
        email_error = (TextView) findViewById(R.id.email_error);
        passwordError = (TextView) findViewById(R.id.password_error);
        confirmPasswordError = (TextView) findViewById(R.id.confirpassword_error);

        confirmPasswordEditText = (EditText) findViewById(R.id.confirm_pwd);
        passwordEditText = (EditText) findViewById(R.id.pwd);
        emailMobileEditText = (EditText) findViewById(R.id.email_mobile_et);
        update_btn = (Button) findViewById(R.id.update_btn);

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (email_ll.getVisibility() == View.VISIBLE) {
                    checkValidation(1);
                } else {
                    checkValidation(2);
                }
            }
        });
    }

    public void checkValidation(int from) {
        if (from == 1) {
            email_error.setVisibility(View.GONE);
            if(emailMobileEditText.getText().toString().length()==0){
                emailMobileEditText.requestFocus();
                email_error.setVisibility(View.VISIBLE);
            }else {
                HashMap<String, String> params = new HashMap<>();
                params.put("email", emailMobileEditText.getText().toString());
                params.put("from_source", "android");
                params.put("action", "verify_account");
                params.put("user_id", PrefManager.getIn().getUserId());
                ApiCall(params, 1);
            }
        } else {
            passwordError.setVisibility(View.INVISIBLE);
            confirmPasswordError.setVisibility(View.INVISIBLE);
            if (Utility.validatePassword(passwordEditText)) {
                passwordEditText.requestFocus();
                passwordError.setVisibility(View.VISIBLE);
            } else if (Utility.validatePassword(confirmPasswordEditText) && !passwordEditText.getText().toString().equalsIgnoreCase(confirmPasswordEditText.getText().toString())) {
                confirmPasswordEditText.requestFocus();
                confirmPasswordError.setText("New Password not matched");
                confirmPasswordError.setVisibility(View.VISIBLE);
            } else {
                if (!Utility.isConnectingToInternet(this)) {
                    Utility.alertDialog(ForgotPasswordActivity.this,
                            "No Internet Connection",
                            "Please check your internet connectivity and try again!");

                } else {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("user_id", PrefManager.getIn().getUserId());
                    params.put("new_password", passwordEditText.getText().toString());
                    params.put("confirm_password", confirmPasswordEditText.getText().toString());
                    params.put("from_source", "android");
                    params.put("action", "forgot_password");
                    ApiCall(params, 2);
                }

            }
        }


    }

    public void ApiCall(Map<String, String> params, final int from) {

        RetrofitClient.getInstance().doBackProcess(ForgotPasswordActivity.this, params, "", new APIResponse() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onSuccess(String result) {
                try {

                    jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        if(from==1){
                            text_header.setText("Set your password");
                            email_ll.setVisibility(View.GONE);
                            change_psd_ll.setVisibility(View.VISIBLE);

                        }else {
                            Toast.makeText(ForgotPasswordActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        if(from==1){
                            email_error.setVisibility(View.VISIBLE);
                        }
                        Utility.alertDialog(ForgotPasswordActivity.this, "Alert", jsonObject.getString("message"));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utility.alertDialog(ForgotPasswordActivity.this, "Alert", e.getMessage());
                }
            }

            @Override
            public void onFailure(String res) {
                Utility.alertDialog(ForgotPasswordActivity.this, "Alert", res);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
        return super.onOptionsItemSelected(item);
    }
}
