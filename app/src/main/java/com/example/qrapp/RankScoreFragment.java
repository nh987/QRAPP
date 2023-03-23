package com.example.qrapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

//to display a leaderboard by highest qrcode scanned by top players
public class RankScoreFragment extends Fragment {

    String RankBundleKey = "RB";
    ArrayList<RankTriple>topScorers;

    ListView topScoreListView;
    RankTripleAdapter topScoreAdapter;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topScorers = (ArrayList<RankTriple>)getArguments().getSerializable(RankBundleKey);
        /*
        topScorers = new ArrayList<>();
        topScorers.add(new RankTriple("dan", "myface",90L));

         */
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_rankscore, container, false);

        //get the listview in the fragment ranscore layout
        topScoreListView = view.findViewById(R.id.listview_rankscore);

        //make an adapter for listview
        topScoreAdapter = new RankTripleAdapter(topScorers,getContext());

        //set listviews adapter
        topScoreListView.setAdapter(topScoreAdapter);

        //not need since leaderboard is static
        //topScoreAdapter.notifyDataSetChanged();
        return view;

    }
}
