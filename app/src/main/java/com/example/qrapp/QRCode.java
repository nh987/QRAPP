package com.example.qrapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * QRCode class that holds the information for a QR code.
 */
public class QRCode implements Parcelable {

    public Object comments;
    public Integer points;
    public String name;
    public String icon;
    public Object playersScanned;
    public GeoPoint geolocation;

    /**

     Constructs a new QRCode object with the specified comments, points, name, icon, playersScanned, and geolocation.
     @param comments The comments associated with the QR code.
     @param points The number of points the QR code is worth.
     @param name The name of the QR code.
     @param icon The icon associated with the QR code.
     @param playersScanned The list of players who have scanned the QR code.
     @param geolocation The geographical location of the QR code.
     */
    public QRCode(Object comments, Integer points, String name, String icon, Object playersScanned, GeoPoint geolocation) {
        this.comments = comments;
        this.points = points;
        this.name = name;
        this.icon = icon;
        this.playersScanned = playersScanned;
        this.geolocation = geolocation;
    }

    public Object getComments() {
        return comments;
    }
    public void setComments(Object comments) {
        this.comments = comments;
    }
    public String getPoints() {
        if (points != null) {
            return points.toString();
        } else {
            return "0"; // or any other default value you want to use
        }
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public Object getPlayersScanned() {
        return playersScanned;
    }
    public void setPlayersScanned(Object playersScanned) {
        this.playersScanned = playersScanned;
    }
    public GeoPoint getGeolocation() {
        return geolocation;
    }
    public void setGeolocation(GeoPoint geolocation) {
        this.geolocation = geolocation;
    }


    protected QRCode(Parcel in) {
        name = in.readString();
        points = Integer.parseInt(in.readString());
        icon = in.readString();
        // pass the comments list
//        comments = in.readArrayList(String.class.getClassLoader());
    }

    public static final Creator<QRCode> CREATOR = new Creator<QRCode>() {
        @Override
        public QRCode createFromParcel(Parcel in) {
            return new QRCode(in);
        }

        @Override
        public QRCode[] newArray(int size) {
            return new QRCode[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(String.valueOf(points));
        parcel.writeString(icon);
//        parcel.writeList((ArrayList<String>) comments);
    }
}

