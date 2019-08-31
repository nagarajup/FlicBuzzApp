package com.aniapps.flicbuzzapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.RequiresApi;
import com.aniapps.flicbuzzapp.AppConstants;
import com.aniapps.flicbuzzapp.activities.IntroductionScreen;
import com.aniapps.flicbuzzapp.networkcall.APIResponse;
import com.aniapps.flicbuzzapp.networkcall.RetrofitClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CampaignReceiver extends BroadcastReceiver {
    private static final String TAG = CampaignReceiver.class.getSimpleName();
   /* private static final Logger LOGGER = LoggerFactory.getLogger(TAG);
    private static final Marker MARKER = MarkerFactory.getMarker(TAG);*/


    @Override
    public void onReceive(final Context context, final Intent intentx) {
      //  LOGGER.info(MARKER, "on Receive called");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    for (String key : intentx.getExtras().keySet()) {
                        try {
                            Log.e("LINK", key + " => " + String.valueOf(intentx.getExtras().get(key)));
                            TrackReferral(context,String.valueOf(intentx.getExtras().get(key)));
                        } catch (Exception e) {
                            Log.e("LINK", "caught exception in on key retrieval ", e);
                        }
                    }
                } catch (Exception e) {
                    Log.e("LINK", "caught exception in key loop ", e);
                }
            }
        });
        executorService.shutdown();
    }

    public void TrackReferral(final Context context, final String referrer) {
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "track_referral");
        params.put("referral_from","Google_Tracker");
        params.put("referral_code", referrer);
        params.put("language", PrefManager.getIn().getLanguage().toLowerCase());
        RetrofitClient.getInstance().doBackProcess(context, params, "online", new APIResponse() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    Log.e("RES", "Referrer" + result);
                    FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
                    mFirebaseAnalytics.setUserProperty("user_id", PrefManager.getIn().getMobile());
                    mFirebaseAnalytics.setUserId(PrefManager.getIn().getMobile());
                    Bundle bundle = new Bundle();
                    bundle.putString("Category", "CampaignReceiver");
                    bundle.putString("Action", ""+referrer);
                    mFirebaseAnalytics.logEvent("my_events", bundle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String res) {
            }
        });
    }
}
