package com.example.qrapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;


/**
 * This is a class that extends the Fragment class. This "RankFragment" class contains and
 * maintains the data that will be displayed on the leaderboard(s)
 */
public class RankFragment extends Fragment {


    Spinner RANK_SPINNER; //select 1 of 4 leaderboards
    ArrayAdapter<CharSequence> RankSpinnerAdapter;

    //Database
    FirebaseAuth Auth;
    FirebaseFirestore DB;
    CollectionReference UserCR, QRCodeCR; //reference to the players and the qrcodes
    String userID;

    //dataholders, temporary storage of some data
    //List<String> players;
    int null_users = 0;

    //for location
    //used whenvever a new last location is requested
    String my_region;
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    updateRegion(); //have permission to use region postal code
                    Log.d("CURRENT LOCATION", "Permission Granted");
                    Log.d("CURRENT LOCATION", "Region = "+my_region);
                } else {
                    //Permission not granted
                    // Local Ranking will be unavailable or inaccurate
                    //IGNORED flow from here since assuming we are getting permission
                    Log.d("CURRENT LOCATION", "Permission Denied");
                }
            });

    //tops
    int X = 10; // will do top 10
    ArrayList<RankPair> Sum_Or_Count; //holds data for all players for sum and count ranking
    ArrayList<RankTriple> Score_Or_Local; //holds data for all players for score and local ranking

    ArrayList<RankPair> topPairs; //these will hold the true top 10
    ArrayList<RankTriple> topTriples;

    //passing data
    String RankBundleKey = "RB"; //use the same key to get the data passed


    /**
     * The onCreate method sets most of the attributes required to maintain ranking data
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Auth = FirebaseAuth.getInstance();
        DB = FirebaseFirestore.getInstance();
        UserCR = DB.collection("Users");
        QRCodeCR = DB.collection("QRCodes");
        userID = Auth.getCurrentUser().getUid();
        setRegion();


        //players = new ArrayList<>();

    }

    /**
     * This method sets the region of the current user to use for local rankings. It first checks
     * for permissions and requests it. If permission is not granted, the default location is used
     */
    private void setRegion() {

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // requirement to check permission
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            //return;
        }else{
            updateRegion();
        }


    }

    /**
     * This method gets the user's postal code from their current location and update this value
     * in the database. If the postal code cannot be determined,
     * the last known region in the database is used
     */
    @SuppressLint("MissingPermission")
    public void updateRegion(){

        //get their current region
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            // If the location cannot be grabbed from GPS we grab it from network
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        //still null? are you an emu? check db for recorded lcoation
        if(location==null){
            //used db location even if empty, wont show anything on lcoal leaderboard if empty
            UserCR.document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    my_region = task.getResult().getString("location");
                }
            });

            //for emus set region to UofA Botanical or anywhere you want
            //my_region = getDefaultRegion();

        }else{// can update location in db and use most recent location
            Geocoder PostalCodeFinder= new Geocoder(getContext());
            try {// try to get current region
                String ANANAN = PostalCodeFinder.getFromLocation(location.getLatitude(),location.getLongitude(),1).get(0).getPostalCode();
                int start = 0, end = 3; // want ANA
                my_region = ANANAN.substring(start,end);
                Log.d("REGION", "User is in region "+my_region);

                //update  location
                HashMap<String,Object>region = new HashMap<>();
                region.put("location",my_region);
                UserCR.document(userID).set(region, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("REGION","Updated user region in database");
                    }
                });


            } catch (IOException e) { //cant get a region, use whatever is in db
                e.printStackTrace();
                Log.d("REGION-Rank","No Postal Code in Location");
                //just set to empty
                my_region="";
            }
        }


    }

    /**
     * This function returns a default region to use for testing location dependent functionality with emulators
     * @return String
     */
    private String getDefaultRegion() {
        Location location = getDefaultLocation();

        String region = "";
        Geocoder PostalCodeFinder = new Geocoder(getContext());
        try {// try to get current region
            String ANANAN = PostalCodeFinder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0).getPostalCode();
            int start = 0, end = 3; // want ANA
            region = ANANAN.substring(start, end);
            Log.d("REGION-SignUp", "User is in region " + my_region);
        } catch (IOException e) { //cant get a region, use whatever is in db
            Log.d("REGION-SignUp", "no location");
            e.printStackTrace();
            //update  location
        }

        return region;

    }

    /**
     * This function returns a default Location to use for testing location dependent functionality with emulators
     * @return Location
     */
    private Location getDefaultLocation() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(53.4080);//somewhere near u of a hopefully
        location.setLongitude(-113.7605);
        return location;
    }

    /**The onCreateView method sets the functionality for critical view parameters
     *  for the RankFragment such as the display and interface of the Spinner
     *  and its adapter. The view gives the user the ability to select the ranking they
     *  choose with the spinner. The view returned once set.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);


        View view = inflater.inflate(R.layout.fragment_rank, container, false);

        RANK_SPINNER = view.findViewById(R.id.spinnerRank);

        //SET SPINNER
        // Create an ArrayAdapter using the string array and a default spinner layout
        RankSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.rank_criteria, R.layout.rankspinner_item);
        // Specify the layout to use when the list of choices appears
        RankSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        RANK_SPINNER.setAdapter(RankSpinnerAdapter);


        RANK_SPINNER.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //will use kth largest instead of sorting to get ranks
                switch (position){
                    case 0: //Score
                        Log.d("RANK", "GLOBAL RANK(SCORE)");

                        //order top 20 by Highest QRcodes in whole app
                        //1. get the highest QRCodes of all players
                        Score_Or_Local = new ArrayList<>();
                        topTriples = new ArrayList<>();


                        UserCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                //String userID;
                                if(task.isSuccessful()){
                                    int P = task.getResult().size(); //used to make querying quicker

                                    Log.d("RANK2",P + "docs");
                                    for (QueryDocumentSnapshot userDoc: task.getResult()) {//GO OVER USERS
                                        Score_Or_Local.clear(); //remove whatever is there
                                        userID = userDoc.getId();

                                        QRCodeCR.whereArrayContains("playersScanned", userID).get()

                                                // STARTS TO BE DIFFERENT
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        int QRcPoints;
                                                        int highest = 0;
                                                        String highestFace = "----";
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot qrcDoc : task.getResult()) {//GO OVER USER'S CODES, GET HIGHEST
                                                                QRcPoints = qrcDoc.getLong("Points").intValue();
                                                                if (highest <= QRcPoints) {
                                                                    highest = QRcPoints;
                                                                    highestFace = (String) qrcDoc.get("icon");
                                                                }
                                                            }
                                                        } else {
                                                            Log.d("RANK2", "Failed to get QRCodes");
                                                        }


                                                        //ADD USER HIGHEST CODE TO LIST, also track the null users so can know when to get top 10
                                                        if(userDoc.getString("username")!=null) {
                                                            Score_Or_Local.add(new RankTriple(userDoc.getString("username"), highestFace, highest));
                                                        }else{
                                                            null_users++;
                                                        }
                                                        //Log.d("RANK", userDoc.getString("username") + " " + highest);
                                                        int N_Players = Score_Or_Local.size();
                                                        Log.d("RANK2",N_Players+" "+null_users);


                                                        //ONLY GET TOP 10 WHEN ALL PLayers's highest is gotten
                                                        if(N_Players+null_users==P){

                                                            //2)
                                                            //top 10
                                                            for(int i=1; i<=X && i<=N_Players; i++){
                                                                topTriples.add(kthLargestTriple(Score_Or_Local,i));
                                                                Log.d("RANK2", topTriples.get(i-1).PlayerName + " " + topTriples.get(i-1).QRcPoints);
                                                            }

                                                            //3)
                                                            //Bundle em up
                                                            Bundle RankBundle = new Bundle();
                                                            RankBundle.putSerializable(RankBundleKey, topTriples);

                                                            //4)
                                                            //make new RankScoreFragment with data
                                                            Fragment selected = new RankScoreFragment();
                                                            selected.setArguments(RankBundle);


                                                            //5)
                                                            //show it
                                                            FragmentActivity activity = getActivity();
                                                            if(activity!=null) { //prevent crash
                                                                Log.d("RANK2","Showing Main Leaderboard");
                                                                activity.getSupportFragmentManager()
                                                                        .beginTransaction()
                                                                        .replace(R.id.rankframe, selected).commit();//SHOW FRAGMENT
                                                            }else{
                                                                Log.d("RANK2","Ended Rank Fragment there null error");
                                                            }
                                                        }


                                                    }
                                                });


                                    }

                                }else{
                                    Log.d("RANK","Failed to get Users");
                                }

                            }

                            //int N_Players = Score_Or_Local.size();

                        });
                        //mapped each player to their highest qrc




                        null_users=0; //reset nulls
                        break;
                    case 1: //Sum
                        Log.d("RANK", "GLOBAL RANK(SUM)");

                        //order top 10 by Sum of QRCodes
                        //1. get the highest QRCodes of all players
                        Sum_Or_Count = new ArrayList<>();
                        topPairs = new ArrayList<>();


                        UserCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                   //String userID;
                                   if (task.isSuccessful()) {
                                       int P = task.getResult().size(); //used to make querying quicker

                                       Log.d("RANK3", P + "docs");
                                       for (QueryDocumentSnapshot userDoc : task.getResult()) {//GO OVER USERS
                                           Sum_Or_Count.clear(); //remove whatever is there
                                           userID = userDoc.getId();

                                           QRCodeCR.whereArrayContains("playersScanned", userID).get()

                                                   .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                                       @Override
                                                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                           int QRCSum = 0;
                                                           if (task.isSuccessful()) {
                                                               for (QueryDocumentSnapshot qrcDoc : task.getResult()) {//GO OVER USER'S CODES, GET SUM OF SCANS
                                                                   QRCSum += qrcDoc.getLong("Points").intValue();
                                                               }
                                                           } else {
                                                               Log.d("RANK3", "Failed to get QRCodes");
                                                           }

                                                           //ADD USER SCORE SUM TO LIST, also track the null users so can know when to get top 10
                                                           if (userDoc.getString("username") != null) {
                                                               Sum_Or_Count.add(new RankPair(userDoc.getString("username"), QRCSum));
                                                           } else {
                                                               null_users++;
                                                           }

                                                           //Log.d("RANK", userDoc.getString("username") + " " + highest);
                                                           int N_Players = Sum_Or_Count.size();
                                                           Log.d("RANK3", String.valueOf(N_Players));


                                                           //ONLY GET TOP 10 WHEN ALL PLayers's highest is gotten
                                                           if (N_Players + null_users == P) {

                                                               //2)
                                                               //top 10
                                                               for (int i = 1; i <= X && i <= N_Players; i++){
                                                                     topPairs.add(kthLargestPair(Sum_Or_Count, i));
                                                                   Log.d("RANK3", topPairs.get(i - 1).PlayerName + " " + topPairs.get(i - 1).Number);
                                                               }

                                                               //3)
                                                               //Bundle em up
                                                               Bundle RankBundle = new Bundle();
                                                               RankBundle.putSerializable(RankBundleKey, topPairs);

                                                               //4)
                                                               //make new RankSumFragment with data
                                                               Fragment selected = new RankSumFragment();
                                                               selected.setArguments(RankBundle);

                                                               //5)
                                                               //show it
                                                               FragmentActivity activity = getActivity();
                                                               if(activity!=null) { //prevent crash
                                                                   Log.d("RANK3","Showing Sum Leaderboard");
                                                                   activity.getSupportFragmentManager()
                                                                           .beginTransaction()
                                                                           .replace(R.id.rankframe, selected).commit();//SHOW FRAGMENT
                                                               }else{
                                                                   Log.d("RANK3","Ended Rank Fragment there null error");
                                                               }
                                                           }


                                                       }
                                                   });
                                       }
                                   } else {
                                       Log.d("RANK", "Failed to get Users");
                                   }
                               }
                           });


                        null_users=0; //reset nulls
                        break;
                    case 2: //Count
                        Log.d("RANK", "GLOBAL RANK(COUNT)");

                        //order top 10 by Count of QRCodes
                        //1. get the highest QRCodes of all players
                        Sum_Or_Count = new ArrayList<>();
                        topPairs = new ArrayList<>();


                        UserCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                //String userID;
                                if (task.isSuccessful()) {
                                    int P = task.getResult().size(); //used to make querying quicker

                                    Log.d("RANK4", P + "docs");
                                    for (QueryDocumentSnapshot userDoc : task.getResult()) {//GO OVER USERS
                                        Sum_Or_Count.clear(); //remove whatever is there
                                        userID = userDoc.getId();

                                        QRCodeCR.whereArrayContains("playersScanned", userID).get()

                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        int QRCCount = 0;
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot qrcDoc : task.getResult()) {//GO OVER USER'S CODES, GET COUNT OF SCANS
                                                                if (qrcDoc.getLong("Points").intValue() >= 0) {
                                                                    QRCCount++;
                                                                }
                                                            }
                                                        } else {
                                                            Log.d("RANK4", "Failed to get QRCodes");
                                                        }

                                                        //ADD USER CODE COUNT TO LIST, also track the null users so can know when to get top 10
                                                        if (userDoc.getString("username") != null) {
                                                            Sum_Or_Count.add(new RankPair(userDoc.getString("username"), QRCCount));
                                                        } else {
                                                            null_users++;
                                                        }

                                                        //Log.d("RANK", userDoc.getString("username") + " " + highest);
                                                        int N_Players = Sum_Or_Count.size();
                                                        Log.d("RANK4", String.valueOf(N_Players));


                                                        //ONLY GET TOP 10 WHEN ALL PLayers's highest is gotten
                                                        if (N_Players + null_users == P) {

                                                            //2)
                                                            //top 10
                                                            for (int i = 1; i <= X && i <= N_Players; i++){
                                                                topPairs.add(kthLargestPair(Sum_Or_Count, i));
                                                                Log.d("RANK4", topPairs.get(i - 1).PlayerName + " " + topPairs.get(i - 1).Number);
                                                            }

                                                            //3)
                                                            //Bundle em up
                                                            Bundle RankBundle = new Bundle();
                                                            RankBundle.putSerializable(RankBundleKey, topPairs);

                                                            //4)
                                                            //make new RankCountFragment with data
                                                            Fragment selected = new RankCountFragment();
                                                            selected.setArguments(RankBundle);

                                                            //5)
                                                            //show it
                                                            FragmentActivity activity = getActivity();
                                                            if(activity!=null) { //prevent crash
                                                                Log.d("RANK4","Showing Count Leaderboard");
                                                                activity.getSupportFragmentManager()
                                                                        .beginTransaction()
                                                                        .replace(R.id.rankframe, selected).commit();//SHOW FRAGMENT
                                                            }else{
                                                                Log.d("RANK4","Ended Rank Fragment there null error");
                                                            }
                                                        }


                                                    }
                                                });
                                    }
                                } else {
                                    Log.d("RANK", "Failed to get Users");
                                }
                            }
                        });


                        null_users=0; //reset nulls
                        break;
                    case 3: //Local
                        Log.d("RANK", "LOCAL RANK(SCORE)");
                        Log.d("REGION","Current Region: "+my_region);

                        //order top 10 by Highest QRCodes locally(ANA of Postal code)
                        //1. get the highest QRCodes of all players
                        Score_Or_Local = new ArrayList<>();
                        topTriples = new ArrayList<>();

                        if(Objects.equals(my_region, "")){
                            //try getting location and region
                            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                        }

                        //still an empty location? Blank Local leaderboard

                        if(Objects.equals(my_region, "")){
                            Log.d("RANK5","Showing BLANK Local Leaderboard");
                            Toast.makeText(getContext(), "No Location Found", Toast.LENGTH_LONG).show();
                            //SHOW AN EMPTY BOARD
                            //3)
                            //Bundle em up
                            Bundle RankBundle = new Bundle();
                            RankBundle.putSerializable(RankBundleKey, topTriples);

                            //4)
                            //make new RankLocalFragment with data
                            Fragment selected = new RankLocalFragment();
                            selected.setArguments(RankBundle);

                            //5)
                            //show it
                            FragmentActivity activity = getActivity();
                            if(activity!=null) { //prevent crash
                                Log.d("RANK5","Showing Local Leaderboard");
                                activity.getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.rankframe, selected).commit();//SHOW FRAGMENT
                            }else{
                                Log.d("RANK5","Ended Rank Fragment there null error");
                            }
                            break;
                        }




                        //get all players with the same region as me
                        UserCR.whereEqualTo("location",my_region).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                String userID;
                                if(task.isSuccessful()){
                                    int P = task.getResult().size(); //used to make querying quicker

                                    Log.d("RANK5",P + "docs");
                                    for (QueryDocumentSnapshot userDoc: task.getResult()) {//GO OVER USERS
                                        Score_Or_Local.clear();
                                        userID = userDoc.getId();

                                        QRCodeCR.whereArrayContains("playersScanned", userID).get()

                                                // STARTS TO BE DIFFERENT
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        int QRcPoints;
                                                        int highest = 0;
                                                        String highestFace = "----";
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot qrcDoc : task.getResult()) {//GO OVER USER'S CODES, GET THEIR HIGHEST
                                                                QRcPoints = qrcDoc.getLong("Points").intValue();
                                                                if (highest <= QRcPoints) {
                                                                    highest = QRcPoints;
                                                                    highestFace = (String) qrcDoc.get("icon");
                                                                }
                                                            }
                                                        } else {
                                                            Log.d("RANK5", "Failed to get QRCodes");
                                                        }


                                                        //IF THEY ARE NOT A NULL USER, ADD THEM TO THE LIST, OTHERWISE TRACK NULLs to know when we can get top 10
                                                        if(userDoc.getString("username")!=null) {
                                                            Score_Or_Local.add(new RankTriple(userDoc.getString("username"), highestFace, highest));
                                                        }else{
                                                            null_users++;
                                                        }

                                                        //Log.d("RANK", userDoc.getString("username") + " " + highest);
                                                        int N_Players = Score_Or_Local.size();
                                                        Log.d("RANK5",N_Players+ " " +null_users);



                                                        //GET TOP 10 ONLY WHEN GONE OVER ALL PLAYERS
                                                        if(N_Players+null_users==P){

                                                            //2) top X=10
                                                            for(int i=1; i<=X && i<=N_Players; i++){
                                                                topTriples.add(kthLargestTriple(Score_Or_Local,i));
                                                                Log.d("RANK5", topTriples.get(i-1).PlayerName + " " + topTriples.get(i-1).QRcPoints);

                                                            }

                                                            //3)
                                                            //Bundle em up
                                                            Bundle RankBundle = new Bundle();
                                                            RankBundle.putSerializable(RankBundleKey, topTriples);

                                                            //4)
                                                            //make new RankLocalFragment with data
                                                            Fragment selected = new RankLocalFragment();
                                                            selected.setArguments(RankBundle);

                                                            //5)
                                                            //show it
                                                            FragmentActivity activity = getActivity();
                                                            if(activity!=null) { //prevent crash
                                                                Log.d("RANK5","Showing Local Leaderboard");
                                                                activity.getSupportFragmentManager()
                                                                        .beginTransaction()
                                                                        .replace(R.id.rankframe, selected).commit();//SHOW FRAGMENT
                                                            }else{
                                                                Log.d("RANK5","Ended Rank Fragment there null error");
                                                            }
                                                        }


                                                    }
                                                });


                                    }

                                }else{
                                    Log.d("RANK5","Failed to get Users");
                                }
                            }

                        });


                        null_users=0;//reset nulls
                        break;
                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }


    // to find the kth largest elem in an Arralist of Player hash string to QRCode pairs
    //eg kthLargest(player_pairs, 1) returns the Player hash string and QRCode pair with
    // the highest QRCode score in player_pairs

    /**
     * This function returns the kth largest RankTriple in an arraylist of ranktriples
     * @param arr
     * @param k
     * @return RankTriple
     */
    public RankTriple kthLargestTriple(ArrayList<RankTriple> arr, int k) {
        //O(n)linear time + works for unsorted ordered containers
        int left = 0;
        int right = arr.size() - 1;

        while (left <= right) {
            int pivotIndex = partitionTriple(arr, left, right);

            if (pivotIndex == k - 1) {
                return arr.get(pivotIndex);
            } else if (pivotIndex < k - 1) {
                left = pivotIndex + 1;
            } else {
                right = pivotIndex - 1;
            }
        }
        return null; // kth largest not found, default to null
    }



    //helper funct for kthLargest. Partitioning based on Quicksort
    /**
     * This function is a helper function that partitions
     * a given array to get the top X players for RankTriples
     * @param arr
     * @param left
     * @param right
     * @return int
     */
    private static int partitionTriple(ArrayList<RankTriple> arr, int left, int right) {
        RankTriple pivot = arr.get(right);
        int i = left - 1;

        for (int j = left; j < right; j++) {
            if (arr.get(j).QRcPoints >= pivot.QRcPoints) {
                i++;
                Collections.swap(arr, i, j);
            }
        }

        Collections.swap(arr, i + 1, right);

        return i + 1;
    }


    /**
     * This function returns the kth largest RankPair in an arraylist of rankpairs
     * @param arr
     * @param k
     * @return RankPair
     */
    public RankPair kthLargestPair(ArrayList<RankPair> arr, int k) {
        //O(n)linear time + works for unsorted ordered containers
        int left = 0;
        int right = arr.size() - 1;

        while (left <= right) {
            int pivotIndex = pairPartition(arr, left, right);

            if (pivotIndex == k - 1) {
                return arr.get(pivotIndex);
            } else if (pivotIndex < k - 1) {
                left = pivotIndex + 1;
            } else {
                right = pivotIndex - 1;
            }
        }
        return null; // kth largest not found, default to null
    }


    //helper funct for kthLargest. Partitioning based on Quicksort
    /**
     * This function is a helper function that partitions
     * a given array to get the top X players for RankPairs
     * @param arr
     * @param left
     * @param right
     * @return int
     */
    private static int pairPartition(ArrayList<RankPair> arr, int left, int right) {
        RankPair pivot = arr.get(right);
        int i = left - 1;

        for (int j = left; j < right; j++) {
            if (arr.get(j).Number >= pivot.Number) {
                i++;
                Collections.swap(arr, i, j);
            }
        }

        Collections.swap(arr, i + 1, right);

        return i + 1;
    }
}


/*CITATIONS

1)Spinner making
https://developer.android.com/develop/ui/views/components/spinner

2)ChatGTP Coding kthLargest
https://openai.com/blog/chatgpt

3)ChatGTP Coding partition fro kth Largest
https://openai.com/blog/chatgpt

4)kth largest theory
Introduction To Algorithms, 3rd Edition.
 Chapter 9, 9.3: Selection in Worst-Case Linear Time.
 Chapter 7 Quicksort, Chapter 8 Sorting in Linear Time.

5)updating a firebase database
https://firebase.google.com/docs/firestore/manage-data/add-data

6)Region system using postal codes
https://beginnersbook.com/2013/12/java-string-substring-method-example/


 */
