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

    public void setLanguage(String language){
        editor.putString("language",language);
        editor.commit();
    }
    public String getLanguage() {
        return pref.getString("language", "hindi");
    }



}
