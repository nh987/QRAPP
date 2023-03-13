package com.example.qrapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.location.Location;

import com.google.firebase.firestore.GeoPoint;

import org.junit.Test;

public class QRCodeTest {
    @Test
    public QRCode MockQRCode() {
        Location location = new Location("location");
        location.setLatitude(50.0);
        location.setLongitude(50.0);
        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        QRCode qrCode = new QRCode(null, 10, "QR Code", "icon", null, geoPoint);
        return qrCode;
    }

    @Test
    public void getCommentsTest() {
        QRCode qrCode = MockQRCode();
        assertEquals(qrCode.getComments(), null);
    }
    @Test
    public void setCommentsTest() {
        QRCode qrCode = MockQRCode();
        qrCode.setComments("comments");
        assertEquals(qrCode.getComments(), "comments");
    }
    @Test
    public void getPointsTest() {
        QRCode qrCode = MockQRCode();
        assertEquals(qrCode.getPoints(), 10);
    }
    @Test
    public void setPointsTest() {
        QRCode qrCode = MockQRCode();
        qrCode.setPoints(20);
        assertEquals(qrCode.getPoints(), 20);
    }
    @Test
    public void getNameTest() {
        QRCode qrCode = MockQRCode();
        assertEquals(qrCode.getName(), "QR Code");
    }
    @Test
    public void setNameTest() {
        QRCode qrCode = MockQRCode();
        qrCode.setName("New QR Code");
        assertEquals(qrCode.getName(), "New QR Code");
    }
    @Test
    public void getIconTest() {
        QRCode qrCode = MockQRCode();
        assertEquals(qrCode.getIcon(), "icon");
    }
    @Test
    public void setIconTest() {
        QRCode qrCode = MockQRCode();
        qrCode.setIcon("new icon");
        assertEquals(qrCode.getIcon(), "new icon");
    }
    @Test
    public void getPlayersScannedTest() {
        QRCode qrCode = MockQRCode();
        assertEquals(qrCode.getPlayersScanned(), null);
    }
    @Test
    public void setPlayersScannedTest() {
        QRCode qrCode = MockQRCode();
        qrCode.setPlayersScanned("players scanned");
        assertEquals(qrCode.getPlayersScanned(), "players scanned");
    }
    @Test
    public void getGeolocationTest() {
        QRCode qrCode = MockQRCode();
        Location location = new Location("location");
        location.setLatitude(50.0);
        location.setLongitude(50.0);
        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        assertEquals(qrCode.getGeolocation(), geoPoint);
    }
    @Test
    public void setGeolocationTest() {
        QRCode qrCode = MockQRCode();
        Location location = new Location("location");
        location.setLatitude(55.0);
        location.setLongitude(55.0);
        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        qrCode.setGeolocation(geoPoint);
        assertEquals(qrCode.getGeolocation(), geoPoint);
    }

}