package com.example.qrapp;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.Arrays;
import java.util.HashMap;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


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

        HashMap<String, String> comments1 = new HashMap<>();
        comments1.put("Comment 1", "This is comment 1");
        ArrayList<String> playersScanned1 = new ArrayList<>(Arrays.asList("Player 1", "Player 2"));
        ArrayList<String> photos1 = new ArrayList<>(Arrays.asList("photo"));
        ArrayList<Float> geolocation1 = new ArrayList<>(Arrays.asList(50.2f, 45.2f));
        QRCode qrCode1 = new QRCode(comments1, 10, "QR Code 1", "https://www.example.com/icon1.jpg", playersScanned1, photos1, geolocation1);

        HashMap<String, String> comments2 = new HashMap<>();
        comments2.put("Comment 1", "This is comment 2");
        comments2.put("Comment 2", "Another comment");
        ArrayList<String> playersScanned2 = new ArrayList<>(Arrays.asList("Player 3", "Player 4", "Player 5"));
        ArrayList<String> photos2 = new ArrayList<>(Arrays.asList("photo1", "photo2"));
        ArrayList<Float> geolocation2 = new ArrayList<>(Arrays.asList(51.3f, 48.7f));
        QRCode qrCode2 = new QRCode(comments2, 20, "QR Code 2", "https://www.example.com/icon2.jpg", playersScanned2, photos2, geolocation2);

        QRCodeList.add(qrCode1);
        QRCodeList.add(qrCode2);

        //adding DB instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // you actually have to click on the magnifying glass..
                // TODO, hook in database now
                String searchText = searchView.getQuery().toString();
                if (playerFilterButtonClicked) {
                    // query the database, store results in a database


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
                            }
                            catch (Exception e)
                            {
                                Toast queryToast = Toast.makeText(getContext(), "Your search returned no results", Toast.LENGTH_SHORT);
                                queryToast.show();
                            }
                        }
                        else {
                            Toast errorToast = Toast.makeText(getContext(), "An error occurred, please try again", Toast.LENGTH_SHORT);
                            errorToast.show();
                        }
                });
                }


                // Just don't touch this William you giraffe
                //*******************************************
                else if (QrFilterButtonClicked) {
                    String searchLocationStr = searchView.getQuery().toString().trim();
                    String[] locationParts = searchLocationStr.split(",");
                    if (locationParts.length != 2) {
                        // handle exception instead of crash
                    }
                    ArrayList<Float> searchLocation = new ArrayList<>();
                    try {
                        float location1 = Float.parseFloat(locationParts[0].trim());
                        float location2 = Float.parseFloat(locationParts[1].trim());
                        searchLocation.add(location1);
                        searchLocation.add(location2);
                    } catch (NumberFormatException e) {
                        Toast toast = Toast.makeText(getContext(), "Invalid format for geolocation", Toast.LENGTH_SHORT);
                        toast.show();
                        throw new IllegalArgumentException("Invalid search location format. Must be in the form \"[Location1], [Location2]\"");
                    }
                    // Find the closest QR code based on the search location
                    QRCode closestQRCode = null;
                    float closestDistance = Float.MAX_VALUE;

                    // iterate through QR collection and order and display from there
                    for (QRCode qrCode : QRCodeList) {
                        float distance = calculateDistance(qrCode.getGeolocation(), searchLocation);
                        if (distance < closestDistance) {
                            closestQRCode = qrCode;
                            closestDistance = distance;
                        }
                    }
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

                    QRcAdapter qRcAdapter = new QRcAdapter(sortedQRCodeList, getContext());
                    qrListView.setAdapter(qRcAdapter);

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
