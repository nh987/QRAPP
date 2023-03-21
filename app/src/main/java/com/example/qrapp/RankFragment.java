package com.example.qrapp;


import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RankFragment extends Fragment {


    Spinner RANK_SPINNER;
    ArrayAdapter<CharSequence> RankSpinnerAdapter;

    //Database
    FirebaseAuth Auth;
    FirebaseFirestore DB;

    //tops
    int X = 10; //only top 20
    ArrayList<RankPair> Sum_Or_Count_All;
    ArrayList<RankPair>topX_pair;
    ArrayList<RankTriple> Score_Or_Local_All;
    ArrayList<RankTriple> topX_triple;

    //holders
    QRCode highestQRc=null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Auth = FirebaseAuth.getInstance();
        DB = FirebaseFirestore.getInstance();





    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_rank, container, false);



        RANK_SPINNER = (Spinner) view.findViewById(R.id.spinnerRank);

        //SET SPINNER
        // Create an ArrayAdapter using the string array and a default spinner layout
        RankSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.rank_criteria, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        RankSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        RANK_SPINNER.setAdapter(RankSpinnerAdapter);


        RANK_SPINNER.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                switch (position){
                    case 0:


                        //order top 20 by Highest QRcodes in whole app
                        //1. get the highest QRCodes of all players
                        Score_Or_Local_All = new ArrayList<>();
                        topX_triple = new ArrayList<>();

                        DB.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                //going over each player to make a list of all players and their highest QRCode
                                for (QueryDocumentSnapshot userDoc:value) {
                                    String userID = userDoc.getId();


                                    //go over each QRCode to see which ones each player has and which is highest
                                    DB.collection("QRCodes").whereArrayContains("playersScanned",userID)
                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            Long highest = 0L;
                                            String highest_name = "----";
                                            Long QRcPoints;
                                            for (QueryDocumentSnapshot qrcDoc : value) {
                                                List<String> players = (List<String>) qrcDoc.get("playersScanned");
                                                QRcPoints = qrcDoc.getLong("Points");
                                                if (highest < QRcPoints) {
                                                    highest = QRcPoints;
                                                    highest_name = (String) qrcDoc.get("icon");
                                                }
                                            }




                                            Score_Or_Local_All.add(new RankTriple(userID, highest, highest_name));

                                            //2. put in an ordered list
                                            //3. get the top X
                                            for(int i=1;
                                                    i<=X &&
                                                    i<=Score_Or_Local_All.size(); i++){
                                                topX_triple.add(kthLargest(Score_Or_Local_All,i));
                                                Log.d("RANK",topX_triple.get(i-1).PlayerID + " " + topX_triple.get(i-1).Score);
                                            }


                                            //4. Bundle and out
                                            Fragment selected = new RankScoreFragment();

                                            //bundle
                                            Bundle RankBundle = new Bundle();
                                            String RankBundleKey = "RB";
                                            RankBundle.putSerializable(RankBundleKey,topX_triple);
                                            selected.setArguments(RankBundle);

                                            //show leaderboard
                                            getActivity().getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .replace(R.id.rankframe, selected).commit();//SHOW FRAGMENT


                                        }

                                    });

                                }
                            }
                        });
                        break;


                    case 1:
                        //selected = new RankSumFragment();

                        //order top 20 by Sum of QRCodes
                        //1. get the highest QRCodes of all players
                        Sum_Or_Count_All = new ArrayList<>();
                        topX_pair = new ArrayList<>();


                        //2. put in an ordered list

                        //3. get the top 20
                        break;
                    case 2:
                       // selected = new RankCountFragment();

                        //order top 20 by Count of QRCodes
                        //1. get the highest QRCodes of all players
                        Sum_Or_Count_All = new ArrayList<>();
                        topX_pair = new ArrayList<>();

                        //2. put in an ordered list

                        //3. get the top 20

                        break;
                    case 3:
                        //selected = new RankLocalFragment();

                        //order top 20 by Highest QRCodes locally(ANA of Postal code)
                        //1. get the highest QRCodes of all players
                        Score_Or_Local_All = new ArrayList<>();
                        topX_triple = new ArrayList<>();

                        //2. put in an ordered list

                        //3. get the top 20
                        break;
                }

//                getActivity().getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.rankframe, selected).commit();//SHOW FRAGMENT
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });






        return view;
    }


        // to find the kth largest elem in an Arralist of Player hash string to QRCode pairs
    //eg kthLargest(player_pairs, 1) returns the Player hash string and QRCode pair with the highest QRCode score in player_pairs
    public RankTriple kthLargest(ArrayList<RankTriple> arr, int k) {
        //O(n)linear time + works for unsorted ordered containers
        int left = 0;
        int right = arr.size() - 1;

        while (left <= right) {
            int pivotIndex = partition(arr, left, right);

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
    private static int partition(ArrayList<RankTriple> arr, int left, int right) {
        RankTriple pivot = arr.get(right);
        int i = left - 1;

        for (int j = left; j < right; j++) {
            if (arr.get(j).Score >= pivot.Score) {
                i++;
                Collections.swap(arr, i, j);
            }
        }

        Collections.swap(arr, i + 1, right);

        return i + 1;
    }





}
