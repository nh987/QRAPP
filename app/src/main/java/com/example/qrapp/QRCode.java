package com.example.qrapp;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class QRCode {

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
    public Integer getPoints() {
        return points;
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


}
