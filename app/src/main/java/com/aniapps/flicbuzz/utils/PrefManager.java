package com.aniapps.flicbuzz.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.aniapps.flicbuzz.AppApplication;

/**
 * Created by Lincoln on 05/05/16.
 */
public class PrefManager {
    static SharedPreferences pref;
    static SharedPreferences.Editor editor;
    private static PrefManager uniqInstance;

    private static final String PREF_NAME = "flicbuzz";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public static PrefManager getIn() {
        if (uniqInstance == null) {
            uniqInstance = new PrefManager();
            pref = PreferenceManager.getDefaultSharedPreferences(AppApplication.app_ctx);
        }
        editor = pref.edit();
        return uniqInstance;
    }

    public void saveDeviceId(String id) {
        editor.putString("device_id", id);
        editor.apply();
    }

    public String getUserId() {
        return pref.getString("user_id", "");
    }

    public void saveUserId(String id) {
        editor.putString("user_id", id);
        editor.apply();
    }

    public String getDeviceId() {
        return pref.getString("device_id", "");
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setLogin(boolean login) {
        editor.putBoolean("login", login);
        editor.commit();
    }

    public boolean getPackage() {
        return pref.getBoolean("package", false);
    }

    public void setPackage(boolean login) {
        editor.putBoolean("package", login);
        editor.commit();
    }

    public boolean getLogin() {
        return pref.getBoolean("login", false);
    }

    public void setLanguage(String language) {
        editor.putString("language", language);
        editor.commit();
    }

    public String getLanguage() {
        return pref.getString("language", "Hindi");
    }

    public String payment_status = "";
    public String plan_type = "";
    public String payment_data = "";

    public String getPayment_mode() {
        return pref.getString("payment_mode", "");
    }

    public void setPayment_mode(String payment_mode) {
        editor.putString("payment_mode", payment_mode);
        editor.commit();
    }

    public String payment_mode = "";

    public String getPlan() {
        return pref.getString("plan", "");
    }

    public void setPlan(String plan) {
        editor.putString("plan", plan);
        editor.commit();
    }

    public String plan = "";


    public String getPlan_type() {
        return pref.getString("plan_type", "");
    }

    public void setPlan_type(String plan_type) {
        editor.putString("plan_type", plan_type);
        editor.commit();
    }

    public String getPayment_data() {
        return pref.getString("payment_data", "");
    }

    public void setPayment_data(String payment_data) {
        editor.putString("payment_data", payment_data);
        editor.commit();
    }

    public String getSubscription_start_date() {
        return pref.getString("subscription_start_date", "");
    }

    public void setSubscription_start_date(String subscription_start_date) {
        editor.putString("subscription_start_date", subscription_start_date);
        editor.commit();
    }

    public String getSubscription_end_date() {
        return pref.getString("subscription_end_date", "");
    }

    public void setSubscription_end_date(String subscription_end_date) {
        editor.putString("subscription_end_date", subscription_end_date);
        editor.commit();
    }

    public String getName() {
        return pref.getString("name", "");
    }

    public void setName(String name) {
        editor.putString("name", name);
        editor.commit();
    }

    public String getEmail() {
        return pref.getString("email", "");
    }

    public void setEmail(String email) {
        editor.putString("email", email);
        editor.commit();
    }

    public String getMobile() {
        return pref.getString("mobile", "");
    }

    public void setMobile(String mobile) {
        editor.putString("mobile", mobile);
        editor.commit();
    }

    public String getGender() {
        return pref.getString("gender", "");
    }

    public void setGender(String gender) {
        editor.putString("gender", gender);
        editor.commit();
    }

    public String getCity() {
        return pref.getString("city", "");
    }

    public void setCity(String city) {
        editor.putString("city", city);
        editor.commit();
    }

    public String getPincode() {
        return pref.getString("pincode", "");
    }

    public void setPincode(String pincode) {
        editor.putString("pincode", pincode);
        editor.commit();
    }

    public String getDob() {
        return pref.getString("dob", "");
    }

    public void setDob(String dob) {
        editor.putString("dob", dob);
        editor.commit();
    }

    public String subscription_start_date = "";
    public String subscription_end_date = "";

    public String getServer_date_time() {
        return pref.getString("server_date_time", "");
    }

    public void setServer_date_time(String server_date_time) {
        editor.putString("server_date_time", server_date_time);
        editor.commit();
    }

    public String server_date_time = "";

    public String getSubscription_renewal_date() {
        return pref.getString("subscription_renewal_date", "");
    }

    public void setSubscription_renewal_date(String subscription_renewal_date) {
        editor.putString("subscription_renewal_date", subscription_renewal_date);
        editor.commit();
    }

    public String subscription_renewal_date = "";
    public String name = "";
    public String email = "";
    public String mobile = "";
    public String gender = "";
    public String city = "";
    public String pincode = "";
    public String dob = "";

    public String getProfile_pic() {
        return pref.getString("profile_pic", "");
    }

    public void setProfile_pic(String profile_pic) {
        editor.putString("profile_pic", profile_pic);
        editor.commit();
    }

    public String profile_pic = "";


}
