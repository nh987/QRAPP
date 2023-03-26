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

//to display a leaderboard by highest sum of qrcode scores by top players
/**
 * This is a class that extends the Fragment class. This "RankSumFragment" class presents
 * and displays the data from the RankFragment for the Global highest QRCode score sum leaderboard
 */
public class RankSumFragment extends Fragment {

    String RankBundleKey = "RB";
    ArrayList<RankPair> topSumScorers;

    ListView topSumListView;
    RankPairAdapter topSumAdapter;


    /**
     * The onCreate method gets the data to be displayed from the RankFragment
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topSumScorers = (ArrayList<RankPair>)getArguments().getSerializable(RankBundleKey);

    }

    /**
     * The onCreateView method sets all the attributes needed to display the "Sums" leaderboard
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranksum, container, false);
        topSumListView = view.findViewById(R.id.listview_ranksum);
        topSumAdapter = new RankPairAdapter(topSumScorers,getContext());
        topSumListView.setAdapter(topSumAdapter);

        //see the top player profile
        topSumListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("RANK3","Who is this guy?");

                //make intent
                Activity currentActivity = getActivity();
                Intent PlayerProfileIntent = new Intent(currentActivity, PlayerProfileActivity.class);

                //add player info data to intent
                RankPair top_player = (RankPair) topSumAdapter.getItem(position);
                PlayerProfileIntent.putExtra("player",top_player.PlayerName);

                //show profile
                currentActivity.startActivity(PlayerProfileIntent);
            }
        });

        return view;
    }
}
