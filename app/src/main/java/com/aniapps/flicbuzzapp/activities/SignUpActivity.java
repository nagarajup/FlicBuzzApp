package com.aniapps.flicbuzzapp.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
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
    String user_id = "";
    ImageView psw_confirm_img_eye,psw_img_eye;
    boolean check_visibility=true,check_visibility_confirm=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView header_title = (TextView) findViewById(R.id.title);
        header_title.setText("SignUp with Email");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
    }

    @Override
    public void onBackPressed() {
        if (handler != null) {
            if (runnable != null)
                handler.removeCallbacks(runnable);
        }
        if (registerLL.getVisibility() == View.VISIBLE) {
            finish();
            overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
        } else {
            registerLL.setVisibility(View.VISIBLE);
            otpLL.setVisibility(View.GONE);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(handler!=null){
            if(runnable!=null)
                handler.removeCallbacks(runnable);
        }
        if (registerLL.getVisibility() == View.VISIBLE) {
            finish();
            overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
        } else {
            registerLL.setVisibility(View.VISIBLE);
            otpLL.setVisibility(View.GONE);
        }
        return super.onOptionsItemSelected(item);
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
        psw_img_eye = (ImageView)findViewById(R.id.psw_img_eye);
        psw_confirm_img_eye = (ImageView)findViewById(R.id.psw_confirm_img_eye);
        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog(SignUpActivity.this, dobEditText, Calendar.getInstance());
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

        loginLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
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
                if (resendOTP.getText().toString().equalsIgnoreCase("Resend OTP")) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("mobile", mobileEditText.getText().toString());
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
                    if (handler != null) {
                        if (runnable != null) {
                            handler.removeCallbacks(runnable);
                        }
                        resendOTP.setText("Resend OTP");
                    }
                    otpErro.setVisibility(View.GONE);
                    HashMap<String, String> params = new HashMap<>();
                    params.put("mobile", mobileEditText.getText().toString());
                    params.put("otp", otpEditText.getText().toString());
                    params.put("user_id", user_id);
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
                        "Please check your internet connectivity and try again!");

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

                    if (from == 2) {
                        if (status == 1 || status == 14) {
                            user_id = jsonObject.getString("user_id");
                            PrefManager.getIn().saveUserId(jsonObject.getString("user_id"));
                            if (status == 14) {
                                Toast.makeText(SignUpActivity.this, "Otp sent succussfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUpActivity.this, jsonObject.getString("sms_details"), Toast.LENGTH_SHORT).show();
                            }
                            otpLL.setVisibility(View.VISIBLE);
                            registerLL.setVisibility(View.GONE);
                            countDown(resendOTP);
                        } else {
                            Utility.alertDialog(SignUpActivity.this, "Alert", jsonObject.getString("message"));
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
                                Intent intent = new Intent(SignUpActivity.this, LandingPage.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                            } else {
                                Intent intent = new Intent(SignUpActivity.this, PaymentScreen_New.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                            }
                        } else {
                            Utility.alertDialog(SignUpActivity.this, "Alert", jsonObject.getString("message"));
                        }

                    } else {
                        Toast.makeText(SignUpActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        // Utility.alertDialog(SignUpActivity.this, "Alert", jsonObject.getString("message"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Utility.alertDialog(SignUpActivity.this, "Alert", e.getMessage());
                }
            }

            @Override
            public void onFailure(String res) {
                Utility.alertDialog(SignUpActivity.this, "Alert", res);
            }
        });
    }

    int tot_interval = 15000; // 10 secs
    int interval = 1000; // 10 secs
    Runnable runnable;
    Handler handler;

    public void countDown(final TextView mTextField) {
        // validateMobile.setBackground(getDrawable(R.drawable.rounded_corners_grey));
        // validateMobile.setEnabled(false);


        handler = new Handler();
        runnable = new Runnable() {
            public void run() {

                if (tot_interval != 0) {
                    mTextField.setText(tot_interval / 1000 + " Sec");
                    tot_interval = tot_interval - interval;
                } else {
                    mTextField.setText("Resend OTP");
                    handler.removeCallbacks(runnable);
                }
                handler.postDelayed(runnable, interval);
                // your code here
            }
        };
        handler.postDelayed(runnable, interval);

       /* timer = new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                mTextField.setText(millisUntilFinished / 1000 + " Sec");
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                mTextField.setText("Resend OTP");
               // validateMobile.setBackground(getDrawable(R.drawable.rounded_corners_signup));
              //  validateMobile.setEnabled(true);
            }

        };
        timer.start();*/
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
