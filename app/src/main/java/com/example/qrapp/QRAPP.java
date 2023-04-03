package com.example.qrapp;


import android.app.Application;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

//APPLICATION OBJECT

/**
 * This class is a singleton object for the application.
 * It can be used in cases when we need to refer to the application itself
 * We have it as our application name in the Manifest
 */
public class QRAPP extends Application {

    private static QRAPP singleton; //only 1 instance of my app at a time
    //private ArrayList<LatLng> QRcLocations;//locations list,
    //UPDATE: Will use Firebase to track locations now.

    public static QRAPP getSingleton() {
        return singleton;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //QRcLocations = new ArrayList<>();
    }
}
