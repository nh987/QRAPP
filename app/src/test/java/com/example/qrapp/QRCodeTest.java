package com.example.qrapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import android.location.Location;

import com.google.firebase.firestore.GeoPoint;

import org.junit.Test;

/**
 * Test the QRCode class
 */
public class QRCodeTest {
    QRCode qrCode;
    public QRCode MockQRCode() {
        QRCode qrCode = new QRCode(null, null, null, null, null, null);
        return qrCode;
    }
    /**
     * Test setting comments
     */
    @Test
    public void setCommentsTest() {
        QRCode qrCode = MockQRCode();
        qrCode.setComments("comments");
        assertEquals(qrCode.getComments(), "comments");
    }
    /**
     * Test getting comments
     */
    @Test
    public void getCommentsTest() {
        QRCode qrCode = MockQRCode();
        assertEquals(qrCode.getComments(), null);
    }
    /**
     * Test getting point value
     */
    @Test
    public void getPointsTest() {
        QRCode qrCode = MockQRCode();
        Integer expectedPoints = 10;
        qrCode.setPoints(expectedPoints);
        String points = qrCode.getPoints();
        assertEquals(expectedPoints.toString(), points);
    }
    /**
     * Test setting point value
     */
    @Test
    public void setPointsTest() {
        QRCode qrCode = MockQRCode();
        Integer expectedPoints = 5;
        qrCode.setPoints(expectedPoints);
        assertEquals(expectedPoints, qrCode.points);
    }
    /**
     * Test setting name
     */
    @Test
    public void setNameTest() {
        QRCode qrCode = MockQRCode();
        qrCode.setName("New QR Code");
        assertEquals(qrCode.getName(), "New QR Code");
    }
    /**
     * Test getting name
     */
    @Test
    public void getNameTest() {
        QRCode qrCode = MockQRCode();
        qrCode.setName("QR Code");
        assertEquals(qrCode.getName(), "QR Code");
    }
    /**
     * Test getting icon
     */
    @Test
    public void getIconTest() {
        QRCode qrCode = MockQRCode();
        qrCode.setIcon(":)");
        assertEquals(qrCode.getIcon(), ":)");
    }
    /**
     * Test setting icon
     */
    @Test
    public void setIconTest() {
        QRCode qrCode = MockQRCode();
        qrCode.setIcon("new icon");
        assertEquals(qrCode.getIcon(), "new icon");
    }
    /**
     * Test getting players scanned
     */
    @Test
    public void getPlayersScannedTest() {
        QRCode qrCode = MockQRCode();
        assertEquals(qrCode.getPlayersScanned(), null);
    }
    /**
     * Test setting players scanned
     */
    @Test
    public void setPlayersScannedTest() {
        QRCode qrCode = MockQRCode();
        qrCode.setPlayersScanned("players scanned");
        assertEquals(qrCode.getPlayersScanned(), "players scanned");
    }
    /**
     * Test getting geolocation
     */
    @Test
    public void getGeolocationTest() {
        QRCode qrCode = MockQRCode();
        assertEquals(qrCode.getGeolocation(), null);
    }
    /**
     * Test setting geolocation
     */
    @Test
    public void setGeolocationTest() {
        QRCode qrCode = MockQRCode();
        Location location = mock(Location.class);
        when(location.getLatitude()).thenReturn(50.0);
        when(location.getLongitude()).thenReturn(55.0);
        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        qrCode.setGeolocation(geoPoint);
        assertEquals(qrCode.getGeolocation(), geoPoint);
    }

}
