package com.example.qrapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RankFragment extends Fragment {


    Spinner RANK_SPINNER;
    ArrayAdapter<CharSequence> RankSpinnerAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_rank, container, false);

        //SET SPINNER
        RANK_SPINNER = (Spinner) view.findViewById(R.id.spinnerRank);
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
                //Fragment selected = null;
                // not really necessary to initially set to anything but places emphasis that the app loads to main fragment


            /*Overview:
            Code works by creating a new, respective frament when each of the items are clicked
            The clicked fragment is commited to the "frame" of the Main Activity->see activity_main.xml for frame
            There is always at least and only 1 fragment chosen at any given time.
            This is the fragment that is commited
             */


                switch (position){
                    case 0:
                        selected = new RankScoreFragment();
                        break;
                    case 1:
                        selected = new RankSumFragment();
                        break;
                    case 2:
                        selected = new RankCountFragment();
                        break;
                    case 3:
                        selected = new RankLocalFragment();
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
