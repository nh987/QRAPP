package com.example.qrapp;


import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Locale;

//Fragment Class to show map on
public class HelperMapFragment extends Fragment {

    ArrayList<LatLng> QRcLocations; //list of locations
    int N; //number of locations

    SupportMapFragment SMH; //the google map needs its own support fragment

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String LocationDataKey = "LB";
        QRcLocations = (ArrayList<LatLng>) getArguments().getSerializable(LocationDataKey); //location list object passed as bundle so get the bundle
        N = QRcLocations.size();
        Toast.makeText(getContext(), String.format(Locale.CANADA,"%d locations shown", N), Toast.LENGTH_LONG).show();

    }







    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_map_helper, container, false);

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SMH = SupportMapFragment.newInstance();


        //call google map and show it
        SMH.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                googleMap.clear(); //remove anything on map
                //LatLng last=new LatLng(0,0);
                for (LatLng ll : QRcLocations) { //add a marker for each location
                    MarkerOptions MOptions = new MarkerOptions();
                    MOptions.position(ll);
                    if(QRcLocations.indexOf(ll)==N-1) {
                        MOptions.title("YOU ARE HERE");
                        MOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    }else {
                        MOptions.title(ll.latitude + ", " + ll.longitude);
                    }

                    googleMap.addMarker(MOptions);
                }
                if(!QRcLocations.isEmpty())// zoom in on the phones current location, its the last location
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(QRcLocations.get(N-1), 10));


            }
        });

        fragmentTransaction.replace(R.id.google_map, SMH).commit(); //SHOW MAP



        return view;
    }



}




