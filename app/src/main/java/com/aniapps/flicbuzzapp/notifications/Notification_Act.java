package com.aniapps.flicbuzzapp.notifications;

import android.annotation.SuppressLint;
import android.os.Bundle;


import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aniapps.flicbuzzapp.AppConstants;
import com.aniapps.flicbuzzapp.R;
import com.aniapps.flicbuzzapp.adapters.NotificationAdapter;
import com.aniapps.flicbuzzapp.db.DBCallBacks;
import com.aniapps.flicbuzzapp.db.LocalDB;
import com.aniapps.flicbuzzapp.db.NotificationData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Notification_Act extends AppConstants {

    RecyclerView rv_list;
    FloatingActionButton fb_clear;
    List<NotificationData> myData = new ArrayList<>();
    ConstraintLayout lay_notify;
    LinearLayout lay_notice;

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
        lay_notify=findViewById(R.id.lay_notify);
        lay_notice=findViewById(R.id.lay_no_notification);
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
            lay_notify.setVisibility(View.VISIBLE);
            lay_notice.setVisibility(View.GONE);
            fb_clear.setVisibility(View.VISIBLE);
            fb_clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LocalDB.getInstance(Notification_Act.this).deleteNotifications();
                    finish();
                }
            });
        } else {
            lay_notice.setVisibility(View.VISIBLE);
            lay_notify.setVisibility(View.GONE);
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
