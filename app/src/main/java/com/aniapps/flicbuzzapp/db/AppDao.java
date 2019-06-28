package com.aniapps.flicbuzzapp.db;



import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Maybe;

import java.util.List;

@Dao
public interface AppDao {
    @Insert
    void insertNotificationData(NotificationData notificationData);

    @Query("DELETE FROM tb_notification")
    void deleteNotifications();


    @Query("DELETE FROM tb_notification WHERE push_time=:push_time")
    void deleteNotification(String push_time);

    @Query("SELECT * FROM tb_notification ORDER BY id DESC")
    Maybe<List<NotificationData>> getAllNotificationData();

    @Query("SELECT COUNT(id) FROM tb_notification")
    int getRowCount();
}
