package com.aniapps.flicbuzzapp.activities;

import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.aniapps.flicbuzzapp.AppApplication;
import com.aniapps.flicbuzzapp.R;
import com.aniapps.flicbuzzapp.networkcall.APIResponse;
import com.aniapps.flicbuzzapp.networkcall.RetrofitClient;
import com.aniapps.flicbuzzapp.utils.MySMSBroadcastReceiver;
import com.aniapps.flicbuzzapp.utils.PrefManager;
import com.aniapps.flicbuzzapp.utils.Utility;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity implements  MySMSBroadcastReceiver.OTPReceiveListener {
    Button update_btn, validateMobile;
    TextView email_error, passwordError, confirmPasswordError, text_header, otpErro, resendOTP;
    EditText passwordEditText, confirmPasswordEditText, emailMobileEditText, otpEditText;
    JSONObject jsonObject;
    LinearLayout email_ll, change_psd_ll, btn_lay;
    RelativeLayout otpLL;
    ImageView psw_confirm_img_eye, psw_img_eye;
    boolean check_visibility = true, check_visibility_confirm = true;
    String mobile="";
    private MySMSBroadcastReceiver myReceiver = new MySMSBroadcastReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView header_title = (TextView) findViewById(R.id.tvheader);
        header_title.setText("Forgot Password");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initViews();

    }

    void initViews() {
        otpErro = (TextView) findViewById(R.id.otp_error);
        resendOTP = (TextView) findViewById(R.id.resendOTP);
        otpEditText = (EditText) findViewById(R.id.ot_et);
        validateMobile = (Button) findViewById(R.id.validate_otp_btn);
        otpLL = (RelativeLayout) findViewById(R.id.otpLL);
        btn_lay = (LinearLayout) findViewById(R.id.btn_lay);
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
        psw_img_eye = (ImageView) findViewById(R.id.psw_img_eye);
        psw_confirm_img_eye = (ImageView) findViewById(R.id.psw_confirm_img_eye);
        startSMSListener();
        myReceiver.initOTPListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        AppApplication.app_ctx.registerReceiver(myReceiver, intentFilter);

        psw_img_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check_visibility) {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                    psw_img_eye.setImageResource(R.mipmap.password_visible);
                    passwordEditText.setSelection(passwordEditText.getText().length());
                    check_visibility = false;
                } else {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    psw_img_eye.setImageResource(R.mipmap.password_invisible);
                    passwordEditText.setSelection(passwordEditText.getText().length());
                    check_visibility = true;
                }
            }
        });
        psw_confirm_img_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check_visibility_confirm) {
                    confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                    psw_confirm_img_eye.setImageResource(R.mipmap.password_visible);
                    confirmPasswordEditText.setSelection(confirmPasswordEditText.getText().length());
                    check_visibility_confirm = false;
                } else {
                    confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    psw_confirm_img_eye.setImageResource(R.mipmap.password_invisible);
                    confirmPasswordEditText.setSelection(confirmPasswordEditText.getText().length());
                    check_visibility_confirm = true;
                }
            }
        });

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
        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resendOTP.getText().toString().equalsIgnoreCase("Resend OTP")) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("mobile",mobile);
                    params.put("from_source", "android");
                    params.put("action", "resend_otp");
                    params.put("user_id", PrefManager.getIn().getUserId());
                    params.put("language", PrefManager.getIn().getLanguage().toLowerCase());
                    ApiCall(params, 4);
                }
            }
        });
        validateMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otpEditText.getText().toString().length() < 4) {
                    otpErro.setVisibility(View.VISIBLE);
                } else {
                    otpErro.setVisibility(View.INVISIBLE);
                    HashMap<String, String> params = new HashMap<>();
                    params.put("mobile", mobile);
                    params.put("otp", otpEditText.getText().toString());
                    params.put("user_id", PrefManager.getIn().getUserId());
                    params.put("from_source", "android");
                    params.put("language", PrefManager.getIn().getLanguage().toLowerCase());
                    params.put("action", "verify_otp");
                    ApiCall(params, 3);
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
       /* if (myReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        }*/
        if (email_ll.getVisibility() == View.VISIBLE) {
            finish();
            overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
        } else {
            email_ll.setVisibility(View.VISIBLE);
            change_psd_ll.setVisibility(View.GONE);
            btn_lay.setVisibility(View.VISIBLE);
            otpLL.setVisibility(View.GONE);
            text_header.setText("Forgot your password");
        }
        super.onBackPressed();
    }
    public void checkValidation(int from) {
        if (from == 1) {
            email_error.setVisibility(View.INVISIBLE);
            if (emailMobileEditText.getText().toString().length() == 0) {
                emailMobileEditText.requestFocus();
                email_error.setVisibility(View.VISIBLE);
            } else {
                HashMap<String, String> params = new HashMap<>();
                params.put("email", emailMobileEditText.getText().toString());
                params.put("from_source", "android");
                params.put("action", "verify_account");
                params.put("user_id", PrefManager.getIn().getUserId());
                params.put("language", PrefManager.getIn().getLanguage().toLowerCase());
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
                    params.put("language", PrefManager.getIn().getLanguage().toLowerCase());
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
                        if (from == 1) {
                            mobile= jsonObject.getJSONObject("data").getString("mobile");
                            text_header.setText("Set your password");
                            email_ll.setVisibility(View.GONE);
                            change_psd_ll.setVisibility(View.GONE);
                            btn_lay.setVisibility(View.GONE);
                            otpLL.setVisibility(View.VISIBLE);
                            PrefManager.getIn().saveUserId(jsonObject.getString("user_id"));

                        } else if (from == 3) {
                            text_header.setText("Set your password");
                            email_ll.setVisibility(View.GONE);
                            change_psd_ll.setVisibility(View.VISIBLE);
                            otpLL.setVisibility(View.GONE);
                            btn_lay.setVisibility(View.VISIBLE);
                            PrefManager.getIn().saveUserId(jsonObject.getString("user_id"));

                        } else if (from == 4) {
                            Toast.makeText(ForgotPasswordActivity.this, jsonObject.getString("message") + ", Please authenticate with otp", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }else if(status==14){
                        Utility.alertDialog(ForgotPasswordActivity.this,  jsonObject.getString("message"));
                    } else {
                        if (from == 1) {
                          //  email_error.setVisibility(View.VISIBLE);
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

    @Override
    public void onOTPReceived(@NotNull String otp) {
       if (myReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        }
        otpEditText.setText(getVerificationCode(otp));
        validateMobile.performClick();
    }

    @Override
    public void onOTPTimeOut() {

    }

    private String getVerificationCode(String message) {
        String code = null;
        int index = message.indexOf("<#>");

        if (index != -1) {
            int start = index + 4;
            int length = 4;
            code = message.substring(start, start + length);
            return code;
        }


        return code;
    }

    private void startSMSListener() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        Task<Void> task = client.startSmsRetriever();
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("#SMS#", "Success to start retriever");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("#SMS#", "Failed to start retriever");
            }
        });
    }
}
