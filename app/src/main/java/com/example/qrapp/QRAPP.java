package com.example.qrapp;


import android.app.Application;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

//APPLICATION OBJECT
//Application class to keep a global list of locations.
// Allows global access to location as we will need it in a lot of places in app
// can be used for other things as well if needed
public class QRAPP extends Application {

    private static QRAPP singleton; //only 1 instance of my app at a time
    private ArrayList<LatLng> QRcLocations;//locations list

    public static QRAPP getSingleton() {
        return singleton;
    }

    public ArrayList<LatLng> getQRcLocations() {
        return QRcLocations;
    }

    public void setQRcLocations(ArrayList<LatLng> QRcLocations) {
        this.QRcLocations = QRcLocations;
    }

    public void addQRcLocation(LatLng ll){
        QRcLocations.add(ll);
    }

    public void removeQRcLocation(int index){
        QRcLocations.remove(index);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        QRcLocations = new ArrayList<>();
    }
}
