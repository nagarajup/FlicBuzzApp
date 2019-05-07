package com.aniapps.flicbuzzapp.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.aniapps.flicbuzzapp.R;
import com.aniapps.flicbuzzapp.networkcall.APIResponse;
import com.aniapps.flicbuzzapp.networkcall.RetrofitClient;
import com.aniapps.flicbuzzapp.player.LandingPage;
import com.aniapps.flicbuzzapp.utils.PrefManager;
import com.aniapps.flicbuzzapp.utils.Utility;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    Button signIn, validateMobile;
    TextView emailError, passwordError, signUp, forgotPassword, otpErro, resendOTP;
    EditText otpEditText, emailEditText, passwordEditText;
    JSONObject jsonObject;
    RelativeLayout loginLL, otpLL;
    String user_id = "", mobile_num = "";
    CountDownTimer timer;
    String reqString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView header_title = (TextView) findViewById(R.id.title);
        header_title.setText("Login");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timer != null) {
                    timer.cancel();
                }
                if (loginLL.getVisibility() == View.VISIBLE) {
                    finish();
                    overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
                } else {
                    loginLL.setVisibility(View.VISIBLE);
                    otpLL.setVisibility(View.GONE);
                }
            }
        });
        initViews();
    }

    void initViews() {
         reqString = Build.MANUFACTURER
                + " " + Build.MODEL + " " + Build.VERSION.RELEASE
                + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();
        otpErro = (TextView) findViewById(R.id.otp_error);
        resendOTP = (TextView) findViewById(R.id.resendOTP);
        otpEditText = (EditText) findViewById(R.id.ot_et);
        validateMobile = (Button) findViewById(R.id.validate_otp_btn);
        loginLL = (RelativeLayout) findViewById(R.id.loginLL);
        otpLL = (RelativeLayout) findViewById(R.id.otpLL);
        signIn = (Button) findViewById(R.id.login_btn);
        emailError = (TextView) findViewById(R.id.req_customer_email_error);
        passwordError = (TextView) findViewById(R.id.req_customer_password_error);
        signUp = (TextView) findViewById(R.id.signUpTV);
        forgotPassword = (TextView) findViewById(R.id.forgotPsd);
        emailEditText = (EditText) findViewById(R.id.email_txt);
        passwordEditText = (EditText) findViewById(R.id.pwd_txt);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                finish();
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
            }
        });
        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resendOTP.getText().toString().equalsIgnoreCase("Resend OTP")) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("mobile", mobile_num);
                    params.put("from_source", "android");
                    params.put("action", "resend_otp");
                    params.put("user_id", user_id);
                    ApiCall(params, 3);
                }
            }
        });
        validateMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otpEditText.getText().toString().length() < 4) {
                    otpErro.setVisibility(View.VISIBLE);
                } else {
                    otpErro.setVisibility(View.GONE);
                    HashMap<String, String> params = new HashMap<>();
                    params.put("mobile", mobile_num);
                    params.put("otp", otpEditText.getText().toString());
                    params.put("user_id", user_id);
                    params.put("from_source", "android");
                    params.put("action", "verify_otp");
                    ApiCall(params, 1);
                }
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });
    }

    public void checkValidation() {
        emailError.setVisibility(View.INVISIBLE);
        passwordError.setVisibility(View.INVISIBLE);
        if (Utility.validateEmail(emailEditText) && Utility.hasMobileNumber(emailEditText)) {
            emailEditText.requestFocus();
            emailError.setVisibility(View.VISIBLE);
        } else if (passwordEditText.getText().toString().length() == 0) {
            passwordEditText.requestFocus();
            passwordError.setVisibility(View.VISIBLE);
        } else {
            if (!Utility.isConnectingToInternet(this)) {
                Utility.alertDialog(LoginActivity.this,
                        "No Internet Connection",
                        "Please check your internet connectivity and try again!");

            } else {
                HashMap<String, String> params = new HashMap<>();
                params.put("password", passwordEditText.getText().toString());
                params.put("email", emailEditText.getText().toString());
                params.put("from_source", "android");
                params.put("action", "login");
                params.put("system_info", reqString);
                ApiCall(params, 2);
            }

        }


    }


    public void ApiCall(Map<String, String> params, final int from) {

        RetrofitClient.getInstance().doBackProcess(LoginActivity.this, params, "", new APIResponse() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onSuccess(String result) {
                try {

                    jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    if (from == 2) {
                        if (status == 1) {
                            PrefManager.getIn().setLogin(true);
                            PrefManager.getIn().saveUserId(jsonObject.getString("user_id"));
                            PrefManager.getIn().setPayment_data(jsonObject.getString("payment_data"));
                            PrefManager.getIn().setSubscription_start_date(jsonObject.getString("subscription_start_date"));
                            PrefManager.getIn().setSubscription_end_date(jsonObject.getString("subscription_end_date"));
                            PrefManager.getIn().setPlan(jsonObject.getString("plan"));
                            PrefManager.getIn().setPayment_mode(jsonObject.getString("payment_mode"));
                            JSONObject userObject = jsonObject.getJSONObject("data");
                            PrefManager.getIn().setName(userObject.getString("name"));
                            PrefManager.getIn().setEmail(userObject.getString("email"));
                            PrefManager.getIn().setMobile(userObject.getString("mobile"));
                            PrefManager.getIn().setGender(userObject.getString("gender"));
                            PrefManager.getIn().setCity(userObject.getString("city"));
                            PrefManager.getIn().setPincode(userObject.getString("pincode"));
                            PrefManager.getIn().setDob(userObject.getString("dob"));
                            PrefManager.getIn().setProfile_pic(userObject.getString("profile_pic"));
                            if (PrefManager.getIn().getPayment_mode().equals("1")) {
                                Intent intent = new Intent(LoginActivity.this, LandingPage.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                            } else {
                                Intent intent = new Intent(LoginActivity.this, PaymentScreen_New.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                            }
                        } else if (status == 18) {
                            PrefManager.getIn().saveUserId(jsonObject.getString("user_id"));
                            user_id = jsonObject.getString("user_id");
                            mobile_num = jsonObject.getString("mobile");
                            Toast.makeText(LoginActivity.this, jsonObject.getString("message") + ", Please authenticate with otp", Toast.LENGTH_SHORT).show();
                            otpLL.setVisibility(View.VISIBLE);
                            loginLL.setVisibility(View.GONE);
                            countDown(resendOTP);
                        } else {
                            Utility.alertDialog(LoginActivity.this, "Alert", jsonObject.getString("message"));
                        }
                    } else if (from == 1) {
                        if (status == 1) {
                            PrefManager.getIn().setLogin(true);
                            PrefManager.getIn().saveUserId(jsonObject.getString("user_id"));
                            PrefManager.getIn().setPayment_data(jsonObject.getString("payment_data"));
                            PrefManager.getIn().setSubscription_start_date(jsonObject.getString("subscription_start_date"));
                            PrefManager.getIn().setSubscription_end_date(jsonObject.getString("subscription_end_date"));
                            PrefManager.getIn().setPlan(jsonObject.getString("plan"));
                            PrefManager.getIn().setPayment_mode(jsonObject.getString("payment_mode"));
                            JSONObject userObject = jsonObject.getJSONObject("data");
                            PrefManager.getIn().setName(userObject.getString("name"));
                            PrefManager.getIn().setEmail(userObject.getString("email"));
                            PrefManager.getIn().setMobile(userObject.getString("mobile"));
                            PrefManager.getIn().setGender(userObject.getString("gender"));
                            PrefManager.getIn().setCity(userObject.getString("city"));
                            PrefManager.getIn().setPincode(userObject.getString("pincode"));
                            PrefManager.getIn().setDob(userObject.getString("dob"));
                            PrefManager.getIn().setProfile_pic(userObject.getString("profile_pic"));
                            if (PrefManager.getIn().getPayment_mode().equals("1")) {
                                Intent intent = new Intent(LoginActivity.this, LandingPage.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                            } else {
                                Intent intent = new Intent(LoginActivity.this, PaymentScreen_New.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                            }
                        } else {
                            Utility.alertDialog(LoginActivity.this, "Alert", jsonObject.getString("message"));
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utility.alertDialog(LoginActivity.this, "Alert", e.getMessage());
                }
            }

            @Override
            public void onFailure(String res) {
                Utility.alertDialog(LoginActivity.this, "Alert", res);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void countDown(final TextView mTextField) {
        validateMobile.setBackground(getDrawable(R.drawable.rounded_corners_grey));
        validateMobile.setEnabled(false);
        timer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                mTextField.setText(millisUntilFinished / 1000 + " Sec");
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                mTextField.setText("Resend OTP");
                validateMobile.setBackground(getDrawable(R.drawable.rounded_corners_signup));
                validateMobile.setEnabled(true);
            }

        };
        timer.start();
    }

    @Override
    public void onBackPressed() {
        if (timer != null) {
            timer.cancel();
        }
        if (loginLL.getVisibility() == View.VISIBLE) {
            finish();
            overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
        } else {
            loginLL.setVisibility(View.VISIBLE);
            otpLL.setVisibility(View.GONE);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (timer != null) {
            timer.cancel();
        }
        if (loginLL.getVisibility() == View.VISIBLE) {
            finish();
            overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
        } else {
            loginLL.setVisibility(View.VISIBLE);
            otpLL.setVisibility(View.GONE);
        }
        return super.onOptionsItemSelected(item);
    }
}
