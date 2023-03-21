package com.example.qrapp;


import android.Manifest;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RankFragment extends Fragment {


    Spinner RANK_SPINNER;
    ArrayAdapter<CharSequence> RankSpinnerAdapter;

    //Database
    FirebaseAuth Auth;
    FirebaseFirestore DB;

    //tops
    ArrayList<RankPair> Sum_Or_Count;
    ArrayList<RankTriple> Score_Or_Local;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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
                Fragment selected = new RankScoreFragment();

                switch (position){
                    case 0:
                        selected = new RankScoreFragment();

                        //order top 20 by Highest QRcodes in whole app
                        //1. get the highest QRCodes of all players
                        Score_Or_Local = new ArrayList<>();
                        


                        //2. put in an ordered list

                        //3. get the top 20

                        break;
                    case 1:
                        selected = new RankSumFragment();

                        //order top 20 by Sum of QRCodes
                        //1. get the highest QRCodes of all players
                        Sum_Or_Count = new ArrayList<>();


                        //2. put in an ordered list

                        //3. get the top 20
                        break;
                    case 2:
                        selected = new RankCountFragment();

                        //order top 20 by Count of QRCodes
                        //1. get the highest QRCodes of all players
                        Sum_Or_Count = new ArrayList<>();


                        //2. put in an ordered list

                        //3. get the top 20

                        break;
                    case 3:
                        selected = new RankLocalFragment();

                        //order top 20 by Highest QRCodes locally(ANA of Postal code)
                        //1. get the highest QRCodes of all players
                        Score_Or_Local = new ArrayList<>();


                        //2. put in an ordered list

                        //3. get the top 20
                        break;
                }

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.rankframe, selected).commit();//SHOW FRAGMENT
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });






        return view;
    }
}
