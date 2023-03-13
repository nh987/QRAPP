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

    /**
     * Returns the comments associated with the QR code.
     * @return
     */
    public Object getComments() {
        return comments;
    }
    /**
     * Sets the comments associated with the QR code.
     * @param comments
     */
    public void setComments(Object comments) {
        this.comments = comments;
    }

    /**
     * Returns the number of points the QR code is worth.
     * @return The number of points the QR code is worth.
     */
    public String getPoints() {
        if (points != null) {
            return points.toString();
        } else {
            return "0"; // or any other default value you want to use
        }
    }

    /**
     * Sets the number of points the QR code is worth.
     * @param points The number of points the QR code is worth.
     */
    public void setPoints(Integer points) {
        this.points = points;
    }

    /**
     * Returns the name of the QR code.
     * @return The name of the QR code.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the QR code.
     * @param name The name of the QR code.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the icon associated with the QR code.
     * @return The icon associated with the QR code.
     */
    public String getIcon() {
        return icon;
    }
    /**
     * Sets the icon associated with the QR code.
     * @param icon The icon associated with the QR code.
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }
    /**
     * Returns the list of players who have scanned the QR code.
     * @return The list of players who have scanned the QR code.
     */
    public Object getPlayersScanned() {
        return playersScanned;
    }
    /**
     * Sets the list of players who have scanned the QR code.
     * @param playersScanned The list of players who have scanned the QR code.
     */
    public void setPlayersScanned(Object playersScanned) {
        this.playersScanned = playersScanned;
    }
    /**
     * Returns the geographical location of the QR code.
     * @return The geographical location of the QR code.
     */
    public GeoPoint getGeolocation() {
        return geolocation;
    }
    /**
     * Sets the geographical location of the QR code.
     * @param geolocation The geographical location of the QR code.
     */
    public void setGeolocation(GeoPoint geolocation) {
        this.geolocation = geolocation;
    }

    /**
     * Represents a QR code with a name, number of points, and icon.
     */
    protected QRCode(Parcel in) {
        name = in.readString();
        points = Integer.parseInt(in.readString());
        icon = in.readString();
        // pass the comments list
        // comments = in.readArrayList(String.class.getClassLoader());
    }

    /**
     * A Creator object that can be used to create instances of the QRCode class from a Parcel.
     */
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

    /**
     * Override necessary for Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param parcel The Parcel in which the object should be written.
     * @param i      Additional flags about how the object should be written.
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(String.valueOf(points));
        parcel.writeString(icon);
        // parcel.writeList((ArrayList<String>) comments);
    }

}

