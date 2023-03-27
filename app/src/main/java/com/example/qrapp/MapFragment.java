package com.example.qrapp;


import static com.google.android.gms.location.Priority.*;

import static java.lang.Math.toRadians;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This is a class that extends the Fragment class. This "MapFragment" class contains and
 * maintains the data that will be displayed on the map
 */
public class MapFragment extends Fragment {

    //My Helper is so I can be the model while he is the view
    HelperMapFragment HMFragment;

    //Main Location Objects
    FusedLocationProviderClient FLPC;//Google Client to get location
    LocationRequest LRequest; //Used to set Location requesting Parameters

    LocationCallback locationCallback;//Used to get an update of the phones location

    //Location request params
    int trackingACCURACY = PRIORITY_BALANCED_POWER_ACCURACY;
    int update_interval = 10; // these are used to update location after a certain amount of secs
    int fastest_update_interval = 5; // I times these by 100o because apparently, the function accepts time in millisecs


    //Model/Values for loaction storage and maintenace
    Location curr_location;
    ArrayList<QRCode> closestQRcs;

    //THE DBs
    FirebaseFirestore DB; // how we access a db, just use DB
    FirebaseAuth Auth; // authentication for username


    //Views to display and/or update Model
    TextView points; //shows how many locations are saved
    Button UPDATE; //update locations
    int codes_shown=0;//assume none to start

    //user
    String UsernameBundleKey = "UB";
    String UserIDBundleKey = "ID";

    String PlayerName;
    String PlayerID;



