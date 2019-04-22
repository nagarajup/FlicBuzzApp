package com.aniapps.flicbuzz.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;
import com.aniapps.flicbuzz.R;
import com.aniapps.flicbuzz.networkcall.APIResponse;
import com.aniapps.flicbuzz.networkcall.RetrofitClient;
import com.aniapps.flicbuzz.utils.Utility;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UpdatePasswordActivity extends AppCompatActivity {
    Button update_btn;
    TextView passwordError, confirmPasswordError, oldPasswordError;
    EditText passwordEditText, confirmPasswordEditText, oldPasswordEditText;
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.app_name));
        mToolbar.setNavigationIcon(R.drawable.arrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initViews();
    }

    void initViews() {

        passwordError = (TextView) findViewById(R.id.password_error);
        confirmPasswordError = (TextView) findViewById(R.id.confirpassword_error);
        oldPasswordError = (TextView) findViewById(R.id.old_password_error);

        confirmPasswordEditText = (EditText) findViewById(R.id.confirm_pwd);
        passwordEditText = (EditText) findViewById(R.id.pwd);
        oldPasswordEditText = (EditText) findViewById(R.id.old_pwd);
        update_btn = (Button) findViewById(R.id.update_btn);

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkValidation();
            }
        });
    }

    public void checkValidation() {
        passwordError.setVisibility(View.INVISIBLE);
        confirmPasswordError.setVisibility(View.INVISIBLE);
        oldPasswordError.setVisibility(View.INVISIBLE);

        if (Utility.validatePassword(oldPasswordEditText)) {
            oldPasswordEditText.requestFocus();
            oldPasswordError.setVisibility(View.VISIBLE);
        } else if (Utility.validatePassword(passwordEditText)) {
            passwordEditText.requestFocus();
            passwordError.setVisibility(View.VISIBLE);
        } else if (Utility.validatePassword(confirmPasswordEditText) && !passwordEditText.getText().toString().equalsIgnoreCase(confirmPasswordEditText.getText().toString())) {
            confirmPasswordEditText.requestFocus();
            confirmPasswordError.setText("New Password not matched");
            confirmPasswordError.setVisibility(View.VISIBLE);
        } else {
            if (!Utility.isConnectingToInternet(this)) {
                Utility.alertDialog(UpdatePasswordActivity.this,
                        "No Internet Connection",
                        "Please check your internet connectivity and try again!");

            } else {
                HashMap<String, String> params = new HashMap<>();
                params.put("old_password", oldPasswordEditText.getText().toString());
                params.put("new_password", passwordEditText.getText().toString());
                params.put("from_source", "android");
                params.put("action", "change_password");
                ApiCall(params, 2);
            }


        }


    }

    public void ApiCall(Map<String, String> params, final int from) {

        RetrofitClient.getInstance().doBackProcess(UpdatePasswordActivity.this, params, "", new APIResponse() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onSuccess(String result) {
                try {

                    jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        Toast.makeText(UpdatePasswordActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {

                        Utility.alertDialog(UpdatePasswordActivity.this, "Alert", jsonObject.getString("message"));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utility.alertDialog(UpdatePasswordActivity.this, "Alert", e.getMessage());
                }
            }

            @Override
            public void onFailure(String res) {
                Utility.alertDialog(UpdatePasswordActivity.this, "Alert", res);
            }
        });
    }


}
