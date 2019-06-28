package com.aniapps.flicbuzzapp.db;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {NotificationData.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract AppDao appDao();
}
