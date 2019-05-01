package com.aniapps.flicbuzz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.aniapps.flicbuzz.player.LandingPage;
import com.aniapps.flicbuzz.R;
import com.aniapps.flicbuzz.networkcall.APIResponse;
import com.aniapps.flicbuzz.networkcall.RetrofitClient;
import com.aniapps.flicbuzz.utils.PrefManager;
import com.aniapps.flicbuzz.utils.Utility;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    Button signIn;
    TextView emailError, passwordError, signUp, forgotPassword;
    EditText emailEditText, passwordEditText;
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.mytoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");
        initViews();
    }

    void initViews() {
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
                ApiCall(params);
            }


        }


    }

    public void ApiCall(Map<String, String> params) {

        RetrofitClient.getInstance().doBackProcess(LoginActivity.this, params, "", new APIResponse() {
            @Override
            public void onSuccess(String result) {
                try {

                    jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        PrefManager.getIn().setLogin(true);
                        PrefManager.getIn().saveUserId(jsonObject.getString("user_id"));
                        PrefManager.getIn().setPayment_data(jsonObject.getString("payment_data"));
                        PrefManager.getIn().setSubscription_start_date(jsonObject.getString("subscription_start_date"));
                        PrefManager.getIn().setSubscription_end_date(jsonObject.getString("subscription_end_date"));
                        PrefManager.getIn().setPlan(jsonObject.getString("plan"));
                        JSONObject userObject = jsonObject.getJSONObject("data");
                        PrefManager.getIn().setName(userObject.getString("name"));
                        PrefManager.getIn().setEmail(userObject.getString("email"));
                        PrefManager.getIn().setMobile(userObject.getString("mobile"));
                        PrefManager.getIn().setGender(userObject.getString("gender"));
                        PrefManager.getIn().setCity(userObject.getString("city"));
                        PrefManager.getIn().setPincode(userObject.getString("pincode"));
                        PrefManager.getIn().setDob(userObject.getString("dob"));
                        PrefManager.getIn().setProfile_pic(userObject.getString("profile_pic"));
                        if(PrefManager.getIn().getPlan().equals("3")||PrefManager.getIn().getPlan().equals("6")||PrefManager.getIn().getPlan().equals("12")) {
                            Intent intent = new Intent(LoginActivity.this, LandingPage.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                        }else{
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
        return super.onOptionsItemSelected(item);
    }
}
