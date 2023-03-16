package com.example.qrapp;


import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Locale;

//Fragment Class to show map on
public class HelperMapFragment extends Fragment {

    //Data
    ArrayList<QRCode> QRcs; //list of locations
    int N; //number of locations
    Bundle LocationBundle;
    String LocationDataKey = "LB";

    //Map
    SupportMapFragment SMH; //the google map needs its own support fragment
    int Zoom = 18; // how much to zoom in

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationBundle = getArguments();

        QRcs = (ArrayList<QRCode>) LocationBundle.getSerializable(LocationDataKey); //location list object passed as bundle so get the bundle
        N = QRcs.size();

        for (QRCode QRc:QRcs
             ) {
            Log.d("TAG",QRc.toString());
        }
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

                //ADD EM TO MAP
                LatLng ll; // map uses LatLng obj as opposed to GeoPoint
                for (QRCode QRc : QRcs) { //add a marker for each location
                    ll = LatLngify(QRc.getGeolocation());
                    MarkerOptions MOptions = new MarkerOptions();
                    MOptions.position(ll);
                    /*
                    if(QRcs.indexOf(QRc)==N-1) {
                        MOptions.title("YOU ARE HERE");
                        MOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    }else {
                        MOptions.title(ll.latitude + ", " + ll.longitude);
                    }

                     */
                    MOptions
                            .title( QRc.getIcon())
                            .snippet(String.format("%s\n%s",QRc.getName(),QRc.getPoints()) );

                    googleMap.addMarker(MOptions);
                }
                if(!QRcs.isEmpty() && N>0)// zoom in on the phones current location, its the last location
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLngify(QRcs.get(N-1).geolocation), Zoom));


            }
        });

        fragmentTransaction.replace(R.id.google_map, SMH).commit(); //SHOW MAP



        return view;
    }



    // convert from GeoPoint to LatLng, maps used LatLng
    private LatLng LatLngify(GeoPoint geo){
        return new LatLng(geo.getLatitude(),geo.getLongitude());
    }


}




