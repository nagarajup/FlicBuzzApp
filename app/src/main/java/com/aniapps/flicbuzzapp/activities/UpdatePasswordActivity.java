package com.aniapps.flicbuzzapp.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.aniapps.flicbuzzapp.AppConstants;
import com.aniapps.flicbuzzapp.R;
import com.aniapps.flicbuzzapp.networkcall.APIResponse;
import com.aniapps.flicbuzzapp.networkcall.RetrofitClient;
import com.aniapps.flicbuzzapp.utils.PrefManager;
import com.aniapps.flicbuzzapp.utils.Utility;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdatePasswordActivity extends AppConstants {
    Button update_btn;
    TextView passwordError, confirmPasswordError, oldPasswordError;
    EditText passwordEditText, confirmPasswordEditText, oldPasswordEditText;
    JSONObject jsonObject;
    ImageView psw_confirm_img_eye,psw_img_eye,psw_old_img_eye;
    boolean check_visibility=true,check_visibility_confirm=true,check_old_visibility=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView  header_title = (TextView) findViewById(R.id.tvheader);
        header_title.setText("Change Password");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
    void initViews() {

        passwordError = (TextView) findViewById(R.id.password_error);
        confirmPasswordError = (TextView) findViewById(R.id.confirpassword_error);
        oldPasswordError = (TextView) findViewById(R.id.old_password_error);

        confirmPasswordEditText = (EditText) findViewById(R.id.confirm_pwd);
        passwordEditText = (EditText) findViewById(R.id.pwd);
        oldPasswordEditText = (EditText) findViewById(R.id.old_pwd);
        update_btn = (Button) findViewById(R.id.update_btn);
        psw_img_eye = (ImageView)findViewById(R.id.psw_new_img_eye);
        psw_confirm_img_eye = (ImageView)findViewById(R.id.psw_confirm_img_eye);
        psw_old_img_eye = (ImageView)findViewById(R.id.psw_old_img_eye);
        psw_old_img_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check_old_visibility) {
                    oldPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                    psw_old_img_eye.setImageResource(R.mipmap.password_visible);
                    oldPasswordEditText.setSelection(oldPasswordEditText.getText().length());
                    check_old_visibility = false;
                } else {
                    oldPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    psw_old_img_eye.setImageResource(R.mipmap.password_invisible);
                    oldPasswordEditText.setSelection(oldPasswordEditText.getText().length());
                    check_old_visibility = true;
                }
            }
        });
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
                params.put("user_id", PrefManager.getIn().getUserId());
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
                        trackEvent(UpdatePasswordActivity.this,"MainPage","Change Password|Update Password");
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
