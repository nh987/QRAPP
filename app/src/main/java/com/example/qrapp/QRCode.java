package com.example.qrapp;

import java.util.ArrayList;
import java.util.HashMap;

public class QRCode {

    public HashMap<String, String> comments;
    public Integer points;
    public String name;
    public String icon;
    public ArrayList<String> playersScanned;
    public ArrayList<PhotoObject> photos;

    public QRCode(HashMap<String, String> comments, Integer points, String name, String icon, ArrayList<String> playersScanned, ArrayList<PhotoObject> photos) {
        this.comments = comments;
        this.points = points;
        this.name = name;
        this.icon = icon;
        this.playersScanned = playersScanned;
        this.photos = photos;
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
    public ArrayList<PhotoObject> getPhotos() {
        return photos;
    }
    public void setPhotos(ArrayList<PhotoObject> photos) {
        this.photos = photos;
    }

}
