package com.example.qrapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainFragment.Scrollable {

    BottomAppBar bottomAppBar; //solely to hide nav bar when scrolling

    BottomNavigationView nav_bar;//nav bar object
    ImageButton SCAN;// scan button object
    ImageButton MYPROFILE;// get to myprofile page
    ImageView BACK;// get back to main fragment from Leaderboard
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //bottom app bar
        bottomAppBar = findViewById(R.id.bottom_nav_bar);

        bottomAppBar.setHideOnScroll(true);


        //set profile button
        MYPROFILE = findViewById(R.id.button_MYprofile);
        //set scan button
        SCAN = findViewById(R.id.button_scan);
        //set back to main button
        BACK = findViewById(R.id.button_backtomain);
        BACK.setVisibility(View.GONE);//visible only in rank fragment

        //set nav_bar
        nav_bar = findViewById(R.id.nav_barview);
        nav_bar.setOnItemSelectedListener(navbar_listener);

        // start at menu
        nav_bar.setSelectedItemId(R.id.main_tab);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser == null) {
                    // User is not signed in, force them to sign up using Firebase
                    System.out.println("User is not signed in");
                    startActivity(new Intent(MainActivity.this, SignUpActivity.class));

                } else {
                    System.out.println("User is signed in");
                    currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser updatedUser = auth.getCurrentUser();
                                if (updatedUser == null) {
                                    // User account has been deleted
                                    System.out.println("User account has been deleted");
                                }
                            }
                        }
                    });
                }
            }
        });



        //SCAN BUTTON
        //This takes the player to the Scanning[and Picture] activity
        SCAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //NEW SCANNING ACTIVITY, might be easier to use an activity for this one
                Toast.makeText(MainActivity.this, "scanow", Toast.LENGTH_SHORT).show();
                Intent ScanIntent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(ScanIntent);
            }
        });



        // start at menu tab when created
        nav_bar.setSelectedItemId(R.id.main_tab);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, new MainFragment())
                .commit();

        MYPROFILE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //start myprofile activity
                Intent myprofileIntent = new Intent(MainActivity.this, MyProfile.class);
                startActivity(myprofileIntent);
            }
        });

        //go back to main from rank
        BACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomAppBar.setVisibility(View.VISIBLE);
                SCAN.setVisibility(View.VISIBLE);
                BACK.setVisibility(View.GONE);
                nav_bar.setSelectedItemId(R.id.main_tab);
            }
        });

        }









    //THE NAVIGATION BAR items
    NavigationBarView.OnItemSelectedListener navbar_listener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            return false;


            Fragment selected = new MainFragment();
            //Fragment selected = null;
            // not really necessary to initially set to anything but places emphasis that the app loads to main fragment


            /*Overview:
            Code works by creating a new, respective frament when each of the items are clicked
            The clicked fragment is commited to the "frame" of the Main Activity->see activity_main.xml for frame
            There is always at least and only 1 fragment chosen at any given time.
            This is the fragment that is commited
             */


            switch (item.getItemId()){
                case R.id.leaderboard_tab:
                    bottomAppBar.setVisibility(View.INVISIBLE);
                    SCAN.setVisibility(View.INVISIBLE);
                    BACK.setVisibility(View.VISIBLE);
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
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame, selected).commit();//SHOW FRAGMENT

            return true;
        }

    };

    //hide bottom nva bar when scrolling sown
    @Override
    public void Scrollable(int scrollState) {
        Log.d("INTERFACE",String.format("I got %d",scrollState));
        if(scrollState==2){
            bottomAppBar.setVisibility(View.INVISIBLE);
        }else{
            bottomAppBar.setVisibility(View.VISIBLE);
        }
    }


}