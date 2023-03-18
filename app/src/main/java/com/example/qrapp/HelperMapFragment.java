package com.example.qrapp;


import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

//Fragment Class to show map on
public class HelperMapFragment extends Fragment{

    //Data
    ArrayList<QRCode> QRcs; //list of locations
    int N; //number of locations
    Location current;
    String Username;

    Bundle LocationBundle; // bundle of all needed items for map markers

    // keys
    String LocationsQRcDataKey = "LB";
    String MyLocationDataKey = "myLB";
    String MeDataKey = "ME";

    //Map
    SupportMapFragment SMH; //the google map needs its own support fragment
    int Zoom = 18; // how much to zoom in
    QRcMarkerInfoWindowAdapter QRcMarkerAdapter; // a custom view and behaviour for the markers for QRcs

    //Buttons
    ImageButton CENTRE;

    //
    GoogleMap Map;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        LocationBundle = getArguments();

        //GET CLOSEST CODES
        QRcs = (ArrayList<QRCode>) LocationBundle.getBundle(LocationsQRcDataKey).getSerializable(LocationsQRcDataKey); //location list object passed as bundle so get the bundle


        //GET MY CURRENT LOCATION
        current = (Location) LocationBundle.getBundle(MyLocationDataKey).getParcelable(MyLocationDataKey);

        //GET MY UNIQUE USERNAME
        Username = LocationBundle.getString(MeDataKey);
        N = QRcs.size();

        //Marker need their own view adapter
        QRcMarkerAdapter = new QRcMarkerInfoWindowAdapter(getContext());





    }







    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_map_helper, container, false);


        //A button to find yourself
        CENTRE = view.findViewById(R.id.centre);
        CENTRE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng myLL = new LatLng(current.getLatitude(), current.getLongitude());
                Map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLL, Zoom));
            }
        });

        for (QRCode QRc:QRcs
        ) {//just a sanity check
            Log.d("TAG",QRc.toString());
        }
        Toast.makeText(getContext(), String.format(Locale.CANADA,"%d locations shown", N), Toast.LENGTH_LONG).show();

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SMH = SupportMapFragment.newInstance();



        //call google map and show it
        SMH.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                Map = googleMap;
                googleMap.clear(); //remove anything on map
                googleMap.setInfoWindowAdapter(QRcMarkerAdapter);

                //set an on click listener for the window to get QRc Profile
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(@NonNull Marker marker) {
                        String chosen = marker.getSnippet();
                        QRCode chosenQRcToSee = getQRc(chosen);
                        if(chosenQRcToSee!=null){
                            //start new QRcProfile Activity
                            Intent intent = new Intent(getActivity(), QRProfile.class);
                            intent.putExtra("qr_code", chosenQRcToSee); // pass the clicked item to the QRCProfile class
                            Log.d("QRC","starting new QRc Profile Activity");
                            startActivity(intent);
                        }else{
                            Log.d("QRC","Failed to start new QRc Profile Activity");
                        }
                    }
                });

                // for when a marker is clicked
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        LatLng markerLocation = marker.getPosition();

                        // Zoom in on em
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerLocation, Zoom));

                        //Make em bounce
                        markerBounce(googleMap, marker, markerLocation);
                        return false;
                    }
                });



                //ADD EM TO MAP
                LatLng ll; // map uses LatLng obj as opposed to GeoPoint
                for (QRCode QRc : QRcs) { //add a marker for each location
                    ll = LatLngify(QRc.getGeolocation());
                    MarkerOptions MOptions = new MarkerOptions();
                    MOptions.position(ll);
                    MOptions
                            .title(String.format("%s      %s %s",QRc.getIcon(),QRc.getPoints(),"points"))
                            .snippet(QRc.getName());

                    googleMap.addMarker(MOptions);
                }

                //for my location specifically
                if(current!=null) {// zoom in on the phones current location, its the last location
                    LatLng myLL = new LatLng(current.getLatitude(), current.getLongitude());

                    MarkerOptions MOptions = new MarkerOptions();
                    MOptions.position(myLL);
                    MOptions
                            .title("YOU ARE HERE")
                            .snippet(Username);
                    MOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                    googleMap.addMarker(MOptions);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLL, Zoom));
                }

            }
        });


        fragmentTransaction.replace(R.id.google_map, SMH).commit(); //SHOW MAP



        return view;
    }

    //make a marker bounce in place
    private void markerBounce(GoogleMap googleMap, Marker marker, LatLng markerLocation) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = googleMap.getProjection();
        Point startPoint = proj.toScreenLocation(markerLocation);
        startPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1500;
        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * markerLocation.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * markerLocation.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    private QRCode getQRc(String chosen) {
        QRCode placeholder = null;
        for (QRCode QRc:QRcs) {
            if(Objects.equals(QRc.getName(), chosen)){
                return QRc;
            }
        }
        return placeholder;
    }


    // convert from GeoPoint to LatLng, maps used LatLng
    private LatLng LatLngify(GeoPoint geo){
        return new LatLng(geo.getLatitude(),geo.getLongitude());
    }



}




