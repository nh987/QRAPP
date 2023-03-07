package com.example.qrapp;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;


public class SearchFragment extends Fragment {
    Boolean playerFilterButtonClicked = false;
    Boolean QrFilterButtonClicked = false;
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.search, null);
        Button playerSearch = (Button) view.findViewById(R.id.button);
        Button QRSearch = (Button) view.findViewById(R.id.button2);
        SearchView searchView = (SearchView) view.findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // you actually have to click on the magnifying glass..
                // TODO, hook in database now
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        playerSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerFilterButtonClicked = true;
                QrFilterButtonClicked = false;
                // change Qr color to standard
                // change player color to coloured
            }
        });

        QRSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QrFilterButtonClicked = true;
                playerFilterButtonClicked = false;
                // change Qr color to coloured
                // change player color to standard


            }
        });

        return view;
    }

}
