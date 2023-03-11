package com.example.qrapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class QRCode implements Parcelable {

    public Object comments;
    public Integer points;
    public String name;
    public String icon;
    public Object playersScanned;
    public GeoPoint geolocation;

    public QRCode(Object comments, Integer points, String name, String icon, Object playersScanned, GeoPoint geolocation) {
        this.comments = comments;
        this.points = points;
        this.name = name;
        this.icon = icon;
        this.playersScanned = playersScanned;
        this.geolocation = geolocation;
    }

    // getters and setters
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
