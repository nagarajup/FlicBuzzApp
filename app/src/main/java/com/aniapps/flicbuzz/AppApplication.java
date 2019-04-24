package com.aniapps.flicbuzz;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.widget.ImageView;
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
    static float imageheight = 0;
    public static void myImgeRes(int cal,Context context,ImageView imageView){
        if (cal == 0) {
            cal = 1;
        imageView.getLayoutParams().height = (int) ((float) ((context
                    .getResources().getDisplayMetrics().widthPixels-
                    dpToPx(10, context)) ) / 1.77);
            imageheight = (int) ((float) ((context
                    .getResources().getDisplayMetrics().widthPixels-
                    dpToPx(10, context)) ) / 1.77);
        } else {
        imageView.getLayoutParams().height = (int) imageheight;
        }
    }

    public static int dpToPx(int dp, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale);
    }

}
