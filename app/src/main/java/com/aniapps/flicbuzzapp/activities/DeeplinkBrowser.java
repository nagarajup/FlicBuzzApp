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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeeplinkBrowser extends AppConstants {
    ArrayList<MyVideos> itemsList = new ArrayList<MyVideos>();
    MyVideos myVideos, myVideos2;
    Uri data = null;
    String url="";
    String push_title = "", push_msg = "", push_id = "", push_img_url = "",
            push_root_url = "", push_type = "", push_video_id = "", push_video_language = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Intent intent = getIntent();
            if (null != intent && null==intent.getData()) {
                push_id = intent.getStringExtra("push_id");
                switch (intent.getStringExtra("push_id")) {
                /*case "0":
                    data = intent.getData();

                   *//* Log.e("#DEEPLINK#", "data" + data);
                    myVideos = new MyVideos("122", "क्या सलमान का 'बिग बॉस 13' मेकओवर के लिए है तैयार?", "बिग बॉस 13' को शुरू होने में अभी कुछ महीने बचे हैं, लेकिन ऐसा सुनने को मिल रहा है कि इसमें कुछ बदलाव दर्शकों को देखने को मिल सकता है।",
                            "TV And Bollywood News", "1", "https://www.flicbuzz.com/vendor_videos/converted/vendor_3/video_hi_122_20190524_1906.mp4.m3u8", "https://www.flicbuzz.com/browse/hindi/N2Y0Q2NDbThxNEtkbjEySkppdkZ2dz09",
                            "https://www.flicbuzz.com/vendor_videos/vendor_3/image_hindi_122_500x280_20190524_190814.jpg", 200, "n", "बिग बॉस 13' को शुरू होने में अभी कुछ महीने बचे हैं, लेकिन ऐसा सुनने को मिल रहा है कि", "2019-05-24 00:50:00");
                    myVideos2 = new MyVideos("122", "क्या सलमान का 'बिग बॉस 13' मेकओवर के लिए है तैयार?", "बिग बॉस 13' को शुरू होने में अभी कुछ महीने बचे हैं, लेकिन ऐसा सुनने को मिल रहा है कि इसमें कुछ बदलाव दर्शकों को देखने को मिल सकता है।",
                            "TV And Bollywood News", "1", "https://www.flicbuzz.com/vendor_videos/converted/vendor_3/video_hi_122_20190524_1906.mp4.m3u8", "https://www.flicbuzz.com/browse/hindi/N2Y0Q2NDbThxNEtkbjEySkppdkZ2dz09",
                            "https://www.flicbuzz.com/vendor_videos/vendor_3/image_hindi_122_500x280_20190524_190814.jpg", 200, "n", "बिग बॉस 13' को शुरू होने में अभी कुछ महीने बचे हैं, लेकिन ऐसा सुनने को मिल रहा है कि", "2019-05-24 00:50:00");

                    itemsList.add(myVideos);
                    itemsList.add(myVideos2);

                    Intent player_in = new Intent(DeeplinkBrowser.this, MyPlayer.class);
                    player_in.putExtra("playingVideo", myVideos);
                    player_in.putExtra("from", "deeplinking");
                    player_in.putExtra("sequence", itemsList);
                    player_in.putExtra("pos", 1);
                    finish();
                    startActivity(player_in);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);*//*
                    break;*/
                    case "1":
                        push_video_id = intent.getStringExtra("push_video_id");
                        push_video_language = intent.getStringExtra("push_video_language");
                        ApiCall("");
                        break;
                    case "2":
                        break;
                    case "3":
                        break;

                }
            } else {
                data = intent.getData();
                ApiCall("url");
            }
        }


    JSONObject jsonObject;

    public void ApiCall(String from) {
        HashMap<String, String> params = new HashMap<>();

        // params.put("video_id", push_video_id);
        if(from.equals("url")){
            params.put("action", "deeplinking_api");
            params.put("url", data.toString());
        }else {
            params.put("action", "get_video_by_id");
            params.put("video_id", "49");
            params.put("language", "hindi");
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
                        MyVideos lead = new Gson().fromJson(videodata.toString(), MyVideos.class);

                        itemsList = new ArrayList<MyVideos>();
                        itemsList.add(lead);
                        Intent player_in = new Intent(DeeplinkBrowser.this, MyPlayer.class);
                        player_in.putExtra("playingVideo", lead);
                        player_in.putExtra("sequence", itemsList);
                        player_in.putExtra("from", push_id);
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
