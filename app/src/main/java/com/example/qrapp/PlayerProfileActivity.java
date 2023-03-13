package com.example.qrapp;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qrapp.QRCode;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Activity class to display searched Player profiles from SearchFragment.java
 */
public class PlayerProfileActivity extends AppCompatActivity {


    public PlayerProfileActivity() {

    }


    @Override
    /**
     * OnCreate method, init all fields
     * @param savedInstanceState this is used to pass a username from PlayerListAdapter so we can query the db for all player
     * information
     * @return void
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_user);
        Bundle bundle = getIntent().getExtras();
        // greatest hack ever known in existence
        // passed the string username instead of the object and we will Use this to query.
        // cause the bundle just wouldn't allow the object to be passed for no logical reason..
        String username = "filler";
        if(bundle != null) {
            username = bundle.getString("player");
        }
        TextView profileHeader = findViewById(R.id.profile);
        TextView usernameText = findViewById(R.id.username);
        TextView emailText = findViewById(R.id.email);
        String profile = username + "'s Profile";
        profileHeader.setText(profile);
        // Set player data, init db
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String finalUsername = username;
        db.collection("Users").whereEqualTo("Username", username).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                String email = document.getString("Email");
                usernameText.setText(finalUsername);
                emailText.setText(email);
            }
            //TODO stats
        });

    }
}
