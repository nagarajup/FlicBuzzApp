package com.aniapps.flicbuzzapp.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.aniapps.flicbuzzapp.AppConstants;
import com.aniapps.flicbuzzapp.R;
import com.aniapps.flicbuzzapp.networkcall.APIResponse;
import com.aniapps.flicbuzzapp.networkcall.RetrofitClient;
import com.aniapps.flicbuzzapp.utils.*;
import com.squareup.picasso.Picasso;
import okhttp3.MultipartBody;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UpdateProfileActivity extends AppConstants {
    Button signUp;
    TextView emailError, dobError, cityError, mobileError, genderError, nameError, pincodeError;
    EditText nameEditText, emailEditText, cityEditText, mobileEditText, pincodeEditText, dobEditText;
    JSONObject jsonObject;
    RadioGroup gender;
    RadioButton male, female;
    ImageView profileImg;
    RelativeLayout profileImgLL;
    private static final int PERMISSION_REQUEST_CODE_CAMERA = 1;
    private static final int PERMISSION_REQUEST_CODE_GALLERy = 2;
    File f;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView header_title = (TextView) findViewById(R.id.tvheader);
        header_title.setText("My Profile");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();
    }

    void initViews() {
        profileImg = (CircleImageView) findViewById(R.id.profile_img);
        progressBar = (ProgressBar) findViewById(R.id.progress_one);
        profileImgLL = (RelativeLayout) findViewById(R.id.profile_img_ll);
        nameError = (TextView) findViewById(R.id.name_error);
        emailError = (TextView) findViewById(R.id.email_error);
        mobileError = (TextView) findViewById(R.id.mobile_error);
        cityError = (TextView) findViewById(R.id.city_error);
        pincodeError = (TextView) findViewById(R.id.pincode_error);
        genderError = (TextView) findViewById(R.id.gender_error);
        dobError = (TextView) findViewById(R.id.dob_error);
        signUp = (Button) findViewById(R.id.signup_btn);
        nameEditText = (EditText) findViewById(R.id.full_name);
        emailEditText = (EditText) findViewById(R.id.email);
        mobileEditText = (EditText) findViewById(R.id.mobile);
        cityEditText = (EditText) findViewById(R.id.city);
        pincodeEditText = (EditText) findViewById(R.id.pin);
        dobEditText = (EditText) findViewById(R.id.dob);
        gender = (RadioGroup) findViewById(R.id.gender_radio);
        male = (RadioButton) findViewById(R.id.male_radio);
        female = (RadioButton) findViewById(R.id.female_radio);
        nameEditText.setText(PrefManager.getIn().getName());
        emailEditText.setText(PrefManager.getIn().getEmail());
        mobileEditText.setText(PrefManager.getIn().getMobile());
        cityEditText.setText(PrefManager.getIn().getCity());
        pincodeEditText.setText(PrefManager.getIn().getPincode());
        dobEditText.setText(PrefManager.getIn().getDob());
        if(!PrefManager.getIn().getProfile_pic().equals("")){
            Picasso.with(UpdateProfileActivity.this)
                .load(PrefManager.getIn().getProfile_pic())
                    .fit().centerInside()
                    .error(R.mipmap.launcher_icon)
                    .into(profileImg);
        }
        if (PrefManager.getIn().getGender().equalsIgnoreCase("male")) {
            male.setChecked(true);
        } else {
            female.setChecked(true);
        }

        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dobEditText.getText().toString().trim().contains("-")) {
                    String date = dobEditText.getText().toString().trim();
                    String[] p = date.split("\\-");
                    datePickerDialog(UpdateProfileActivity.this, p[2], p[1], p[0], new DatePickerRes() {
                        @Override
                        public void datePickerRes(String res) {
                            dobEditText.setText(res);
                        }

                    });
                } else {
                    datePickerDialog(UpdateProfileActivity.this, "", "", "", new DatePickerRes() {
                        @Override
                        public void datePickerRes(String res) {
                            dobEditText.setText(res);
                        }
                    });
                }
                //datePickerDialog(UpdateProfileActivity.this, dobEditText, Calendar.getInstance());
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });
        profileImgLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getPermisionForCamera()) {
                    if (getPermissionForGallery()) {
                        selectImage();
                    }
                }
            }
        });

    }


    public void checkValidation() {
        nameError.setVisibility(View.INVISIBLE);
        emailError.setVisibility(View.INVISIBLE);
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
                Utility.alertDialog(UpdateProfileActivity.this,
                        "No Internet Connection",
                        "Please check your internet connectivity and try again!");

            } else {
                HashMap<String, String> params = new HashMap<>();
                params.put("name", nameEditText.getText().toString());
                params.put("email", emailEditText.getText().toString());
                params.put("mobile", mobileEditText.getText().toString());
                params.put("city", cityEditText.getText().toString());
                params.put("pincode", pincodeEditText.getText().toString());
                params.put("dob", dobEditText.getText().toString());
                params.put("gender", ((RadioButton) findViewById(gender.getCheckedRadioButtonId())).getText().toString().toLowerCase());
                params.put("from_source", "android");
                params.put("action", "update_profile");
                params.put("language", PrefManager.getIn().getLanguage().toLowerCase());
                ApiCall(params);
            }


        }


    }

    public void updateProfilePic(Map<String, String> params,String path) {
        progressBar.setVisibility(View.VISIBLE);
        ProgressRequestBody fileBody = new ProgressRequestBody(new File(path), new ProgressRequestBody.UploadCallbacks() {
            @Override
            public void onProgressUpdate(int percentage) {
                progressBar.setProgress(percentage);
            }

            @Override
            public void onError() {

            }

            @Override
            public void onFinish() {
                progressBar.setProgress(100);
            }
        });
        MultipartBody.Part filepart = MultipartBody.Part.createFormData("profile_pic", new File(path).getName(), fileBody);
        RetrofitClient.getInstance().getNoCryptResImages(UpdateProfileActivity.this, params, filepart, new APIResponse() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onSuccess(String result) {
                try {
                    progressBar.setProgress(100);
                    jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        PrefManager.getIn().setProfile_pic(jsonObject.getString("pic_url"));
                        Toast.makeText(UpdateProfileActivity.this, "Successfully profile pic updated.", Toast.LENGTH_SHORT).show();
                       // finish();
                    }else if(status==14){
                        Utility.alertDialog(UpdateProfileActivity.this,  jsonObject.getString("message"));
                    } else {
                        Utility.alertDialog(UpdateProfileActivity.this, "Alert", jsonObject.getString("message"));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utility.alertDialog(UpdateProfileActivity.this, "Alert", e.getMessage());
                }
            }

            @Override
            public void onFailure(String res) {
                Utility.alertDialog(UpdateProfileActivity.this, "Alert", res);
            }
        });
    }



    public void ApiCall(Map<String, String> params) {

        RetrofitClient.getInstance().doBackProcess(UpdateProfileActivity.this, params, "", new APIResponse() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onSuccess(String result) {
                try {

                    jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        trackEvent(UpdateProfileActivity.this,"MainPage","My Profile|Update Profile");
                        PrefManager.getIn().setName(nameEditText.getText().toString());
                        PrefManager.getIn().setEmail(emailEditText.getText().toString());
                        PrefManager.getIn().setMobile(mobileEditText.getText().toString());
                        PrefManager.getIn().setGender(((RadioButton) findViewById(gender.getCheckedRadioButtonId())).getText().toString().toLowerCase());
                        PrefManager.getIn().setCity(cityEditText.getText().toString());
                        PrefManager.getIn().setPincode(pincodeEditText.getText().toString());
                        PrefManager.getIn().setDob(dobEditText.getText().toString());
                        Toast.makeText(UpdateProfileActivity.this, jsonObject.getString("details"), Toast.LENGTH_SHORT).show();
                        finish();
                    } else if(status==14){
                        Utility.alertDialog(UpdateProfileActivity.this,  jsonObject.getString("message"));
                    }else {
                        Utility.alertDialog(UpdateProfileActivity.this, "Alert", jsonObject.getString("message"));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utility.alertDialog(UpdateProfileActivity.this, "Alert", e.getMessage());
                }
            }

            @Override
            public void onFailure(String res) {
                Utility.alertDialog(UpdateProfileActivity.this, "Alert", res);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean getPermisionForCamera() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI

            }

            requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE_CAMERA);

        } else {
            getPermissionForGallery();
        }

        return false;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean getPermissionForGallery() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI

            }

            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE_GALLERy);

        } else {
            selectImage();
        }
        return false;
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    f = new File(android.os.Environment.getExternalStorageDirectory(), "profile_img.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);
                    profileImg.setImageBitmap(bitmap);
                    HashMap<String, String> params = new HashMap<>();
                    params.put("from_source", "android");
                    params.put("action", "upload_profile_pic");
                    updateProfilePic(params,f.getAbsolutePath());
                }

                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    profileImg.setImageURI(selectedImage);
                    HashMap<String, String> params = new HashMap<>();
                    params.put("from_source", "android");
                    params.put("action", "upload_profile_pic");
                    updateProfilePic(params,picturePath);
                }
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_CAMERA:
                // Make sure it's our original READ_CONTACTS request
                if (requestCode == PERMISSION_REQUEST_CODE_CAMERA) {
                    if (grantResults.length == 1 &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        getPermissionForGallery();
                    }
                } else {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
                break;
            case PERMISSION_REQUEST_CODE_GALLERy:
                // Make sure it's our original READ_CONTACTS request
                if (requestCode == PERMISSION_REQUEST_CODE_GALLERy) {
                    if (grantResults.length == 1 &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        selectImage();

                    }
                } else {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
                break;

            default:

                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;

        }
    }


    public void datePickerDialog(Context context, String date, String month,
                                 String year, final DatePickerRes result) {
        final Dialog date_picker = new Dialog(context, R.style.ThemeDialogCustom);
        date_picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        date_picker.setContentView(R.layout.custom_layout_datepickr);
        date_picker.setCancelable(false);
        final DatePicker dt = date_picker.findViewById(R.id.datePicker1);
        Button set_date = date_picker.findViewById(R.id.button1);
        Button cancel_date = date_picker.findViewById(R.id.button2);
        if (date.length() > 0 && month.length() > 0 && year.length() > 0) {
            dt.updateDate(Integer.parseInt(year), Integer.parseInt(month) - 1,
                    Integer.parseInt(date));
        }

        set_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dt.clearFocus();
                int m = dt.getMonth();
                int day = dt.getDayOfMonth();
                int year = dt.getYear();
                int mnth = m + 1;
                String month = String.valueOf(mnth);
                if (month.length() < 2) {
                    month = "0" + month;
                } else {
                    month = "" + month;
                }
                String date = String.valueOf(day);
                if (date.length() < 2) {
                    date = "0" + date;
                } else {
                    date = "" + date;
                }
                result.datePickerRes(year + "-" + month + "-" + date);
                date_picker.dismiss();
            }
        });

        cancel_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // result.datePickerRes("");
                date_picker.dismiss();
            }
        });
        date_picker.show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
        return super.onOptionsItemSelected(item);
    }
}
