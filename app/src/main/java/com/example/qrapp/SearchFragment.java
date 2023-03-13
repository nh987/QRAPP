package com.example.qrapp;


import static java.lang.Math.toRadians;

import android.annotation.SuppressLint;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.AbstractMap;


import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Fragment for searching for players and QR codes
 * Allows user to look up players based on their exact username
 * Allows user to look up QR codes based on the users current location
 */
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

    PlayerListAdapter playerListAdapter;

    @SuppressLint("CutPasteId")
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
            /**
             * Method for searching for players, uses the searchview functionality
             * @param query is the inputted text
             * @return a boolean to signify success
             */
            public boolean onQueryTextSubmit(String query) {
                // you actually have to click on the magnifying glass..
                if (playerFilterButtonClicked) {
                    ArrayList<Player> playerList = new ArrayList<>();
                    String searchText = searchView.getQuery().toString();
                    // query all users based on partial string matching
                    // works for partial string matching (i.e search:"User" --> "User1", "User2"
                    db.collection("Users").orderBy("Username").startAt(searchText).endAt(searchText + "\uf8ff").get().addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            // Handle out of bounds error with document snapshot.
                            try {

                                DocumentSnapshot test = task.getResult().getDocuments().get(0);
                            } catch (IndexOutOfBoundsException e) {
                                Toast queryToast = Toast.makeText(getContext(), "User not found!", Toast.LENGTH_LONG);
                                queryToast.show();
                                Log.d("myTag", "User not found in db");
                                return;
                            }
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            // loop through all queried users, create player objects
                            for (DocumentSnapshot document : documents) {
                                Log.d("myTag", document.getString("Username"));
                                String username = document.getString("Username");
                                String email = document.getString("Email");
                                String phoneNumber = document.getString("PhoneNumber");
                                String location = "edmonton"; // TODO  This is currently NOT in the db
                                Player queriedPlayer = new Player(username, email, location, phoneNumber);
                                playerList.add(queriedPlayer);
                            }
                            // So its making it here before the query even finishes executing...
                            String test = String.valueOf(playerList.size());
                            Log.d("myTag", test + " playerListSizeTest");
                            // set adapter and display the listview with queried data

                            playerListAdapter = new PlayerListAdapter(playerList, getContext(), getActivity());
                            qrListView.setAdapter(playerListAdapter);
                            playerListAdapter.notifyDataSetChanged();

                            // listview button listeners are in PlayerListAdapter.java

                        } else {
                            Log.d("myTag", "This shouldn't be logged");
                            Toast errorToast = Toast.makeText(getContext(), "An error occurred, please try again", Toast.LENGTH_SHORT);
                            errorToast.show();
                        }
                    });

                }
                else if (QrFilterButtonClicked) {

                }
                return false;
            }

            // this is here because it is required by interface, but it is not used.
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             This method is called when an item is selected from the spinner. It extracts the maximum distance from the selected item, gets the current location, and retrieves QR codes from Firestore that are within the maximum distance from the current location. The QR codes are then sorted by distance and displayed on the screen.
             @param parent The AdapterView where the selection happened.
             @param view The view within the AdapterView that was clicked.
             @param position The position of the item selected.
             @param id The row id of the item that is selected.
             */
            @SuppressLint("MissingPermission")
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDistance = spinner.getSelectedItem().toString();
                double maxDistance = extractMaxDistance(selectedDistance);
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // requirement to check permission
                    return;
                }
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null) {
                    // If the location cannot be grabbed from GPS we grab it from network
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                ArrayList<QRCode> qrCodeList = new ArrayList<>();
                if (location == null) {
                    Toast.makeText(getContext(), "Location not being shared, location set to default", Toast.LENGTH_SHORT).show();
                    location = new Location("");
                    location.setLatitude(37.4216863);
                    location.setLongitude(-122.0842771);
                } else {
                    double locationLatitude = location.getLatitude();
                    double locationLongitude = location.getLongitude();
                    Toast.makeText(getContext(), "Location found (" + locationLatitude + ", " + locationLongitude + ")", Toast.LENGTH_SHORT).show();
                }
                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                // Search the database for non null geolocations, within the max distance
                db.collection("QRCodes")
                        .whereNotEqualTo("Geolocation", null)
                        .get()
                        .addOnCompleteListener(task -> {
                            ArrayList<Map.Entry<QRCode, Double>> QRCodeListWithDistances = new ArrayList<>();
                            if (task.isSuccessful()) {
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                for (DocumentSnapshot document : documents) {
                                    GeoPoint qrCodeLocation = document.getGeoPoint("Geolocation");
                                    double distance = calculateDistance(geoPoint, qrCodeLocation);
                                    if (distance <= maxDistance) {
                                        Integer points = document.getLong("Points").intValue();
                                        String name = document.getString("Name");
                                        String icon = document.getString("icon");
                                        Object playersScanned = document.get("playersScanned");
                                        GeoPoint geolocation = document.getGeoPoint("Geolocation");
                                        Object comments =  document.get("Comments");
                                        QRCode queriedQR = new QRCode(comments, points, name, icon, playersScanned, geolocation);
                                        // use a map to store the qr code and its distance from the current location
                                        Map.Entry<QRCode, Double> entry = new AbstractMap.SimpleEntry<>(queriedQR, distance);
                                        QRCodeListWithDistances.add(entry);
                                    }
                                }
                                // sort the list of qr codes by distance
                                Collections.sort(QRCodeListWithDistances, new Comparator<Map.Entry<QRCode, Double>>() {
                                    /**
                                     * Compares two entries in the QRCodeListWithDistances list by their distance from the current location.
                                     * @param a
                                     * @param b
                                     * @return
                                     */
                                    public int compare(Map.Entry<QRCode, Double> a, Map.Entry<QRCode, Double> b) {
                                        return a.getValue().compareTo(b.getValue());
                                    }
                                });
                                for (Map.Entry<QRCode, Double> entry : QRCodeListWithDistances) {
                                    qrCodeList.add(entry.getKey());
                                }
                                displayQRCodeList(qrCodeList);
                            } else {
                                Toast queryToast = Toast.makeText(getContext(), "Your search returned no results", Toast.LENGTH_SHORT);
                                queryToast.show();
                            }
                        });
            }
            /**
             Displays a list of QR codes in a ListView and sets a click listener to each item in the list.
             @param QRCodeList the list of QRCode objects to display in the ListView
             */
            private void displayQRCodeList(ArrayList<QRCode> QRCodeList) {
                qRcAdapter = new QRcAdapter(QRCodeList, getContext());
                qrListView.setAdapter(qRcAdapter);

                qrListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        QRCode qrCode = QRCodeList.get(i);
                        Intent intent = new Intent(getActivity(), QRProfile.class);
                        intent.putExtra("qr_code", qrCode); // pass the clicked item to the QRCProfile class
                        startActivity(intent);
                    }
                });
            }

            /**
             This method parses the selected distance from the spinner and returns the maximum distance in kilometers.
             @param selectedDistance the input from the spinner, [0-9]+km
             @return the distance max distance in km to be searched
             */
            private double extractMaxDistance(String selectedDistance) {
                selectedDistance = selectedDistance.replaceAll("[^0-9]", "");
                return Double.parseDouble(selectedDistance);
            }

            /**
             This method calculates the distance between two GeoPoints
             @param point1 the first GeoPoint
             @param point2 the second GeoPoint
             @return the distance between the two GeoPoints
             */
            public double calculateDistance(GeoPoint point1, GeoPoint point2) {
                double lat1 = toRadians(point1.getLatitude());
                double lon1 = toRadians(point1.getLongitude());
                double lat2 = toRadians(point2.getLatitude());
                double lon2 = toRadians(point2.getLongitude());
                double x = (lon2 - lon1) * Math.cos((lat1 + lat2) / 2);
                double y = (lat2 - lat1);
                return Math.sqrt(x * x + y * y) * 6371;
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /**
         * Method for playerSearch button filter, which acts as a way to filter what is queried
         * @param view represents the current view
         * @return nothing
         */
        playerSearch.setOnClickListener(new View.OnClickListener() {
            Context mContext = getContext();
            @Override
            public void onClick(View view) {
                playerFilterButtonClicked = true;
                QrFilterButtonClicked = false;
                QRSearch.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                playerSearch.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffafbd")));
                searchView.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.INVISIBLE);
                qrListView.setAdapter(playerListAdapter);
            }
        });
        /**
         * Method for qrSearch button filter, which acts as a way to filter what is queried
         * @param view represents the current view
         * @return nothing
         */
        QRSearch.setOnClickListener(new View.OnClickListener() {
            // I need the context
            Context mContext = getContext();

            @Override
            /**
             * Method for qrSearch button filter, which acts as a way to filter what is queried
             * @param view represents the current view
             * @return nothing
             */
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
}