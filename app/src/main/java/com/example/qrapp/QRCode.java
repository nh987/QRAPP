package com.example.qrapp;

import java.util.ArrayList;
import java.util.HashMap;

public class QRCode {

    public HashMap<String, String> comments;
    public Integer points;
    public String name;
    public String icon;
    public ArrayList<String> playersScanned;
    public ArrayList<String> photos;
    public ArrayList<Float> geolocation;

    public QRCode(HashMap<String, String> comments, Integer points, String name, String icon, ArrayList<String> playersScanned, ArrayList<String> photos, ArrayList<Float> geolocation) {
        this.comments = comments;
        this.points = points;
        this.name = name;
        this.icon = icon;
        this.playersScanned = playersScanned;
        this.photos = photos;
        this.geolocation = geolocation;
    }

    // getters and setters
    public HashMap<String, String> getComments() {
        return comments;
    }
    public void setComments(HashMap<String, String> comments) {
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
    public ArrayList<String> getPlayersScanned() {
        return playersScanned;
    }
    public void setPlayersScanned(ArrayList<String> playersScanned) {
        this.playersScanned = playersScanned;
    }
    public ArrayList<Float> getGeolocation() {
        return geolocation;
    }
    public void setGeolocation(ArrayList<Float> geolocation) {
        this.geolocation = geolocation;
    }
    public ArrayList<String> getPhotos() {
        return photos;
    }
    public void setPhotos(ArrayList<String> photos) {
        this.photos = photos;
    }

}
