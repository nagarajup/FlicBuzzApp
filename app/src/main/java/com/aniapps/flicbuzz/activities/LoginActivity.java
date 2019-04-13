package com.aniapps.flicbuzz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.aniapps.flicbuzz.Listing;
import com.aniapps.flicbuzz.MyPlaerList;
import com.aniapps.flicbuzz.R;
import com.aniapps.flicbuzz.SignUp;
import com.aniapps.flicbuzz.networkcall.APIResponse;
import com.aniapps.flicbuzz.networkcall.RetrofitClient;
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
                Intent intent = new Intent(LoginActivity.this, SignUp.class);
                startActivity(intent);
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
        if (Utility.validateEmail(emailEditText)) {
            emailEditText.requestFocus();
            emailError.setVisibility(View.VISIBLE);
        } else if (Utility.validatePassword(passwordEditText)) {
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
                        Intent intent = new Intent(LoginActivity.this, MyPlaerList.class);
                        startActivity(intent);
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

}
