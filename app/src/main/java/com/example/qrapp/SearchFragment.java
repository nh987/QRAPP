package com.example.qrapp;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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



import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class SearchFragment extends Fragment {

    Boolean playerFilterButtonClicked = true;
    Boolean QrFilterButtonClicked = false;
    private QRcAdapter qRcAdapter;

    public ArrayList<QRCode> dataList;
    public ArrayList<Player> playerList;
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.search, null);
        Button playerSearch = (Button) view.findViewById(R.id.button);
        Button QRSearch = (Button) view.findViewById(R.id.button2);
        SearchView searchView = (SearchView) view.findViewById(R.id.searchView);
        ListView qrListView = view.findViewById(R.id.listView);

        // qr code list contains the qr codes, but you cannot put those on screen
        // you need to use an adapter to do that
        // the adapter is a class that you create that extends BaseAdapter
        // qrListView is actually what shows up on screen

        ArrayList<QRCode> QRCodeList = new ArrayList<>();
        qRcAdapter = new QRcAdapter(QRCodeList, this.getContext());

        //adding DB instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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

                            PlayerListAdapter playerListAdapter = new PlayerListAdapter(playerList, getContext());
                            qrListView.setAdapter(playerListAdapter); // why the fuck isn't this displaying (I don't think its an adapter problem but alas...)


                            // Listener for Listview
                            qrListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    // set buttons in listview, s.t it opens profile_user.xml
                                }
                            });

                        } else {
                            Log.d("myTag", "This shouldn't be logged");
                            Toast errorToast = Toast.makeText(getContext(), "An error occurred, please try again", Toast.LENGTH_SHORT);
                            errorToast.show();
                        }
                    });

                }

                else if (QrFilterButtonClicked) {
                    String searchLocationStr = searchView.getQuery().toString().trim();
                    String[] locationParts = searchLocationStr.split("[\\s,]+");
                    GeoPoint searchLocation = new GeoPoint(Double.parseDouble(locationParts[0].trim()), Double.parseDouble(locationParts[1].trim()));
                    db.collection("QrCodes").whereNotEqualTo("Geolocation", null).get().addOnCompleteListener(task -> {
                        ArrayList<Map.Entry<QRCode, Double>> QRCodeListWithDistances = new ArrayList<>();
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            System.out.println("_------------------------------------");
                            for (DocumentSnapshot document : documents) {
                                // Get the GeoPoint object from the document
                                GeoPoint location = document.getGeoPoint("Geolocation");
                                double distance = distanceBetweenPoints(searchLocation, location);
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

                            QRcAdapter qRcAdapter = new QRcAdapter(QRCodeList, getContext());
                            qrListView.setAdapter(qRcAdapter);
                        } else {
                            Toast queryToast = Toast.makeText(getContext(), "Your search returned no results", Toast.LENGTH_SHORT);
                            queryToast.show();
                        }
                    });


                }


                else {
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
        /**
         * Method for playerSearch button filter, which acts as a way to filter what is queried
         * @param view represents the current view
         * @return nothing
         */
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
        /**
         * Method for qrSearch button filter, which acts as a way to filter what is queried
         * @param view represents the current view
         * @return nothing
         */
        QRSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QrFilterButtonClicked = true;
                playerFilterButtonClicked = false;
                QRSearch.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                playerSearch.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
            }
        });
        return view;
    }

    // Compute the distance between two GeoPoint objects using the Haversine formula
    private double distanceBetweenPoints(GeoPoint point1, GeoPoint point2) {
        double earthRadius = 6371; // Earth's radius in kilometers
        double dLat = Math.toRadians(point2.getLatitude() - point1.getLatitude());
        double dLng = Math.toRadians(point2.getLongitude() - point1.getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(point1.getLatitude())) * Math.cos(Math.toRadians(point2.getLatitude())) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;
        return distance;
    }

}