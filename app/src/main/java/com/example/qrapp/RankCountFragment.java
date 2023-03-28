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

public class RankCountFragment extends Fragment {

    String RankBundleKey = "RB";
    ArrayList<RankPair> topCountScorers;

    ListView topScoreListView;
    RankPairAdapter topScoreAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topCountScorers = (ArrayList<RankPair>)getArguments().getSerializable(RankBundleKey);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rankcount, container, false);
        topScoreListView = view.findViewById(R.id.listview_rankcount);
        topScoreAdapter = new RankPairAdapter(topCountScorers,getContext());
        topScoreListView.setAdapter(topScoreAdapter);
        return view;
    }
}
