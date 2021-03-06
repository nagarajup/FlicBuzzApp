package com.aniapps.flicbuzzapp.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.*;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.aniapps.flicbuzzapp.R;
import com.aniapps.flicbuzzapp.db.DBCallBacks;
import com.aniapps.flicbuzzapp.db.LocalDB;
import com.aniapps.flicbuzzapp.db.NotificationData;
import com.aniapps.flicbuzzapp.utils.PrefManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FCMMessaging extends FirebaseMessagingService {
    private Context context = this;
    String push_title = "", push_msg = "", push_id = "", push_img_url = "",
            push_root_url = "", push_type = "", push_video_id = "", push_video_language = "";
    String refreshedToken = "";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        refreshedToken = s;
        PrefManager.getIn().setFcm_token(s);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                PrefManager.getIn().sendRegistrationToServer(getApplicationContext(), refreshedToken);
            }
        }, 2000);
        Log.e("#FCM#", "@Refresh"+s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (null != remoteMessage && remoteMessage.getData().size() > 0) {
            try {
                final Bundle data = new Bundle();
                for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                    data.putString(entry.getKey(), entry.getValue());
                }
                if (remoteMessage.getData().size() > 0) {
                    if (null != data.getString("push_id") &&
                            !data.getString("push_id").equals("") &&
                            !data.getString("push_id").equalsIgnoreCase("null")) {
                        generateNotification(data);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    NotificationManager notificationManager = null;

    private void generateNotification(Bundle data) {
        push_id = data.getString("push_id");
        push_title = data.getString("push_title");
        push_msg = data.getString("push_msg");
        push_img_url = data.getString("push_img_url");
        push_root_url = data.getString("push_root_url");
        push_type = data.getString("push_type");
        push_video_id = data.getString("push_video_id");
        push_video_language = data.getString("push_video_language");


        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        NotificationData myData = new NotificationData(push_id, push_title, push_msg,
                push_img_url, push_type, push_root_url, push_video_id, push_video_language, currentDateTimeString, "n");

        LocalDB.getInstance(getApplicationContext()).insertNotifications(myData);

        LocalDB.getInstance(getApplicationContext()).getAllNotifications(new DBCallBacks() {
            @Override
            public void loadMainData(List<NotificationData> notificationData) {
                Log.e("#FCM#",""+notificationData.toString());
                Log.e("#FCM#",""+notificationData.size());
            }
        });

        switch (push_id) {

            default:
                try {
                    NotificationCompat.Builder builder = null;
                    Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.mipmap.ic_notification);
                    Intent onclick = new Intent(getApplicationContext(), Notification_BroadCast.class);
                    onclick.putExtra("push_msg", push_msg);
                    onclick.putExtra("push_id", push_id);
                    onclick.putExtra("push_img_url", push_img_url);
                    onclick.putExtra("push_root_url", push_root_url);
                    onclick.putExtra("push_title", push_title);
                    onclick.putExtra("push_type", push_type);
                    onclick.putExtra("push_video_id", push_video_id);
                    onclick.putExtra("push_video_language", push_video_language);
                    onclick.setAction("0");

                    PendingIntent intent = PendingIntent.getBroadcast(context, 0,
                            onclick, PendingIntent.FLAG_UPDATE_CURRENT);
                    Uri soundUri = RingtoneManager
                            .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel chan;
                        switch (push_id) {
                            case "1":
                                NotificationChannel chan1 = new NotificationChannel(push_id,
                                        "New Video Alert", NotificationManager.IMPORTANCE_DEFAULT);
                                chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                                getManager(context).createNotificationChannel(chan1);
                                builder = new NotificationCompat.Builder(
                                        context, push_id);
                                break;
                            case "2":
                                NotificationChannel chan2 = new NotificationChannel(push_id,
                                        "App Update", NotificationManager.IMPORTANCE_DEFAULT);
                                chan2.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                                getManager(context).createNotificationChannel(chan2);
                                builder = new NotificationCompat.Builder(
                                        context, push_id);
                                break;
                            case "3":
                                NotificationChannel chan3 = new NotificationChannel(push_id,
                                        "Package Expiry", NotificationManager.IMPORTANCE_DEFAULT);
                                chan3.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                                getManager(context).createNotificationChannel(chan3);
                                builder = new NotificationCompat.Builder(
                                        context, push_id);
                                break;

                            case "4":
                                NotificationChannel chan4 = new NotificationChannel(push_id,
                                        "Birth Day", NotificationManager.IMPORTANCE_DEFAULT);
                                chan4.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                                getManager(context).createNotificationChannel(chan4);
                                builder = new NotificationCompat.Builder(
                                        context, push_id);
                                break;
                            case "5":
                                NotificationChannel chan5 = new NotificationChannel(push_id,
                                        "Login Alert", NotificationManager.IMPORTANCE_DEFAULT);
                                chan5.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                                getManager(context).createNotificationChannel(chan5);
                                builder = new NotificationCompat.Builder(
                                        context, push_id);
                                break;

                            case "6":
                                NotificationChannel chan6 = new NotificationChannel(push_id,
                                        "Default", NotificationManager.IMPORTANCE_DEFAULT);
                                chan6.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                                getManager(context).createNotificationChannel(chan6);
                                builder = new NotificationCompat.Builder(
                                        context, push_id);
                                break;

                        }
                    } else {
                        builder = new NotificationCompat.Builder(getApplicationContext(), "");
                    }
                    builder.setSmallIcon(setIcon());
                    builder.setLargeIcon(icon);
                    builder.setWhen(System.currentTimeMillis());
                    builder.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
                    builder.setContentTitle(push_title);
                    builder.setContentText(push_msg);
                    NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                    bigTextStyle.setBigContentTitle(push_title);
                    bigTextStyle.bigText(push_msg);
                    if (push_img_url == null || push_img_url.length() == 0) {
                        builder.setStyle(bigTextStyle);
                    } else {
                        NotificationCompat.BigPictureStyle style =
                                new NotificationCompat.BigPictureStyle().bigPicture(getBitmapFromURL(push_img_url));
                        style.setSummaryText(push_msg);
                        builder.setStyle(style);
                    }

                    builder.setContentIntent(intent);
                    builder.setSound(soundUri);
                    builder.setAutoCancel(true);
                    getManager(getApplicationContext());
                    notificationManager.notify((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE),
                            builder.build());
                } catch (Exception e) {
                    e.printStackTrace();
                }

        }

    }

    private NotificationManager getManager(Context context) {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public static Bitmap getBitmapFromURL(final String src) {
        InputStream input;
        try {
            URL url = new URL(src);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private int setIcon() {
        boolean whiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return whiteIcon ? R.mipmap.ic_notification : R.mipmap.ic_launcher;
    }
}