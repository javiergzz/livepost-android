package com.livepost.javiergzzr.application;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by javiergzzr on 15/06/15.
 */
public class LoadData extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }

}
