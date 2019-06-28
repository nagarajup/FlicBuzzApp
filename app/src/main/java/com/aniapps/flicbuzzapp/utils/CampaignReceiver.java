package com.aniapps.flicbuzzapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CampaignReceiver extends BroadcastReceiver {
    private static final String TAG = CampaignReceiver.class.getSimpleName();
   /* private static final Logger LOGGER = LoggerFactory.getLogger(TAG);
    private static final Marker MARKER = MarkerFactory.getMarker(TAG);*/


    @Override
    public void onReceive(Context context, final Intent intentx) {
      //  LOGGER.info(MARKER, "on Receive called");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    for (String key : intentx.getExtras().keySet()) {
                        try {
                            Log.e("LINK", key + " => " + String.valueOf(intentx.getExtras().get(key)));
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
}
