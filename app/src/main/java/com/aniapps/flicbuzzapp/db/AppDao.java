package com.aniapps.flicbuzzapp.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import io.reactivex.Maybe;

import java.util.List;

@Dao
public interface AppDao {
    @Insert
    void insertNotificationData(NotificationData notificationData);

    @Query("DELETE FROM tb_notification")
    void deleteNotifications();

    @Query("DELETE FROM tb_notification WHERE push_id=:id")
    void deleteNotification(String id);

    @Query("SELECT * FROM tb_notification")
    Maybe<List<NotificationData>> getAllNotificationData();

}
