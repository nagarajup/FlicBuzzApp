package com.aniapps.flicbuzz;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
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
        initChannel();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Runtime.getRuntime().gc();
    }
    public void initChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(new NotificationChannel("CHID_CallDetector", "Caller Id Service", NotificationManager.IMPORTANCE_DEFAULT));
        }
    }

}