    /**
     * A constructor for the MapFragment. This sets the majority of the relevant attributes
     * for the Map Fragment. This allows for the passing of any surplus data to the Fragment
     * to use without needing to have knowledge of the source
     * @return MapFragment
     */
    public MapFragment(){
        //set attrs really needed as soon as it is created/ can already be set
        closestQRcs = new ArrayList<>();

        DB = FirebaseFirestore.getInstance();
        Auth = FirebaseAuth.getInstance();

        //String userID = Auth.getCurrentUser().getUid();
        //PlayerName = "----"; // default if no username
        //setUsername(userID);


        //MAPS RELATED VARS
        //get results every 10 secs
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                curr_location = locationResult.getLastLocation();
                //can throw null
                if(curr_location!=null) {
                    //addLocationsToMap();
                    int current = Integer.parseInt(points.getText().toString());
                    updateView();

                    //tell user to update if need be
                    int next = closestQRcs.size();
                    if(next!=current)
                        Toast.makeText(getContext(), "Refresh map to see new codes near you!", Toast.LENGTH_LONG).show();

                    Log.d("CURRENT LOCATION","started with location from callback");
                    LatLng curr_LL = new LatLng(curr_location.getLatitude(), curr_location.getLongitude());
                    Log.d("CURRENT LOCATION", String.valueOf(curr_LL.latitude) +  " " + String.valueOf(curr_LL.longitude) + " in callback");
                }else{
                    Log.d("CURRENT LOCATION","No location on callback");

                }
            }
        };

    }


    //used whenvever a new last location is requested
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.

                    startLocUpdates();
                    //checkUserSettingsAndStartLocUpdates();

                } else {
                    //Permission not granted
                    // Maps will be unavailable

                    //IGNORED flow from here since assuming we are getting permission
                    Log.d("CURRENT LOCATION", "Permission Denied");
                }
            });

    /**
     * The onCreateView method sets critical view parameters for the MapFragment
     * such as the display of Nearby Codes and the Update button.
     * With user permission, Location updates begin as soon as the view is created
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        //Connect to XML
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_map, null);

        Bundle userInfo = getArguments();
        PlayerName = userInfo.getString(UsernameBundleKey);
        PlayerID = userInfo.getString(UserIDBundleKey);

        LRequest = new LocationRequest.Builder(trackingACCURACY)
                .setIntervalMillis(update_interval * 1000L)
                .setMinUpdateIntervalMillis(fastest_update_interval * 1000L)
                .build();

        //INIT VIEWS and BUTTONS
        UPDATE = view.findViewById(R.id.button_update);
        points = view.findViewById(R.id.textview_points);



        //BUTTON FUNCTIONS
        UPDATE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startLocUpdates();
                //Ask permission
                if (ActivityCompat
                        .checkSelfPermission(getContext(),
                                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    startLocUpdates();
                }else {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                }
            }
        });

        //want to show immediate created
        //startLocUpdates();
        if (ActivityCompat
                .checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocUpdates();
        }else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }



        return view;
    }





    /**
     * The onDestroy method is called when the MapFragment is destroyed.
     * This method stops getting location updates before the MapFragment is destroyed
     */
    @Override
    public void onDestroy() {
        stopLocUpdates();
        FLPC=null;
        super.onDestroy();

    }


    /**
     * This method keeps a live update of the user's current location.
     * This information is used to determine the closest codes in the area
     */
    @SuppressLint("MissingPermission") //literally asked for permission 3 times before using loc... suppressed
    private void startLocUpdates() {
        FLPC = LocationServices.getFusedLocationProviderClient(getContext());
        FLPC.requestLocationUpdates(LRequest, locationCallback, Looper.getMainLooper());
        //getting last location with location task
        Task<Location> locationTask = FLPC.getLastLocation();

        //WE GOT EM BOYS
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                //BUT, even though already got permissions, computers can throw.
                // Also this only gets the last location... which may not exist if lets say you are actual an android studio emulator
                if (location == null) {
                    Log.d("CURRENT LOCATION", "Got location in getLocation() but was null");

                } else {

                    curr_location = location;
                    addLocationsToMap();
                    updateView();
                    showMapFragment();
                   // Log.d("CURRENT LOCATION","started with location");


                    LatLng curr_LL = new LatLng(curr_location.getLatitude(), curr_location.getLongitude());
                    Log.d("CURRENT LOCATION", String.valueOf(curr_LL.latitude) + "in loc ups");
                    //Toast.makeText(getContext(),String.valueOf(curr_LL.latitude) + "in loc ups" , Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Couldnt get locations
        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("CURRENT LOCATION", "YOU FAILED TO GET LAST LOCATION from loc ups()");
            }
        });

    }


    //to prevent crashing, need to stop getting update BEFORE the fragment dies

    /**
     * This method stops keeping a live update of the user's location.
     * This is used when a MapFragment reaches the end of its lifecycle
     */
    private void stopLocUpdates(){
        FLPC.removeLocationUpdates(locationCallback);
    }


    //communication with the helper map fragment
    // a helper fragment used to actually show the google map

    /**
     * This method initializes the HelperMapFragment to display the appropriate information
     * on a map for the user to see
     */
    private void showMapFragment() {

        HMFragment = new HelperMapFragment();

        // package model data
        Bundle LocationBundle = new Bundle();

        //package QRcs
        Bundle LocationsQRcBundle = new Bundle(); //to pass location data into fragment
        String LocationsQRcDataKey = "LB";
        LocationsQRcBundle.putSerializable(LocationsQRcDataKey, closestQRcs);

        //package current location
        Bundle MyLocationBundle = new Bundle();
        String MyLocationDataKey = "myLB";
        MyLocationBundle.putParcelable(MyLocationDataKey, curr_location);

        //package player
        String MeDataKey = "ME";

        //put all bundles
        LocationBundle.putBundle(LocationsQRcDataKey,LocationsQRcBundle);
        LocationBundle.putBundle(MyLocationDataKey,MyLocationBundle);
        LocationBundle.putString(MeDataKey,PlayerName); //also put username

        //pass to Helper
        HMFragment.setArguments(LocationBundle);


        //show map
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.mapframe, HMFragment)
                .commit();
    }


    //add new set of nearby locations
    //If i can get a list of all the QRcodes from a db or something, I can show the closest within a given range

    /**
     * This method adds the nearest codes to the user's determined location
     * as data to be displayed on the map
     */
    private void addLocationsToMap() {
        if(closestQRcs.size()>=10 || curr_location==null){
            return;
        }
        closestQRcs.clear();
        int Contact_Radius = 5; //all QRc within a xkm radius
        int max_count = 10; // I want only x qrcs to show



        //to stroe in a collection
        CollectionReference QRC_collectionReference = DB.collection("QRCodes");
        //need a key to get to collection/doc. Collection will be referecned by key

        QRC_collectionReference
                .whereNotEqualTo("Geolocation", null)// non null location
                .get() //getem
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int count = 0; // the true counted QRcs
                        GeoPoint QRcLocation;
                        for (QueryDocumentSnapshot QRcDoc: queryDocumentSnapshots) {
                            Location you = curr_location;
                            QRcLocation = (GeoPoint)QRcDoc.get("Geolocation");

                            List<String> alreadyScanned = (List<String>) QRcDoc.get("playersScanned");
                            boolean gotten = alreadyScanned!=null && alreadyScanned.contains(PlayerID);
                            //true if already scanned not null and user has already scanned the code

                            if(!gotten && inRange(Contact_Radius, calculateDistance(you, QRcLocation))  ){//dont add if already gotten
                                closestQRcs.add( new QRCode(
                                        (Object)QRcDoc.get("Comments"),
                                        QRcDoc.getLong("Points").intValue(),
                                        (String)QRcDoc.get("Name"),
                                        (String)QRcDoc.get("icon"),
                                        (Object)alreadyScanned, //may still throw if null but same code everwhere...
                                        QRcLocation,
                                        (String)QRcDoc.get("Hash")) );
                                count++;
                            }
                            if(count==max_count){
                                //
                                // Toast.makeText(getContext(), String.valueOf(count), Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                })
                //if could not get anything
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       Log.d("DB","Data was NOT successfully gotten" + e.toString()); //or just e
                    }
                });




    }


    //update UI

    /**
     * This method updates the view of the number of codes nearests to the user
     */
    private void updateView() {
        if (curr_location == null) {
            //Toast.makeText(getContext(), "No Location found", Toast.LENGTH_SHORT).show();
            return;
        }
        //Toast.makeText(getContext(), String.format(Locale.CANADA, "%.5f", curr_location.getLatitude()), Toast.LENGTH_SHORT).show();
        //put location values in view

        //update points total
        int next = closestQRcs.size();
        Log.d("MAP", String.valueOf(next));
        points.setText(String.format(Locale.CANADA, "%d", next));
    }


    /**
     This method calculates the distance between a Location and a GeoPoint
     @param point1 the Location
     @param point2 the GeoPoint
     @return the distance between the Location and GeoPoints
     */
    private double calculateDistance(Location point1, GeoPoint point2) {
        int Earth_Radius = 6371;
        double lat1 = toRadians(point1.getLatitude());
        double lon1 = toRadians(point1.getLongitude());
        double lat2 = toRadians(point2.getLatitude());
        double lon2 = toRadians(point2.getLongitude());
        double x = (lon2 - lon1) * Math.cos((lat1 + lat2) / 2);
        double y = (lat2 - lat1);
        return Math.sqrt(x * x + y * y) * Earth_Radius;
    }


    /**
     * This method returns true if a given distance is less than a given threshold
     * @param threshold the bounding distance
     * @param wantsIn the distance being compared
     * @return
     */
    private boolean inRange(double threshold, double wantsIn){
        return wantsIn <= threshold;
    }


}