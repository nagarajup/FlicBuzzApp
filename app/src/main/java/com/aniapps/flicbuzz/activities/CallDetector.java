package com.aniapps.flicbuzz.activities;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.aniapps.flicbuzz.BuildConfig;
import com.aniapps.flicbuzz.R;
import com.aniapps.flicbuzz.networkcall.APIService;
import com.aniapps.flicbuzz.networkcall.RetrofitConverter;

import okhttp3.OkHttpClient;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by NagRaj_Pilla on 6/21/2017.
 * call detection service
 */

public class CallDetector extends Service {
    private boolean isRunning = false;
    String mytag = "";
    private static Retrofit retrofit = null;

    public CallDetector() {
        super();
    }

    @Override
    public void onCreate() {
        isRunning = true;
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                   "CHID_CallDetector");
            builder.setWhen(System.currentTimeMillis())
                    .setContentTitle("Caller ID")
                    .setContentText("Call Status")
                    .setSmallIcon(R.mipmap.ic_launcher);
            startForeground(20, builder.build());
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent.getExtras()) {
            mytag = intent.getExtras().getString("search_tag");
        } else {
            stopSelf();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    if (netCheck()) {
                        BuyLeadCheck(mytag);
                    } else {
                        isRunning = false;
                       stopSelf();
                    }
                }
                stopSelf();
            }
        }).start();
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        stopSelf();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private boolean netCheck() {
        ConnectivityManager cm = (ConnectivityManager) this
                .getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private static Retrofit getClient(Context context) {
        if (retrofit == null) {
            OkHttpClient okHttpClient = null;

            okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(90, TimeUnit.SECONDS)
                    .connectTimeout(90, TimeUnit.SECONDS)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(context.getResources().getString(R.string.base))
                    .addConverterFactory(new RetrofitConverter())
                    .client(okHttpClient)
                    .build();
            //  Log.e("##Retrofit##", "WITHOUT CRT");

        }
        return retrofit;
    }

    private void BuyLeadCheck(String mytag) {
        Log.e("@#####@",""+mytag);
        Map<String, String> params = new HashMap<>();
        params.put("version_code", "" + BuildConfig.VERSION_CODE);
        params.put("keyword",  mytag);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(CallDetector.this);
        params.put("user_id", "" + settings.getString("user_id",""));
        params.put("device_id", settings.getString("device_id", ""));
        params.put("language", settings.getString("language", "").toLowerCase());
        params.put("action", "autosuggest");
        Log.e("####","POST"+params);
       // params.put("from", "android");
        APIService apiService = getClient(getApplicationContext()).create(APIService.class);
        apiService.coreApiResult("https://www.flicbuzz.com/apis/autosuggest", params).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> res) {

                if (res.isSuccessful()) {
                    if (null != res.body() && !res.body().equals("")) {
                        Log.e("####","RES"+res.body().toString());
                        try {
                            JSONObject obj = new JSONObject(res.body());
                            int status = obj.getInt("status");
                            if (status == 1) {
                                sendBroadcast(new Intent("METHOD_GET_SEARCH_SUGGESTION")
                                        .putExtra("response", res.body()));

                                /*JSONObject object=obj.getJSONObject("fields");
                                String access = Pref.getIn().getBuy_access();
                                String sync_status = Pref.getIn().getSync_done();
                                String phone_click = Pref.getIn().getPhoneClick();
                                if (access.equals("y") && sync_status.equals("y") && object.getString("mobile_clicked").equals("y")) {
                                    contextInitialized(CallDetector.this, obj, "buylead");
                                }*/
                                isRunning = false;
                                stopSelf();
                            } else {
                                isRunning = false;
                                stopSelf();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            isRunning = false;
                            stopSelf();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                isRunning = false;
                stopSelf();
            }
        });
    }

   /* public void contextInitialized(final Context ctx, final JSONObject obj, final String from) {
        Timer timer = new Timer();
        TimerTask delayedThreadStartTask = new TimerTask() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject fields = obj.getJSONObject("fields");
                            String lead_id = fields.getString("id");
                            Intent in = new Intent(CallDetector.this, from.equals("buylead") ? CallStatusUpdate.class : ConsumerCallStatus.class);
                            in.putExtra("lead_id", "" + lead_id);
                            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ctx.startActivity(in);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        };
        timer.schedule(delayedThreadStartTask, 100);
    }*/
}
