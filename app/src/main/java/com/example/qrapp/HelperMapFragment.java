package com.example.qrapp;


import static androidx.appcompat.content.res.AppCompatResources.getDrawable;
import static java.lang.Math.abs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.firebase.firestore.auth.User;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

//Fragment Class to show map on


/**
 * This class extends the Fragment class. The "HelperMapFragment" class presents the data
 * maintained by the "MapFragment" class on an interactive map.
 */
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


    //Buttons
    ImageButton CENTRE;
    ImageView TRASH;

    //Map
    SupportMapFragment SMH; //the google map needs its own support fragment
    int Zoom = 14; // how much to zoom in
    QRcMarkerInfoWindowAdapter QRcMarkerAdapter; // a custom view and behaviour for the markers for QRcs
    GoogleMap Map;

    public interface HMFListener{
        void onRemovedMarker(boolean lost);
    }
    HMFListener update_listener;



    /**
     * The onCreate method gets the data passed to the "HelperMapFragment" by the "MapFragment" and
     * @param savedInstanceState
     */
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



    //// End of Update Interface


    /**
     * The onCreateView method prepares to display the data on the map by setting all the view
     * attributes and items. It puts all the markers on the map and makes the map interactive.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_map_helper, container, false);


//        A button to find yourself
        CENTRE = view.findViewById(R.id.centre);
        CENTRE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.animate().translationZ(1000);
                LatLng myLL = new LatLng(current.getLatitude(), current.getLongitude());
                Map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLL, Zoom+3));

            }
        });

        //A place to throw your trash
        TRASH=view.findViewById(R.id.trash);
        TRASH.setVisibility(View.INVISIBLE);
//        TRASH.setOnHoverListener(new View.OnHoverListener() {
//            @Override
//            public boolean onHover(View v, MotionEvent event) {
//                TRASH.setHovered(true);
//                return false;
//            }
//        });



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
                        }else{//def me show me!
                            //start new PlayerProfile Activity
                            Intent intent = new Intent(getActivity(), PlayerProfileActivity.class);
                            intent.putExtra("player", Username); // pass the clicked item to the QRCProfile class
                            Log.d("PLAYER","starting new Player Profile Activity");
                            startActivity(intent);
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

                //for when a marker is dragged
                googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    LatLng startLL;

                    @Override
                    public void onMarkerDrag(@NonNull Marker marker) {
                        OnDragState(marker);
                        OnDragTrashUI(marker);
                        marker.setInfoWindowAnchor(0.5f,0.9f);
                        marker.showInfoWindow();
                    }

                    @Override
                    public void onMarkerDragEnd(@NonNull Marker marker) {
                        LatLng markerLocation = marker.getPosition();
                        Projection proj = googleMap.getProjection();
                        Point endPoint = proj.toScreenLocation(markerLocation);

                        int[] trash = new int[2];
                        TRASH.getLocationOnScreen(trash);

                        Log.d("MAP", endPoint + " " + Arrays.toString(trash));

                        if((trash[0]<=endPoint.x&&endPoint.x<=trash[0]+200) && abs(trash[1]-endPoint.y)<400){
                            String name = marker.getSnippet();
                            Toast.makeText(getContext(), name + " removed from map.", Toast.LENGTH_SHORT).show();
                            Log.d("MAP","trashed marker "+name );
                            marker.setVisible(false);

                            //let view know that we have less codes displayed
                            update_listener.onRemovedMarker(true);

                            //remove from actual list
                            String codename = marker.getSnippet();
                            QRcs.removeIf(code -> Objects.equals(code.getName(), codename));


                        }else{
                            marker.setPosition(startLL);
                            marker.setRotation(0);
                            marker.setAlpha(1f);
                            marker.setVisible(true);
                            marker.setInfoWindowAnchor(0.5f,0);
                            markerBounce(Map,marker,startLL);
                        }
                        TRASH.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onMarkerDragStart(@NonNull Marker marker) {
//                        Projection proj = googleMap.getProjection();
//
//                        startLL = marker.getPosition();
//                        Point startPoint = proj.toScreenLocation(startLL);
//                        startPoint.offset(0, 100);
//                        startLL = proj.fromScreenLocation(startPoint);

                        String name = marker.getSnippet();
                        for(QRCode code:QRcs){
                            if(Objects.equals(code.getName(), name)){
                                startLL = LatLngify(code.getGeolocation());
                            }
                        }

                        TRASH.setVisibility(View.VISIBLE);

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
                            .snippet(QRc.getName())
                            .draggable(true);

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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof HMFListener){
            update_listener = (HMFListener) context;
        }else{
            throw new RuntimeException(context.toString() + "MUST IMPLEMENT HMFListener");
        }
    }

    //HANDLE NO CONTEXT CRASH
    @Override
    public void onDetach() {
        super.onDetach();
        update_listener=null;
    }


    //state of a marker while dragging
    private void OnDragState(Marker marker) {
        marker.setAlpha(0.5f);
        marker.setRotation(180);
    }

    //trash UI while dragging
    private void OnDragTrashUI(Marker marker) {
        LatLng markerLocation = marker.getPosition();
        Projection proj = Map.getProjection();
        Point endPoint = proj.toScreenLocation(markerLocation);

        int[] trash = new int[2];
        TRASH.getLocationOnScreen(trash);

        Log.d("MAP1", endPoint + " " + Arrays.toString(trash));

        if((trash[0]<=endPoint.x&&endPoint.x<=trash[0]+200) && abs(trash[1]-endPoint.y)<400){
            TRASH.setImageDrawable(getDrawable(getContext(), R.drawable.ic_baseline_delete_24_other));
        }else {
            TRASH.setImageDrawable(getDrawable(getContext(), R.drawable.ic_baseline_delete_24));
        }
    }


    //make a marker bounce in place
    /**
     * This method causes the google map markers to bounce in place
     * @param googleMap
     * @param marker
     * @param markerLocation
     */
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

    /**
     * This method return the QRcode the user selects on a the map
     * @param chosen
     * @return
     */
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

    /**
     * This method converts a GeoPoint object to a LatLng object
     * @param geo
     * @return
     */
    private LatLng LatLngify(GeoPoint geo){
        return new LatLng(geo.getLatitude(),geo.getLongitude());
    }



}




