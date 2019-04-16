package com.aniapps.flicbuzz.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import com.aniapps.flicbuzz.utils.PrefManager;
import com.aniapps.flicbuzz.utils.Utility;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    Button signUp, validateMobile;
    TextView emailError, passwordError, confirmPasswordError, dobError, cityError, mobileError, genderError, nameError, pincodeError, otpErro, resendOTP;
    EditText otpEditText, nameEditText, emailEditText, passwordEditText, confirmPasswordEditText, cityEditText, mobileEditText, pincodeEditText, dobEditText;
    JSONObject jsonObject;
    LinearLayout loginLL;
    RadioGroup gender;
    RadioButton male, female;
    RelativeLayout registerLL, otpLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.app_name));
        mToolbar.setNavigationIcon(R.drawable.arrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (registerLL.getVisibility() == View.VISIBLE) {
                    finish();
                } else {
                    registerLL.setVisibility(View.VISIBLE);
                    otpLL.setVisibility(View.GONE);
                }
            }
        });
        initViews();
    }

    void initViews() {
        registerLL = (RelativeLayout) findViewById(R.id.registerLL);
        otpLL = (RelativeLayout) findViewById(R.id.otpLL);
        nameError = (TextView) findViewById(R.id.name_error);
        emailError = (TextView) findViewById(R.id.email_error);
        passwordError = (TextView) findViewById(R.id.password_error);
        confirmPasswordError = (TextView) findViewById(R.id.confirpassword_error);
        mobileError = (TextView) findViewById(R.id.mobile_error);
        cityError = (TextView) findViewById(R.id.city_error);
        pincodeError = (TextView) findViewById(R.id.pincode_error);
        genderError = (TextView) findViewById(R.id.gender_error);
        dobError = (TextView) findViewById(R.id.dob_error);
        otpErro = (TextView) findViewById(R.id.otp_error);
        resendOTP = (TextView) findViewById(R.id.resendOTP);
        signUp = (Button) findViewById(R.id.signup_btn);
        validateMobile = (Button) findViewById(R.id.validate_otp_btn);
        nameEditText = (EditText) findViewById(R.id.full_name);
        emailEditText = (EditText) findViewById(R.id.email);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirm_pwd);
        passwordEditText = (EditText) findViewById(R.id.pwd);
        mobileEditText = (EditText) findViewById(R.id.mobile);
        cityEditText = (EditText) findViewById(R.id.city);
        pincodeEditText = (EditText) findViewById(R.id.pin);
        dobEditText = (EditText) findViewById(R.id.dob);
        otpEditText = (EditText) findViewById(R.id.ot_et);
        gender = (RadioGroup) findViewById(R.id.gender_radio);
        male = (RadioButton) findViewById(R.id.male_radio);
        female = (RadioButton) findViewById(R.id.female_radio);
        loginLL = (LinearLayout) findViewById(R.id.loginLL);
        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog(SignUpActivity.this, dobEditText, Calendar.getInstance());
            }
        });
        loginLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });
        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<>();
                params.put("mobile", mobileEditText.getText().toString());
                params.put("from_source", "android");
                params.put("action", "resend_otp");
                ApiCall(params, 3);
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
                    params.put("mobile", mobileEditText.getText().toString());
                    params.put("otp", otpEditText.getText().toString());
                    params.put("from_source", "android");
                    params.put("action", "verify_otp");
                    ApiCall(params, 1);
                }
            }
        });
    }

    public void checkValidation() {
        nameError.setVisibility(View.INVISIBLE);
        emailError.setVisibility(View.INVISIBLE);
        passwordError.setVisibility(View.INVISIBLE);
        confirmPasswordError.setVisibility(View.INVISIBLE);
        mobileError.setVisibility(View.INVISIBLE);
        cityError.setVisibility(View.INVISIBLE);
        pincodeError.setVisibility(View.INVISIBLE);
        dobError.setVisibility(View.INVISIBLE);
        genderError.setVisibility(View.INVISIBLE);

        if (Utility.hasName(nameEditText)) {
            nameEditText.requestFocus();
            nameError.setVisibility(View.VISIBLE);
        } else if (Utility.validateEmail(emailEditText)) {
            emailEditText.requestFocus();
            emailError.setVisibility(View.VISIBLE);
        } else if (Utility.hasMobileNumber(mobileEditText)) {
            mobileEditText.requestFocus();
            mobileError.setVisibility(View.VISIBLE);
        } else if (Utility.validatePassword(passwordEditText)) {
            passwordEditText.requestFocus();
            passwordError.setVisibility(View.VISIBLE);
        } else if (Utility.validatePassword(confirmPasswordEditText) && !passwordEditText.getText().toString().equalsIgnoreCase(confirmPasswordEditText.getText().toString())) {
            confirmPasswordEditText.requestFocus();
            confirmPasswordError.setText("Password not matched");
            confirmPasswordError.setVisibility(View.VISIBLE);
        } else if (Utility.hasName(cityEditText)) {
            cityEditText.requestFocus();
            cityError.setVisibility(View.VISIBLE);
        } else if (pincodeEditText.getText().toString().length() < 6) {
            pincodeEditText.requestFocus();
            pincodeError.setVisibility(View.VISIBLE);
        } else if (dobEditText.getText().toString().length() == 0) {
            dobEditText.requestFocus();
            dobError.setVisibility(View.VISIBLE);
        } else {
            if (!Utility.isConnectingToInternet(this)) {
                Utility.alertDialog(SignUpActivity.this,
                        "No Internet Connection",
                        "Please check your internet connectivity and try again!", "");

            } else {
                HashMap<String, String> params = new HashMap<>();
                params.put("password", passwordEditText.getText().toString());
                params.put("name", nameEditText.getText().toString());
                params.put("email", emailEditText.getText().toString());
                params.put("mobile", mobileEditText.getText().toString());
                params.put("city", cityEditText.getText().toString());
                params.put("pincode", pincodeEditText.getText().toString());
                params.put("dob", dobEditText.getText().toString());
                params.put("gender", ((RadioButton) findViewById(gender.getCheckedRadioButtonId())).getText().toString().toLowerCase());
                params.put("from_source", "android");
                params.put("action", "register");
                ApiCall(params, 2);
            }


        }


    }

    public void ApiCall(Map<String, String> params, final int from) {

        RetrofitClient.getInstance().doBackProcess(SignUpActivity.this, params, "", new APIResponse() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onSuccess(String result) {
                try {

                    jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        if (from == 2) {
                            Toast.makeText(SignUpActivity.this, jsonObject.getString("sms_details"), Toast.LENGTH_SHORT).show();
                            otpLL.setVisibility(View.VISIBLE);
                            registerLL.setVisibility(View.GONE);
                            countDown(resendOTP);
                        } else if (from == 1) {
                            Toast.makeText(SignUpActivity.this, "Otp Succussfully verified", Toast.LENGTH_SHORT).show();
                            PrefManager.getIn().setLogin(true);
                            Intent intent = new Intent(SignUpActivity.this, PaymentScreen.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else if (from == 3) {
                            Toast.makeText(SignUpActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (status == 13) {
                            Utility.alertDialog(SignUpActivity.this, "Alert", jsonObject.getString("message"), "13");
                        } else {
                            Utility.alertDialog(SignUpActivity.this, "Alert", jsonObject.getString("message"), "");

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utility.alertDialog(SignUpActivity.this, "Alert", e.getMessage(), "");
                }
            }

            @Override
            public void onFailure(String res) {
                Utility.alertDialog(SignUpActivity.this, "Alert", res, "");
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void countDown(final TextView mTextField) {
        validateMobile.setBackground(getDrawable(R.drawable.rounded_corners_grey));
        validateMobile.setEnabled(false);
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                mTextField.setText(millisUntilFinished / 1000 + " Sec");
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                mTextField.setText("Resend OTP");
                validateMobile.setBackground(getDrawable(R.drawable.rounded_corners_signup));
                validateMobile.setEnabled(true);
            }

        }.start();
    }

    public static void datePickerDialog(Context context, final EditText editText, final Calendar myCalendar) {
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                myCalendar.set(Calendar.YEAR, selectedyear);
                myCalendar.set(Calendar.MONTH, selectedmonth);
                myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                updateLabel(editText, myCalendar);
            }
        }, mYear, mMonth, mDay);

        mDatePicker.show();
    }


    private static void updateLabel(EditText view, final Calendar myCalendar) {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        view.setText(sdf.format(myCalendar.getTime()));

    }
}
