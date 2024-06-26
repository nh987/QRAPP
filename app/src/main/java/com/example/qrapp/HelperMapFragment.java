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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

//Fragment Class to show map on


/**
 * This class extends the Fragment class. The "HelperMapFragment" class presents the data
 * maintained by the "MapFragment" class on an interactive map.
 */
public class HelperMapFragment extends Fragment{

    //Data
    ArrayList<QRCode> QRcs; //list of locations
    ArrayList<QRCode> scanned; //list of locations

    int N; //number of locations
    Location current;
    String Username;
    Bundle LocationBundle; // bundle of all needed items for map markers

    // keys
    String LocationsQRcDataKey = "LB";
    String ScannedQRcDataKey = "SB";
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
        scanned = (ArrayList<QRCode>) LocationBundle.getBundle(ScannedQRcDataKey).getSerializable(ScannedQRcDataKey);//already scanned these


        //GET MY CURRENT LOCATION
        current = (Location) LocationBundle.getBundle(MyLocationDataKey).getParcelable(MyLocationDataKey);

        //GET MY UNIQUE USERNAME
        Username = LocationBundle.getString(MeDataKey);
        N = QRcs.size()+scanned.size();

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
                Map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLL, Zoom+4));

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
                    LatLng startLL=null;

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
                            scanned.removeIf(code -> Objects.equals(code.getName(), codename));


                        }else{
                            marker.setPosition(startLL);
                            Log.d("POS", "At end " + startLL.toString());
                            marker.setRotation(0);

                            float x = marker.getAlpha();
                            if(x==0.45f || x==0.95f) {//either normal or dragged, set back to normal at drag end
                                marker.setAlpha(0.95f);
                            }else{
                                marker.setAlpha(1f);
                            }
                            marker.setVisible(true);
                            marker.setInfoWindowAnchor(0.5f,0);
                            markerBounce(Map,marker,startLL);
                        }
                        TRASH.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onMarkerDragStart(@NonNull Marker marker) {

                        String name = marker.getSnippet();
                        ArrayList<LatLng> allCodesLL = new ArrayList<>();
                        ArrayList<String> allNames = new ArrayList<>();

                        for(QRCode code:QRcs){
                            allCodesLL.add(LatLngify(code.getGeolocation()));
                            allNames.add(code.getName());
                        }
                        for(QRCode code:scanned){
                            allCodesLL.add(LatLngify(code.getGeolocation()));
                            allNames.add(code.getName());
                        }
                        startLL = allCodesLL.get(allNames.indexOf(name));



                        Log.d("POS", "At start " + startLL.toString());

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
                for (QRCode QRc : scanned) { //add a marker for each location
                    ll = LatLngify(QRc.getGeolocation());
                    MarkerOptions MOptions = new MarkerOptions();
                    MOptions.position(ll);
                    MOptions
                            .alpha(0.95f)
                            .title(String.format("%s      %s %s",QRc.getIcon(),QRc.getPoints(),"points [scanned]"))
                            .snippet(QRc.getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
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
                    MOptions.zIndex(1000000);//on top

                    googleMap.addMarker(MOptions);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLL, Zoom));
                }

            }
        });


        fragmentTransaction.replace(R.id.google_map, SMH).commit(); //SHOW MAP


        return view;
    }

    /**
     * This method sets the listeners for the HelperMapFragment to communicate
     * map changes with the MapFragment when the fragment is attached
     * @param context
     */
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
    /**
     * This method sets all listeners to null before detaching the fragment
     */
    @Override
    public void onDetach() {
        super.onDetach();
        update_listener=null;
    }


    //state of a marker while dragging

    /**
     * This method sets the state of a marker when it is being dragged
     * @param marker
     */
    private void OnDragState(Marker marker) {
        float x = marker.getAlpha();

        if(x==0.95f || x==0.45f){
            marker.setAlpha(0.45f);
        }else {
            marker.setAlpha(0.5f);
        }
        marker.setRotation(180);
    }

    //trash UI while dragging

    /**
     * This method sets the state of the trash when a marker is being dragged
     * and when a marker is near the trash
     * @param marker
     */
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
     * @return QRCode
     */
    private QRCode getQRc(String chosen) {
        QRCode placeholder = null;

        for (QRCode QRc:QRcs) {
            if (Objects.equals(QRc.getName(), chosen)) {
                placeholder = QRc;
                break;
            }
        }
        if(placeholder==null){
            for (QRCode QRc:scanned) {
                if (Objects.equals(QRc.getName(), chosen)) {
                    placeholder = QRc;
                    break;
                }
            }
        }
        return placeholder;
    }


    // convert from GeoPoint to LatLng, maps used LatLng

    /**
     * This method converts a GeoPoint object to a LatLng object
     * @param geo
     * @return LatLng
     */
    private LatLng LatLngify(GeoPoint geo){
        return new LatLng(geo.getLatitude(),geo.getLongitude());
    }



}


/*CITATIONS

1)Show map on fragment
https://www.geeksforgeeks.org/how-to-implement-google-map-inside-fragment-in-android/
https://www.youtube.com/watch?v=YCFPClPjDIQ

2)listener between fragments
https://codinginflow.com/tutorials/android/fragment-to-fragment-communication-with-interfaces

3) Making custom markers
https://github.com/googlemaps/android-samples/tree/master/ApiDemos/java/app/src/gms/java/com/example/mapdemo
https://github.com/googlemaps/android-samples/blob/master/ApiDemos/java/app/src/gms/java/com/example/mapdemo/MarkerDemoActivity.java

4)Set z index
https://cloud.google.com/blog/products/maps-platform/marker-zindex-and-more-come-to-google
https://stackoverflow.com/questions/7932727/max-initial-zindex-for-google-maps-v3-markers

5)Marker bounce
https://stackoverflow.com/questions/8191582/how-to-animate-marker-when-it-is-added-to-map-on-android
https://stackoverflow.com/questions/7339200/bounce-a-pin-in-google-maps-once
https://www.wpmapspro.com/example/bounce-animation-marker-click-google-maps/

6)Custom info window
https://www.youtube.com/watch?v=DhYofrJPzlI&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=11


7) Add marker to google map
https://www.youtube.com/watch?v=s_6xxTjoLGY&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=7


8)Marker settings
https://stackoverflow.com/questions/32943568/android-google-map-infowindow-anchor-point-after-marker-rotation
http://www.learnsharecorner.com/javascript/working-google-map-api-v3-infowindow-custom-positioning-based-on-space-from-top-left-right-bottom/
https://stackoverflow.com/questions/2472957/how-can-i-change-the-color-of-a-google-maps-marker
https://developers.google.com/maps/documentation/android-sdk/marker



 */




