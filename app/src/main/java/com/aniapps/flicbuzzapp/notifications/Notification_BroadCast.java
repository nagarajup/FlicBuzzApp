package com.aniapps.flicbuzzapp.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.aniapps.flicbuzzapp.activities.SignIn;
import com.aniapps.flicbuzzapp.activities.Spalsh;
import com.aniapps.flicbuzzapp.player.LandingPage;
import com.aniapps.flicbuzzapp.utils.PrefManager;


public class Notification_BroadCast extends BroadcastReceiver {
    String push_title = "", push_msg = "", push_id = "", push_name = "", push_img_url = "",
            push_root_url = "";
    String dealer_id="";
    private boolean remember;

    @Override
    public void onReceive(Context context, Intent intent) {
        //notification id
        push_title = intent.getStringExtra("push_title");
        // message
        push_msg = intent.getStringExtra("push_msg");
        // push type
        push_id = intent.getStringExtra("push_id");
        // image url
        push_img_url = intent.getStringExtra("push_img_url");
        // root url OEM Consumer Offers
        push_root_url = intent.getStringExtra("push_root_url");


        Log.e("#FCM111#","push_id"+push_id);
        Log.e("#FCM111#","push_title"+push_title);
        Log.e("#FCM111#","push_msg"+push_msg);
        Log.e("#FCM111#","push_img_url"+push_img_url);
        Log.e("#FCM111#","push_root_url"+push_root_url);

        Intent notificationIntent;
        dealer_id =  PrefManager.getIn().getUserId();
        remember = PrefManager.getIn().getLogin();

        if (push_id != null && !push_id.equals("")) {
            switch (push_id) {
                //Start App
                case "1":
                    try {
                        notificationIntent = new Intent(context,
                                Spalsh.class);
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        notificationIntent.putExtra("url", push_root_url);
                        context.startActivity(notificationIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                // Play Store
                case "2":
                    final String appPackageName = context.getPackageName();
                    try {
                        notificationIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + appPackageName));
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(notificationIntent);
                    } catch (android.content.ActivityNotFoundException anfe) {
                        notificationIntent = new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id="
                                        + appPackageName));
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(notificationIntent);
                    }
                    break;
                // Web View
                case "3":
                    try {
                        notificationIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(push_root_url));
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(notificationIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case "4":
                    if (remember) {
                        try {
                            notificationIntent = new Intent(context,
                                    LandingPage.class);
                            notificationIntent.putExtra("push_id", push_id);
                            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(notificationIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            notificationIntent = new Intent(context,
                                    SignIn.class);
                            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(notificationIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                default:
                    if (remember) {
                        try {
                            notificationIntent = new Intent(context,
                                    LandingPage.class);
                            notificationIntent.putExtra("push_id", push_id);
                            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(notificationIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            notificationIntent = new Intent(context,
                                    SignIn.class);
                            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(notificationIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

}
