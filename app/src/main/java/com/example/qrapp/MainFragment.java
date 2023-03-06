package com.example.qrapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class MainFragment extends Fragment {

    private ListView mListView;
    private ArrayAdapter ListViewAdapter;
    private ArrayList<String> qrcs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        //
        //set attrs
        View contentView = inflater.inflate(R.layout.fragment_main,container,false);
        mListView = contentView.findViewById(R.id.item_listview);
        qrcs = new ArrayList<>();

        //Dummy items
        for(int i=20; i>=0; i--){
            qrcs.add("Another");
        }

        //link list AND listview to a listadapter
        mListView = contentView.findViewById(R.id.item_listview);
        ListViewAdapter = new ArrayAdapter<>(contentView.getContext(), R.layout.item_qrc, qrcs);
        mListView.setAdapter(ListViewAdapter);

        //notify adapter of list updates
        ListViewAdapter.notifyDataSetChanged();


        return contentView;
    }
}
