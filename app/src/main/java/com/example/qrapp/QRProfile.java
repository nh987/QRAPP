package com.example.qrapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qrapp.QRCode;
import com.google.firebase.firestore.GeoPoint;

public class QRProfile extends AppCompatActivity {

    private TextView QRCName;
    private TextView points;
//    Object comments = document.get("Comments");
//    Integer points = document.getLong("Points").intValue();
//    String name = document.getString("Name");
//    String icon = document.getString("icon");
//    Object playersScanned = document.get("playersScanned");
//    GeoPoint geolocation = document.getGeoPoint("Geolocation");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_qrc);

        Intent intent = getIntent();
        QRCode qrCode = intent.getParcelableExtra("qr_code"); // get the passed item

        QRCName = findViewById(R.id.QRCName);
        points = findViewById(R.id.textView3);

        QRCName.setText(qrCode.getName()); // set the name text
        points.setText(qrCode.getPoints()); // set the description text
    }
}
