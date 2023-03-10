package com.example.qrapp;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;

import android.Manifest;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.api.core.ApiFuture;


import org.checkerframework.common.returnsreceiver.qual.This;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class SearchFragment extends Fragment {
    Boolean playerFilterButtonClicked = true;
    Boolean QrFilterButtonClicked = false;
    View view;
    Button playerSearch;
    Button QRSearch;
    SearchView searchView;
    Spinner spinner;
    ListView qrListView;
    QRcAdapter qRcAdapter;
    public ArrayList<Player> playerList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = LayoutInflater.from(getContext()).inflate(R.layout.search, null);
        playerSearch = view.findViewById(R.id.button);
        QRSearch = view.findViewById(R.id.button2);
        searchView = view.findViewById(R.id.searchView);
        spinner = view.findViewById(R.id.spinner);
        spinner.setVisibility(View.GONE);
        spinner.clearAnimation();
        qrListView = view.findViewById(R.id.listView);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // you actually have to click on the magnifying glass..
                // TODO, hook in database  now
                if (playerFilterButtonClicked) {
                    ArrayList<Player> playerList = new ArrayList<>();
                    String searchText = searchView.getQuery().toString();
                    // not sure why this the query returns null atm, i'll figure that out later.
                    db.collection("Users").whereEqualTo("Username", searchText).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0); // this im assuming gets the first result of this list
                            String username = document.getString("Username");
                            String email = document.getString("Email");
                            String phoneNumber = document.getString("PhoneNumber");
                            String location = "edmonton"; // TODO  This is currently NOT in the db
                            Player queriedPlayer = new Player(username, email, location, phoneNumber);
                            try {
                                playerList.add(queriedPlayer);
                                PlayerListAdapter playerListAdapter = new PlayerListAdapter(playerList, getContext());
                                qrListView.setAdapter(playerListAdapter);
                            } catch (Exception e) {
                                Toast queryToast = Toast.makeText(getContext(), "Your search returned no results", Toast.LENGTH_SHORT);
                                queryToast.show();
                            }
                        } else {
                            Toast errorToast = Toast.makeText(getContext(), "An error occurred, please try again", Toast.LENGTH_SHORT);
                            errorToast.show();
                        }
                    });
                } else {
                    Toast toast = Toast.makeText(getContext(), "Please select a filter", Toast.LENGTH_SHORT);
                    toast.show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(spinner.getSelectedItem().toString());
                double maxDistance = Double.parseDouble(spinner.getSelectedItem().toString());
                // TODO, get the location of the user
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                double userLatitude = location.getLatitude();
                double userLongitude = location.getLongitude();
                System.out.println("user latitude" + userLatitude);
                System.out.println(userLongitude);
//                double userLatitude = 55;
//                double userLongitude = 137;
                GeoPoint geoPoint = new GeoPoint(userLatitude, userLongitude);
                double maxLat = geoPoint.getLatitude() + toDegrees(maxDistance / 6371.0);
                double minLat = geoPoint.getLatitude() - toDegrees(maxDistance / 6371.0);
                double maxLon = geoPoint.getLongitude() + toDegrees(maxDistance / 6371.0 / cos(toRadians(geoPoint.getLatitude())));
                double minLon = geoPoint.getLongitude() - toDegrees(maxDistance / 6371.0 / cos(toRadians(geoPoint.getLatitude())));
                db.collection("QrCodes")
                        .whereNotEqualTo("Geolocation", null)
                        .get()
                        .addOnCompleteListener(task -> {
                            ArrayList<Map.Entry<QRCode, Double>> QRCodeListWithDistances = new ArrayList<>();
                            if (task.isSuccessful()) {
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                System.out.println(documents.size());
                                for (DocumentSnapshot document : documents) {
                                    System.out.println("Document: " + document);
                                    GeoPoint qrCodeLocation = document.getGeoPoint("Geolocation");
                                    double lat1 = toRadians(geoPoint.getLatitude());
                                    double lon1 = toRadians(geoPoint.getLongitude());
                                    double lat2 = toRadians(qrCodeLocation.getLatitude());
                                    double lon2 = toRadians(qrCodeLocation.getLongitude());
                                    System.out.println("QR Latitude: " + qrCodeLocation.getLatitude());
                                    System.out.println("QR Longitude: " + qrCodeLocation.getLongitude());
                                    System.out.println("User Latitude: " + geoPoint.getLatitude());
                                    System.out.println("User Longitude: " + geoPoint.getLongitude());
                                    double x = (lon2 - lon1) * Math.cos((lat1 + lat2) / 2);
                                    double y = (lat2 - lat1);
                                    double distance = Math.sqrt(x * x + y * y) * 6371;
                                    System.out.println("Distance: " + distance);
                                    if (distance <= maxDistance) {
                                        Object comments = document.get("Comments");
                                        Integer points = document.getLong("Points").intValue();
                                        String name = document.getString("Name");
                                        String icon = document.getString("icon");
                                        Object playersScanned = document.get("playersScanned");
                                        GeoPoint geolocation = document.getGeoPoint("Geolocation");

                                        QRCode queriedQR = new QRCode(comments, points, name, icon, playersScanned, geolocation);
                                        Map.Entry<QRCode, Double> entry = new AbstractMap.SimpleEntry<>(queriedQR, distance);
                                        QRCodeListWithDistances.add(entry);
                                    }

                                }

                                // Sort the list by distance in ascending order
                                Collections.sort(QRCodeListWithDistances, new Comparator<Map.Entry<QRCode, Double>>() {
                                    public int compare(Map.Entry<QRCode, Double> a, Map.Entry<QRCode, Double> b) {
                                        return a.getValue().compareTo(b.getValue());
                                    }
                                });

                                // Convert the list of map entries back to a list of QRCode objects
                                ArrayList<QRCode> QRCodeList = new ArrayList<>();
                                for (Map.Entry<QRCode, Double> entry : QRCodeListWithDistances) {
                                    QRCodeList.add(entry.getKey());
                                }

                                qRcAdapter = new QRcAdapter(QRCodeList, getContext());
                                qrListView.setAdapter(qRcAdapter);

                                qrListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        System.out.println(QRCodeList.size());
                                        QRCode qrCode = QRCodeList.get(i);
                                        Intent intent = new Intent(getContext(), QRCodeFragment.class);
                                        System.out.println(qrCode.getComments());
                                        System.out.println(qrCode.getPoints());
                                        System.out.println(qrCode.getName());
                                        System.out.println(qrCode.getIcon());
                                        System.out.println(qrCode.getPlayersScanned());
                                        System.out.println(qrCode.getGeolocation());
                                        startActivity(intent);

                                        // startActivity(intent);
                                    }
                                });

                            } else {
                                Toast queryToast = Toast.makeText(getContext(), "Your search returned no results", Toast.LENGTH_SHORT);
                                queryToast.show();
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        playerSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerFilterButtonClicked = true;
                QrFilterButtonClicked = false;
                QRSearch.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                playerSearch.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffafbd")));
                searchView.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.INVISIBLE);
                qrListView.setAdapter(null);
            }
        });
        QRSearch.setOnClickListener(new View.OnClickListener() {
            // I need the context
            Context mContext = getContext();

            @Override
            public void onClick(View view) {
                QrFilterButtonClicked = true;
                playerFilterButtonClicked = false;
                QRSearch.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffafbd")));
                playerSearch.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                searchView.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.VISIBLE);
                qrListView.setAdapter(qRcAdapter);

            }
        });

        return view;
    }

    // Compute the distance between two GeoPoint objects using the Haversine formula
    // Source: https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula
}