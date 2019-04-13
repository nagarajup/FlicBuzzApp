package com.aniapps.flicbuzz;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.provider.Settings;
import com.aniapps.flicbuzz.utils.PrefManager;

public class AppApplication extends Application {
    public static Context app_ctx;

    @Override
    public void onCreate() {
        super.onCreate();
        app_ctx = getApplicationContext();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (PrefManager.getIn().getDeviceId().equals("")) {
            PrefManager.getIn().saveDeviceId(Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Runtime.getRuntime().gc();
    }


}
