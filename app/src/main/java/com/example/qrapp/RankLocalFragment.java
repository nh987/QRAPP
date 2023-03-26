package com.example.qrapp;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

//to display a leaderboard by highest qrcode scanned by top players locally
/**
 * This is a class that extends the Fragment class. This "RankLocalFragment" class presents
 * and displays the data from the RankFragment for the Local highest QRCode leaderboard
 */
public class RankLocalFragment extends Fragment {

    String RankBundleKey = "RB";
    ArrayList<RankTriple>topScorers;

    ListView topLocalListView;
    RankTripleAdapter topLocalAdapter;


    /**
     * The onCreate method gets the data to be displayed from the RankFragment
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topScorers = (ArrayList<RankTriple>)getArguments().getSerializable(RankBundleKey);
        /*
        topScorers = new ArrayList<>();
        topScorers.add(new RankTriple("dan", "myface",90L));
         */
    }

    /**
     * The onCreateView method sets all the critical items in the view and returns the view for
     * the local highest QRCode leaderboard
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_ranklocal, container, false);

        //get the listview in the fragment ranscore layout
        topLocalListView = view.findViewById(R.id.listview_ranklocal);

        //make an adapter for listview
        topLocalAdapter = new RankTripleAdapter(topScorers,getContext());

        //set listviews adapter
        topLocalListView.setAdapter(topLocalAdapter);

        //see the top player profile
        topLocalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("RANK5","Who is this guy?");

                //make intent
                Activity currentActivity = getActivity();
                Intent PlayerProfileIntent = new Intent(currentActivity, PlayerProfileActivity.class);

                //add player info data to intent
                RankTriple top_player = (RankTriple) topLocalAdapter.getItem(position);
                PlayerProfileIntent.putExtra("player",top_player.PlayerName);

                //show profile
                currentActivity.startActivity(PlayerProfileIntent);
            }
        });

        //not need since leaderboard is static
        //topScoreAdapter.notifyDataSetChanged();
        return view;

    }
}
