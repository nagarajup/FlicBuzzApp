package com.aniapps.flicbuzzapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;
import com.aniapps.flicbuzzapp.AppConstants;
import com.aniapps.flicbuzzapp.R;
import com.aniapps.flicbuzzapp.models.MyVideos;
import com.aniapps.flicbuzzapp.networkcall.APIResponse;
import com.aniapps.flicbuzzapp.networkcall.RetrofitClient;
import com.aniapps.flicbuzzapp.player.MyPlayer;
import com.aniapps.flicbuzzapp.utils.Utility;
import com.google.gson.Gson;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeeplinkBrowser extends AppConstants {
    ArrayList<MyVideos> itemsList = new ArrayList<MyVideos>();
    MyVideos myVideos, myVideos2;
    Uri data = null;
    String url = "";
    String push_title = "", push_msg = "", push_id = "", push_img_url = "",
            push_root_url = "", push_type = "", push_video_id = "", push_video_language = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Intent intent = getIntent();
        if (null != intent && null == intent.getData()) {
            push_video_id = intent.getStringExtra("push_video_id");
            push_video_language = intent.getStringExtra("push_video_language");
            ApiCall("notification");
        } else {
            data = intent.getData();
            ApiCall("url");
        }
    }


    JSONObject jsonObject;

    public void ApiCall(final String from) {
        HashMap<String, String> params = new HashMap<>();
        if (from.equals("url")) {
            params.put("action", "deeplinking_api");
            params.put("url", data.toString());
        } else {
            params.put("action", "get_video_by_id");
            params.put("video_id", push_video_id);
            params.put("language", push_video_language);
        }

        RetrofitClient.getInstance().doBackProcess(DeeplinkBrowser.this, params, "online", new APIResponse() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onSuccess(String result) {
                try {
                    jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONObject videodata = data.getJSONObject("video");
                        JSONArray next = data.getJSONArray("next");
                        if (from.equals("url")) {
                            push_video_language=jsonObject.getString("language");
                        }
                        MyVideos lead = new Gson().fromJson(videodata.toString(), MyVideos.class);
                        MyVideos lead2 = new Gson().fromJson(next.get(0).toString(), MyVideos.class);
                        itemsList = new ArrayList<MyVideos>();
                        itemsList.add(lead);
                        itemsList.add(lead2);
                        Intent player_in = new Intent(DeeplinkBrowser.this, MyPlayer.class);
                        player_in.putExtra("playingVideo", itemsList.get(0));
                        player_in.putExtra("sequence", itemsList);
                        player_in.putExtra("language", push_video_language);
                        startActivity(player_in);
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utility.alertDialog(DeeplinkBrowser.this, "Alert", e.getMessage());
                }
            }

            @Override
            public void onFailure(String res) {
                Utility.alertDialog(DeeplinkBrowser.this, "Alert", res);
            }
        });
    }


}
