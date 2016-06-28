package com.example.elzatom.potholefinder.volley;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.elzatom.potholefinder.MyApplication;

/**
 * Created by elzatom on 3/14/16.
 */
public class VolleySingleton {
    private static VolleySingleton sInstance=null;

    private RequestQueue mRequestQueue;
    private VolleySingleton(){
        mRequestQueue= Volley.newRequestQueue(MyApplication.getAppContext());
    }
    public static VolleySingleton getInstance(){
        if(sInstance==null)
        {
            sInstance=new VolleySingleton();
        }
        return sInstance;
    }
    public RequestQueue getRequestQueue(){
        return mRequestQueue;
    }

}
