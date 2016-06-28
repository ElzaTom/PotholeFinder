package com.example.elzatom.potholefinder;

import android.app.Application;
import android.content.Context;

/**
 * Created by elzatom on 3/14/16.
 */
public class MyApplication extends Application {



    private static MyApplication sInstance;



    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

    }

}

