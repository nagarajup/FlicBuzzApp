package com.aniapps.flicbuzzapp.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tb_notification")
public class NotificationData {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "push_title")
    private String push_title = "";

    @ColumnInfo(name = "push_msg")
    private String push_msg = "";

    @ColumnInfo(name = "push_id")
    private String push_id = "";

    @ColumnInfo(name = "push_img_url")
    private String push_img_url = "";

    @ColumnInfo(name = "push_type")
    private String push_type = "";


    @ColumnInfo(name = "push_root_url")
    private String push_root_url = "";

    @ColumnInfo(name = "push_video_id")
    private String push_video_id = "";

    @ColumnInfo(name = "push_video_language")
    private String push_video_language = "";

    public NotificationData(int id, String push_title, String push_msg, String push_id,
                            String push_img_url, String push_type, String push_root_url,
                            String push_video_id, String push_video_language) {
        this.id = id;
        this.push_title = push_title;
        this.push_msg = push_msg;
        this.push_id = push_id;
        this.push_img_url = push_img_url;
        this.push_type = push_type;
        this.push_root_url = push_root_url;
        this.push_video_id = push_video_id;
        this.push_video_language = push_video_language;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPush_title() {
        return push_title;
    }

    public void setPush_title(String push_title) {
        this.push_title = push_title;
    }

    public String getPush_msg() {
        return push_msg;
    }

    public void setPush_msg(String push_msg) {
        this.push_msg = push_msg;
    }

    public String getPush_id() {
        return push_id;
    }

    public void setPush_id(String push_id) {
        this.push_id = push_id;
    }

    public String getPush_img_url() {
        return push_img_url;
    }

    public void setPush_img_url(String push_img_url) {
        this.push_img_url = push_img_url;
    }

    public String getPush_type() {
        return push_type;
    }

    public void setPush_type(String push_type) {
        this.push_type = push_type;
    }

    public String getPush_root_url() {
        return push_root_url;
    }

    public void setPush_root_url(String push_root_url) {
        this.push_root_url = push_root_url;
    }

    public String getPush_video_id() {
        return push_video_id;
    }

    public void setPush_video_id(String push_video_id) {
        this.push_video_id = push_video_id;
    }

    public String getPush_video_language() {
        return push_video_language;
    }

    public void setPush_video_language(String push_video_language) {
        this.push_video_language = push_video_language;
    }
}
