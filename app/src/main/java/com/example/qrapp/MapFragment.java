package com.example.qrapp;


import static com.google.android.gms.location.Priority.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class MapFragment extends Fragment {

    FusedLocationProviderClient FLPC;//Google Client to get local
    LocationRequest LRequest; //Used to set Location requesting Parameters
    LocationCallback locationCallback;//Used to get an update of the phones location


    Location curr_location;
    ArrayList<LatLng> QRcLocations;

    TextView points; //shows how many locations are saved
    Button UPDATE; //update locations


    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();

                    update();
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
//                    request permissions

//
                    Toast.makeText(getContext(), "Permission NOT Granted", Toast.LENGTH_SHORT).show();
                }

            });


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        QRcLocations = new ArrayList<>();

        //for location updates every 10 or 5 secs
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //save new last location
                Location location = locationResult.getLastLocation();
                curr_location = location;


                updateView(location);
            }
        };


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        //set attrs

        int update_interval=10; // these are used to update location after a certain amount of secs
        int fastest_update_interval = 5; // I times these by 100o because apparently, the function accepts time in millisecs

        LRequest = new LocationRequest.Builder(PRIORITY_BALANCED_POWER_ACCURACY)
                .setIntervalMillis(update_interval * 1000) //set location requesting params
                .setMinUpdateIntervalMillis(fastest_update_interval * 1000)
                .build();


        View view = inflater.inflate(R.layout.fragment_map, container, false);
        UPDATE = view.findViewById(R.id.button_addpoint);
        points = view.findViewById(R.id.textview_points);


        //button to update points on the map
        UPDATE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                update(); //update locs in list
            }
        });


        update(); //want to immediate show the points after BOTH Fragment and its view are created

        return view;
    }


    @SuppressLint("MissingPermission")
    private void update() {

        //Client allows retrieval of location
        FLPC = LocationServices.getFusedLocationProviderClient(getContext());

        //Ask permission
        if (ActivityCompat
                .checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //user said yes, get their last location
            FLPC.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location == null) {// something went wrong, location not found
                        Toast.makeText(getContext(), "NULLudpated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "udpated", Toast.LENGTH_SHORT).show();

                        //use location to add nearby points and updateviews
                        addLocationsToMap(location);
                        updateView(location);

                        //Fragment to show map on
                        showMapFragment();
                    }


                }
            });

        } else {
            //rejected
            requestPermissionLauncher.launch(//request permissions
                    Manifest.permission.ACCESS_FINE_LOCATION);
            //Toast.makeText(getContext(), "Location Permission requested", Toast.LENGTH_SHORT).show();


        }
    }

    // a helper fragment used to actually show the google map
    private void showMapFragment() {
        Fragment HMFragment = new HelperMapFragment();

        Bundle LocationBundle = new Bundle(); //to pass location data into fragment
        String LocationDataKey = "LB";
        LocationBundle.putSerializable(LocationDataKey, QRcLocations);
        HMFragment.setArguments(LocationBundle);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.mapframe, HMFragment)
                .commit();
    }


    //add new set of nearby locations
    //TODO: This is where the db/a list of the closest  QRc locations to the phone is set
    //If i can get a list of all the QRcodes from a db or something, I can show the closest within a given range
    private void addLocationsToMap(Location location) {
        curr_location = location; //set/update phone location



        double Lat = curr_location.getLatitude(), Long = curr_location.getLatitude();
        QRcLocations.clear(); //clear whatever is there.

        //add random nearby locations
        for (int i = 10; i > 0; i--) {
            QRcLocations.add(
                    new LatLng(
                            ThreadLocalRandom.current().nextDouble(Lat - 0.2, Lat + 0.2),
                            ThreadLocalRandom.current().nextDouble(Long - 0.2, Long + 0.2)
                    ));
        }
        //QRcLocations(new LatLng(Lat, Long)); //add current location as well as a referecne point
        QRcLocations.add(new LatLng(Lat,Long));
    }


    //update UI
    private void updateView(Location location) {
        if (location == null) {
            Toast.makeText(getContext(), "No Location found", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getContext(), String.format(Locale.CANADA, "%.5f", location.getLatitude()), Toast.LENGTH_SHORT).show();
        //put location values in view

        //update points total
        points.setText(String.format(Locale.CANADA, "%d", QRcLocations.size()));
    }

    // convert from GeoPoint to LatLng, maps used LatLng
    private LatLng LatLngify(GeoPoint geo){
        return new LatLng(geo.getLatitude(),geo.getLongitude());
    }

}

