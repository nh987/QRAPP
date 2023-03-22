package com.example.qrapp;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankFragment extends Fragment {


    Spinner RANK_SPINNER;
    ArrayAdapter<CharSequence> RankSpinnerAdapter;

    //Database
    FirebaseAuth Auth;
    FirebaseFirestore DB;
    CollectionReference UserCR, QRCodeCR;

    //dataholders
    List<String> players;

    //tops
    int X = 10;
    ArrayList<RankPair> Sum_Or_Count;
    ArrayList<RankTriple> Score_Or_Local;

    ArrayList<RankPair> topPairs;
    ArrayList<RankTriple> topTriples;

    //passing data
    String RankBundleKey = "RB";



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Auth = FirebaseAuth.getInstance();
        DB = FirebaseFirestore.getInstance();
        UserCR = DB.collection("Users");
        QRCodeCR = DB.collection("QRCodes");


        players = new ArrayList<>();



    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);



        View view = inflater.inflate(R.layout.fragment_rank, container, false);
        for (String playrID:players) {
            Log.d("RANK",playrID + " is a player");
        }


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
                Fragment selected = new RankSumFragment();



                switch (position){
                    case 0: //Score
                        selected = new RankScoreFragment();

                        //order top 20 by Highest QRcodes in whole app
                        //1. get the highest QRCodes of all players
                        Score_Or_Local = new ArrayList<>();
                        topTriples = new ArrayList<>();


                        UserCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                String userID;
                                if(task.isSuccessful()){

                                    for (QueryDocumentSnapshot userDoc: task.getResult()) {//GO OVER USERS
                                        Score_Or_Local.clear();
                                        userID = userDoc.getId();

                                        QRCodeCR.whereArrayContains("playersScanned", userID).get()

                                                // STARTS TO BE DIFFERENT
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        Long QRcPoints;
                                                        Long highest = 0L;
                                                        String highestFace = "----";
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot qrcDoc : task.getResult()) {
                                                                QRcPoints = qrcDoc.getLong("Points");
                                                                if (highest <= QRcPoints) {
                                                                    highest = QRcPoints;
                                                                    highestFace = (String) qrcDoc.get("icon");
                                                                }
                                                            }
                                                        } else {
                                                            Log.d("RANK", "Failed to get QRCodes");
                                                        }


                                                        if(userDoc.getString("username")!=null)
                                                            Score_Or_Local.add(new RankTriple(userDoc.getString("username"), highestFace, highest));
                                                        //Log.d("RANK", userDoc.getString("username") + " " + highest);
                                                        int N_Players = Score_Or_Local.size();
                                                        Log.d("RANK2",String.valueOf(N_Players));
                                                        topTriples.clear();
                                                        for(int i=1; i<=X && i<=N_Players; i++){
                                                            topTriples.add(kthLargestTriple(Score_Or_Local,i));
                                                            Log.d("RANK2", topTriples.get(i-1).PlayerID + " " + topTriples.get(i-1).QRcPoints);

                                                        }
                                                        //Bundle em up
                                                        Bundle RankBundle = new Bundle();
                                                        RankBundle.putSerializable(RankBundleKey, topTriples);

                                                        //make new RankScoreFragment with data
                                                        Fragment selected = new RankScoreFragment();
                                                        selected.setArguments(RankBundle);

                                                        //show it
                                                        getActivity().getSupportFragmentManager()
                                                                .beginTransaction()
                                                                .replace(R.id.rankframe, selected).commit();//SHOW FRAGMENT

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






                        //2. put in an ordered list
                        //will use kth largest instead of sorting

                        //3. get the top 10
                        //Now, get the top 10 players!!


                        break;
                    case 1: //Sum
                        selected = new RankSumFragment();

                        //order top 20 by Sum of QRCodes
                        //1. get the highest QRCodes of all players
                        Sum_Or_Count = new ArrayList<>();





                        //2. put in an ordered list

                        //3. get the top 20
                        break;
                    case 2: //Count
                        selected = new RankCountFragment();

                        //order top 20 by Count of QRCodes
                        //1. get the highest QRCodes of all players
                        Sum_Or_Count = new ArrayList<>();


                        //2. put in an ordered list

                        //3. get the top 20

                        break;
                    case 3: //Local
                        selected = new RankLocalFragment();

                        //order top 20 by Highest QRCodes locally(ANA of Postal code)
                        //1. get the highest QRCodes of all players
                        Score_Or_Local = new ArrayList<>();


                        //2. put in an ordered list

                        //3. get the top 20
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
    //eg kthLargest(player_pairs, 1) returns the Player hash string and QRCode pair with the highest QRCode score in player_pairs
    public RankTriple kthLargestTriple(ArrayList<RankTriple> arr, int k) {
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
            if (arr.get(j).QRcPoints >= pivot.QRcPoints) {
                i++;
                Collections.swap(arr, i, j);
            }
        }

        Collections.swap(arr, i + 1, right);

        return i + 1;
    }
}
