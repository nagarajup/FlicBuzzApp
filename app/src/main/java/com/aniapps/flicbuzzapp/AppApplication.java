package com.aniapps.flicbuzzapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import com.aniapps.flicbuzzapp.utils.AppSignatureHelper;
import com.aniapps.flicbuzzapp.utils.PrefManager;
import com.razorpay.Checkout;
import io.branch.referral.Branch;

import java.util.Map;

public class AppApplication extends Application {
    public static Context app_ctx;
    private static final String AF_DEV_KEY = "airwMEJ3yKQD3aGFGBsc7D";

    @Override
    public void onCreate() {
        super.onCreate();
        app_ctx = getApplicationContext();
        Checkout.preload(getApplicationContext());
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        // Branch logging for debugging
        Branch.enableTestMode();

        // Branch object initialization
        Branch.getAutoInstance(this);
        if (PrefManager.getIn().getDeviceId().equals("")) {
            PrefManager.getIn().saveDeviceId(Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
        }
        //Log.e("###Deviceid", "" + PrefManager.getIn().getDeviceId());
        Log.e("OTP HASH","###"+new AppSignatureHelper(app_ctx).getAppSignatures().get(0));
        initChannel();
        /*AppsFlyerConversionListener conversionDataListener =
                new AppsFlyerConversionListener() {

                    @Override
                    public void onInstallConversionDataLoaded(Map<String, String> map) {

                    }

                    @Override
                    public void onInstallConversionFailure(String s) {

                    }

                    @Override
                    public void onAppOpenAttribution(Map<String, String> map) {

                    }

                    @Override
                    public void onAttributionFailure(String s) {

                    }
                };
        AppsFlyerLib.getInstance().setDebugLog(true);
        AppsFlyerLib.getInstance().init(AF_DEV_KEY, conversionDataListener, getApplicationContext());
        AppsFlyerLib.getInstance().startTracking(this);*/

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

    public static void myImgeRes(int cal, Context context, ImageView imageView) {
        if (cal == 0) {
            cal = 1;
            imageView.getLayoutParams().height = (int) ((float) ((context
                    .getResources().getDisplayMetrics().widthPixels -
                    dpToPx(10, context))) / 1.77);
            imageheight = (int) ((float) ((context
                    .getResources().getDisplayMetrics().widthPixels -
                    dpToPx(10, context))) / 1.77);
        } else {
            imageView.getLayoutParams().height = (int) imageheight;
        }
    }

    public static int dpToPx(int dp, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale);
    }

}
