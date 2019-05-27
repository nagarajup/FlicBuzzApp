package com.aniapps.flicbuzzapp.notifications;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.aniapps.flicbuzzapp.AppConstants;
import com.aniapps.flicbuzzapp.R;
import com.aniapps.flicbuzzapp.adapters.NotificationAdapter;
import com.aniapps.flicbuzzapp.db.DBCallBacks;
import com.aniapps.flicbuzzapp.db.LocalDB;
import com.aniapps.flicbuzzapp.db.NotificationData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Notification_Act extends AppConstants {

    RecyclerView rv_list;
    FloatingActionButton fb_clear;
    List<NotificationData> myData = new ArrayList<>();

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView header_title = (TextView) findViewById(R.id.tvheader);
        header_title.setText("Notifications");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        rv_list = findViewById(R.id.rv_notifications);

        fb_clear = findViewById(R.id.btn_notifications_clear);


        LocalDB.getInstance(Notification_Act.this).getAllNotifications(new DBCallBacks() {
            @Override
            public void loadMainData(List<NotificationData> notificationData) {
                myData = notificationData;


                NotificationAdapter myAdapter = new NotificationAdapter(Notification_Act.this, myData);
                rv_list.setLayoutManager(new LinearLayoutManager(Notification_Act.this));
                rv_list.setAdapter(myAdapter);

            }
        });


        if (LocalDB.getInstance(Notification_Act.this).getNumFiles() > 0) {
            fb_clear.setVisibility(View.VISIBLE);
            fb_clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LocalDB.getInstance(Notification_Act.this).deleteNotifications();
                    finish();
                }
            });
        } else {
            fb_clear.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        finish();
        overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);

        return super.onOptionsItemSelected(item);
    }
}
