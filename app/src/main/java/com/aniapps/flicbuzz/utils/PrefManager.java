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

    public void saveDeviceId(String id) {
        editor.putString("device_id", id);
        editor.apply();
    }

    public String getDeviceId() {
        return pref.getString("device_id", "");
    }
    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public static PrefManager getIn() {
        if (uniqInstance == null) {
            uniqInstance = new PrefManager();
            pref = PreferenceManager.getDefaultSharedPreferences(AppApplication.app_ctx);
        }
        editor = pref.edit();
        return uniqInstance;
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

}
