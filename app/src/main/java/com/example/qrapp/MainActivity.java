package com.example.qrapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    BottomNavigationView nav_bar;

    ImageButton myProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nav_bar = findViewById(R.id.nav_barview);
        nav_bar.setOnItemSelectedListener(navbar_listener);

        myProfileButton = findViewById(R.id.myp);

        // start at menu
        nav_bar.setSelectedItemId(R.id.main_tab);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, new MainFragment())
                .commit();

        myProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, new MyProfileFragment())
                        .commit();
            }
        });

        }




    NavigationBarView.OnItemSelectedListener navbar_listener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            return false;


            Fragment selected = null;


            switch (item.getItemId()){
                case R.id.leaderboard_tab:
                    selected = new RankFragment();
                    break;
                case R.id.main_tab:
                    selected = new MainFragment();
                    break;
                case R.id.map_tab:
                    selected = new MapFragment();
                    break;
                case R.id.search_tab:
                    selected = new SearchFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, selected).commit();

            return true;
        }

    };


}