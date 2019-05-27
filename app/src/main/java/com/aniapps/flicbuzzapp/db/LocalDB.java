package com.aniapps.flicbuzzapp.db;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.util.Log;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.util.List;

public class LocalDB {
    private static final String DB_NAME = "FlicBuzz_DB";
    private AppDataBase db;
    Context context;
    private static LocalDB ourInstance;
    List<NotificationData> myLocations;

    public static LocalDB getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new LocalDB(context);
        }
        return ourInstance;
    }

    public LocalDB(Context context) {
        this.context = context;
        db=Room.databaseBuilder(context,AppDataBase.class,DB_NAME).allowMainThreadQueries().build();
    }

    public AppDataBase getDb() {
        if (db != null)
            return db;
        else
            return null;
    }

    @SuppressLint("CheckResult")
    public void insertNotifications(final NotificationData mainRepo) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                db.appDao().insertNotificationData(mainRepo);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                Log.e("#DB Success#", "Main Repo inserted done");
            }

            @Override
            public void onError(Throwable e) {
                Log.e("#DB Error#", "Main Repo not inserted" + e.getMessage());

            }
        });
    }

    // get  Data
    @SuppressLint("CheckResult")
    public void getAllNotifications(final DBCallBacks dbCallBack) {
        db.appDao().getAllNotificationData().subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<NotificationData>>() {
            @Override
            public void accept(List<NotificationData> yardsPojos) throws Exception {
                dbCallBack.loadMainData(yardsPojos);
            }
        });
    }

    // delete data
    public void deleteNotifications() {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                db.appDao().deleteNotifications();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                Log.e("#DB Success#", "Deleted Pending Repos ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e("#DB Error#", "Failed Delete Pending Repos" + " " + e.getMessage());
                ;
            }
        });
    }

    // delete data
    public void deleteNotification(final String time) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                db.appDao().deleteNotification(time);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                Log.e("#DB Success#", "Deleted Pending Repos ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e("#DB Error#", "Failed Delete Pending Repos" + " " + e.getMessage());
                ;
            }
        });
    }

    public int getNumFiles() {
        return db.appDao().getRowCount();
    }
}
