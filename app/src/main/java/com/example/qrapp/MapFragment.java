package com.example.qrapp;


import static android.location.LocationRequest.*;
import static com.google.android.gms.location.Priority.*;

import static java.lang.Math.toRadians;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.internal.ApiKey;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class MapFragment extends Fragment {

    //My Helper is so I can be the model while he is the view
    HelperMapFragment HMFragment;

    //Main Location Objects
    FusedLocationProviderClient FLPC;//Google Client to get location
    LocationRequest LRequest; //Used to set Location requesting Parameters

    LocationCallback locationCallback;//Used to get an update of the phones location

    LocationSettingsRequest LSRequest; //these for setting check so not locked out bu user
    SettingsClient SClient;

    //request codes
    int requestCode_Rationale = 10000;
    int requestCode_SettingsFailure = 11000;

    //Location request params
    int trackingACCURACY = PRIORITY_BALANCED_POWER_ACCURACY;
    int update_interval = 10; // these are used to update location after a certain amount of secs
    int fastest_update_interval = 5; // I times these by 100o because apparently, the function accepts time in millisecs


    //Model/Values for loaction storage and maintenace
    Location curr_location;
    ArrayList<LatLng> QRcLocations;
    ArrayList<QRCode> closestQRcs;

    //THE DB
    FirebaseFirestore DB; // how we access a db, just use DB

    //Views to display and/or update Model
    TextView points; //shows how many locations are saved
    Button UPDATE; //update locations

    //used whenvever a new last location is requested
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.

                    checkUserSettingsAndStartLocUpdates();
                    if(curr_location!=null) {
                        addLocationsToMap();
                        updateView();
                        showMapFragment();
                        Log.d("CURRENT LOCATION","not null");
                    }else{
                        Log.d("CURRENT LOCATION","No location on found");
                    }

                } else {
                    //Permission not granted
                    // Maps will be unavailable

                    //IGNORED flow from here since assuming we are getting permission
                    Log.d("CURRENT LOCATION", "Permission Denied");
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set attrs really needed as soon as it is created/ can already be set
        QRcLocations = new ArrayList<>();
        closestQRcs = new ArrayList<>();

        FLPC = LocationServices.getFusedLocationProviderClient(getContext());

        LRequest = new LocationRequest.Builder(trackingACCURACY)
                .setIntervalMillis(update_interval * 1000L)
                .setMinUpdateIntervalMillis(fastest_update_interval * 1000L)
                .build();

        //get results every 10 secs
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //can throw null
                if(locationResult==null){
                    Log.d("LOCATION CALLBACK", "No new locations from updates");
                    return;
                }else{
                    curr_location = locationResult.getLastLocation();
                    if(curr_location!=null) {
                        //addLocationsToMap();
                        Log.d("CURRENT LOCATION","started with location from callback");
                    }else{
                        Log.d("CURRENT LOCATION","No location on callback");

                    }


                    LatLng curr_LL = new LatLng(curr_location.getLatitude(), curr_location.getLongitude());
                    Log.d("CURRENT LOCATION", String.valueOf(curr_LL.latitude) +  " " + String.valueOf(curr_LL.longitude) + " in callback");
                }
            }
        };




    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        //Connect to XML
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_map, null);

        //INIT VIEWS and BUTTONS
        UPDATE = view.findViewById(R.id.button_update);
        points = view.findViewById(R.id.textview_points);

        //MAPS RELATED VARS
        int requestCode_Rationale = 10000;


        //BUTTON FUNCTIONS
        UPDATE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION);

                startLocUpdates();
                if(curr_location!=null){
                    addLocationsToMap();
                    updateView();
                    showMapFragment();
                    Log.d("CURRENT LOCATION",String.valueOf(curr_location) + " updated");
                }else{
                    Log.d("CURRENT LOCATION","Location not updated");
                }
            }
        });

        //want to show immediate created
        startLocUpdates();



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // 1)permission for location
        if (ActivityCompat //PERMISSION GRANTED
                .checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            checkUserSettingsAndStartLocUpdates();
            if(curr_location!=null) {
                addLocationsToMap();
                updateView();
                showMapFragment();
                Log.d("CURRENT LOCATION","started with location in onStart()");
            }else {
                Log.d("CURRENT LOCATION","started with no location in onStart()");
            }




        } else { //PERMISSION DENIED
            requestLocationPermission();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopLocUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocUpdates();
        FLPC=null;
    }


    //use when permission NOT granted
    public void requestLocationPermission() {

        if (ActivityCompat //if permission not granted on create, ask again, since they opened the fragment again
                .checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // give reason why, ignored fore now
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d("RATIONALE", "SHOW PERMISSION RATIONALE with ALERT DIALOG");
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        requestCode_Rationale);
                //requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);


            } else { // directly ask for permission, they have since the rationale now
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }

        }
    }


    private void checkUserSettingsAndStartLocUpdates() {

        //CHECK SETTING, we may be locked out of location by user
        LSRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(LRequest)
                .build();

        SClient = LocationServices.getSettingsClient(getContext());//used to check setting for loc

        Task<LocationSettingsResponse> LSR_Task = SClient.checkLocationSettings(LSRequest);
        //success and failure listeners

        LSR_Task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // Location setting good
                //UPDATE location
                //Ask permission
                if (ActivityCompat
                        .checkSelfPermission(getContext(),
                                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    startLocUpdates();

                }else{
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                }


            }
        });

        LSR_Task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //some or all setting not good. ask to be turned on/foo
                if (e instanceof ResolvableApiException) { //if exception can be auto resolved, do it
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        //let android studio handle it
                        apiException.startResolutionForResult(getActivity(), requestCode_SettingsFailure);

                    } catch (IntentSender.SendIntentException Ie) {
                        Ie.printStackTrace(); //even android studio could handle it
                    }
                } else {
                    e.printStackTrace(); //just show what happened
                }
            }
        });


    }

    @SuppressLint("MissingPermission") //literally asked for permission 3 times before using loc... suppressed
    private void startLocUpdates() {
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
                    Toast.makeText(getContext(),String.valueOf(curr_LL.latitude) + "in loc ups" , Toast.LENGTH_SHORT).show();
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
    private void stopLocUpdates(){
        FLPC.removeLocationUpdates(locationCallback);
    }


    //communication with the helper map fragment
    // a helper fragment used to actually show the google map
    private void showMapFragment() {

        HMFragment = new HelperMapFragment();

        // package model data
        Bundle LocationBundle = new Bundle(); //to pass location data into fragment
        String LocationDataKey = "LB";
        LocationBundle.putSerializable(LocationDataKey, closestQRcs);

        //pass to Helper
        HMFragment.setArguments(LocationBundle);


        //show map
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.mapframe, HMFragment)
                .commit();
    }


    //add new set of nearby locations
    //TODO: This is where the db/a list of the closest  QRc locations to the phone is set
    //If i can get a list of all the QRcodes from a db or something, I can show the closest within a given range
    private void addLocationsToMap() {
        int Contact_Radius = 500000; //all QRc within a xkm radius
        int max_count = 10; // I want only x qrcs to show


        DB = FirebaseFirestore.getInstance();

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

                            if(  inRange(Contact_Radius, calculateDistance(you, QRcLocation))  ){
                                closestQRcs.add( new QRCode(
                                        (Object)QRcDoc.get("Comments"),
                                        QRcDoc.getLong("Points").intValue(),
                                        (String)QRcDoc.get("Name"),
                                        (String)QRcDoc.get("icon"),
                                        (Object)QRcDoc.get("playersScanned"),
                                        QRcLocation) );
                                count++;
                            }
                            if(count==max_count){break;}
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

























        /*
        double Lat = curr_location.getLatitude(), Long = curr_location.getLongitude();
        QRcLocations.clear(); //clear whatever is there.

        //add random nearby locations for now.
        //This loop is actually supposed to add the top X locations closest to user
        double bound = 0.001;
        for (int i = 10; i > 0; i--) {
            QRcLocations.add(
                    new LatLng(
                            ThreadLocalRandom.current().nextDouble(Lat - bound, Lat + bound),
                            ThreadLocalRandom.current().nextDouble(Long - bound, Long + bound)
                    ));
        }
        //QRcLocations(new LatLng(Lat, Long)); //add current location as well as a referecne point
        QRcLocations.add(new LatLng(Lat,Long));

         */
    }


    //update UI
    private void updateView() {
        if (curr_location == null) {
            Toast.makeText(getContext(), "No Location found", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getContext(), String.format(Locale.CANADA, "%.5f", curr_location.getLatitude()), Toast.LENGTH_SHORT).show();
        //put location values in view

        //update points total
        points.setText(String.format(Locale.CANADA, "%d", QRcLocations.size()));
    }


    /**
     This method calculates the distance between two GeoPoints
     @param point1 the first GeoPoint
     @param point2 the second GeoPoint
     @return the distance between the two GeoPoints
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

    private boolean inRange(double threshold, double wantsIn){
        return wantsIn <= threshold;
    }



}