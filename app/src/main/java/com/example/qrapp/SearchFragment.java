package com.example.qrapp;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class SearchFragment extends Fragment {
    Boolean playerFilterButtonClicked = false;
    Boolean QrFilterButtonClicked = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HashMap<String, String> comments1 = new HashMap<>();
        comments1.put("Comment 1", "This is comment 1");
        comments1.put("Comment 2", "This is comment 2");
        comments1.put("Comment 3", "This is comment 3");
        ArrayList<String> playersScanned1 = new ArrayList<>();
        playersScanned1.add("Player 1");
        playersScanned1.add("Player 2");
        playersScanned1.add("Player 3");

        ArrayList<String> photos1 = new ArrayList<>();
        photos1.add("photo 2");
        photos1.add("photo 1");


        ArrayList<Float> geolocation1 = new ArrayList<>();
        geolocation1.add(50.2f);
        geolocation1.add(45.3f);
        QRCode qrCode1 = new QRCode(comments1, 10, "QR Code 1", "https://www.example.com/icon1.jpg", playersScanned1, photos1, geolocation1);

        HashMap<String, String> comments2 = new HashMap<>();
        comments2.put("Comment 1", "This is comment 1 for QR Code 2");
        comments2.put("Comment 2", "This is comment 2 for QR Code 2");

        ArrayList<String> playersScanned2 = new ArrayList<>();
        playersScanned2.add("Player 1");
        playersScanned2.add("Player 2");

        ArrayList<String> photos2 = new ArrayList<>();
        photos2.add("photo");
        photos2.add("photo 2");

        ArrayList<Float> geolocation2 = new ArrayList<>();
        geolocation2.add(40.8f);
        geolocation2.add(70.2f);

        QRCode qrCode2 = new QRCode(comments2, 5, "QR Code 2", "https://www.example.com/icon2.jpg", playersScanned2, photos2, geolocation2);


        ArrayList<QRCode> QRCodeList = new ArrayList<>();
        QRCodeList.add(qrCode1);
        QRCodeList.add(qrCode2);

//        return super.onCreateView(inflater, container, savedInstanceState);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.search, null);
        Button playerSearch = (Button) view.findViewById(R.id.button);
        Button QRSearch = (Button) view.findViewById(R.id.button2);
        SearchView searchView = (SearchView) view.findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // you actually have to click on the magnifying glass..
                // TODO, hook in database now
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        playerSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerFilterButtonClicked = true;
                QrFilterButtonClicked = false;
                QRSearch.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                playerSearch.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                // change Qr color to standard
                // change player color to coloured
            }
        });

        QRSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QrFilterButtonClicked = true;
                playerFilterButtonClicked = false;
                QRSearch.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                playerSearch.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));

                // Get the geolocation entered in the search bar
                String[] searchLocationStr = searchView.getQuery().toString().split(",");
                ArrayList<Float> searchLocation = new ArrayList<>();
                searchLocation.add(Float.parseFloat(searchLocationStr[0].trim()));
                searchLocation.add(Float.parseFloat(searchLocationStr[1].trim()));

                // Find the closest QR code based on the search location
                QRCode closestQRCode = null;
                float closestDistance = Float.MAX_VALUE;
                for (QRCode qrCode : QRCodeList) {
                    float distance = calculateDistance(qrCode.getGeolocation(), searchLocation);
                    if (distance < closestDistance) {
                        closestQRCode = qrCode;
                        closestDistance = distance;
                    }
                }

                // Sort the QR codes based on distance from the search location
                ArrayList<QRCode> sortedQRCodeList = new ArrayList<>(QRCodeList);
                Collections.sort(sortedQRCodeList, new Comparator<QRCode>() {
                    @Override
                    public int compare(QRCode qrCode1, QRCode qrCode2) {
                        float distance1 = calculateDistance(qrCode1.getGeolocation(), searchLocation);
                        float distance2 = calculateDistance(qrCode2.getGeolocation(), searchLocation);
                        return Float.compare(distance1, distance2);
                    }
                });

                // Display the QR codes in order of increasing distance
                if (closestQRCode != null) {
                    sortedQRCodeList.remove(closestQRCode);
                    sortedQRCodeList.add(0, closestQRCode);
                }
                for (QRCode qrCode : sortedQRCodeList) {
                    Log.d("QR Code", qrCode.getName() + ": " + qrCode.getGeolocation().toString());
                }
            }
        });

        return view;
    }

    public float calculateDistance(ArrayList<Float> location1, ArrayList<Float> location2) {
        float x1 = location1.get(0);
        float y1 = location1.get(1);
        float x2 = location2.get(0);
        float y2 = location2.get(1);
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

}
