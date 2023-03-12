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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


public class SearchFragment extends Fragment {
    Boolean playerFilterButtonClicked = true;
    Boolean QrFilterButtonClicked = false;
    View view;
    Button playerSearch;
    Button QRSearch;
    SearchView searchView;
    Spinner spinner;
    ListView qrListView;

    ListView playerListView;
    QRcAdapter qRcAdapter;

    PlayerListAdapter playerListAdapter;

    /*
    ListView qrListView;
    QRcAdapter qRcAdapter;



     */

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
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDistance = spinner.getSelectedItem().toString();
                double maxDistance = extractMaxDistance(selectedDistance);
                Location location = getCurrentLocation();
                ArrayList<QRCode> qrCodeList = new ArrayList<>();
                if (location == null) {
                    Toast.makeText(getContext(), "Location not being shared, location set to default", Toast.LENGTH_SHORT).show();
                    location = new Location("");
                    location.setLatitude(53.5444);
                    location.setLongitude(-113.4909);
                }
                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                System.out.println("Location" + geoPoint);
                db.collection("QrCodes")
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
                                        System.out.println("Found QR CODE!!");
                                        Integer points = document.getLong("Points").intValue();
                                        String name = document.getString("Name");
                                        String icon = document.getString("icon");
                                        Object playersScanned = document.get("playersScanned");
                                        GeoPoint geolocation = document.getGeoPoint("Geolocation");
                                        Object comments =  document.get("Comments");
                                        QRCode queriedQR = new QRCode(comments, points, name, icon, playersScanned, geolocation);
                                        Map.Entry<QRCode, Double> entry = new AbstractMap.SimpleEntry<>(queriedQR, distance);
                                        QRCodeListWithDistances.add(entry);
                                        System.out.println("QRCodeListWithDistances size" + QRCodeListWithDistances.size());
                                    }
                                }
                                Collections.sort(QRCodeListWithDistances, new Comparator<Map.Entry<QRCode, Double>>() {
                                    public int compare(Map.Entry<QRCode, Double> a, Map.Entry<QRCode, Double> b) {
                                        return a.getValue().compareTo(b.getValue());
                                    }
                                });
                                for (Map.Entry<QRCode, Double> entry : QRCodeListWithDistances) {
                                    qrCodeList.add(entry.getKey());
                                }
                                System.out.println("QRCodeList size" + qrCodeList.size());
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


            private double extractMaxDistance(String selectedDistance) {
                selectedDistance = selectedDistance.replaceAll("[^0-9]", "");
                return Double.parseDouble(selectedDistance);
            }

            private Location getCurrentLocation() {
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                final long MIN_TIME_BETWEEN_UPDATES = 1000 * 60 * 1;
                final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
                final int PERMISSION_REQUEST_CODE = 1001;

                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        // Called when the location has changed
                        // You can use the new location here
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        // Called when the provider status changes
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        // Called when the provider is enabled by the user
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        // Called when the provider is disabled by the user
                    }
                };

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Request permission from the user
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
                    return null;
                } else {
                    // Permission already granted, so register the location listener and get the current location
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }

            private double calculateDistance(GeoPoint point1, GeoPoint point2) {
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