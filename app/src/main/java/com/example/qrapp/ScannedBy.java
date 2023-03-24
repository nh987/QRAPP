package com.example.qrapp;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

// SOURCES:
// 1.)
// URL: https://stackoverflow.com/questions/52046182/arraylist-only-updated-inside-oncompletelistener-and-not-outside-it
// AUTHOR: https://stackoverflow.com/users/5221149/andreas
// SITE: https://stackoverflow.com/
/**
 * Activity for displaying ListView of players that have scanned a particular QR Code. Can click to view players' profiles. Uses playerListAdapter.
 * Gets arrayList of userIDs and gets their information from the database to send into the playerListAdapter to display in the ListView.
 */
public class ScannedBy extends AppCompatActivity {
    private ImageButton backButton;
    private ListView listView;
    private PlayerListAdapter playerListAdapter;
    private ArrayList playersList;
    private ArrayList<Player> players = new ArrayList<Player>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        playersList = intent.getCharSequenceArrayListExtra("userIDs");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scannedby);

        listView = findViewById(R.id.scannedby_list);
        listView.setAdapter(playerListAdapter);
        backButton = findViewById(R.id.scannedby_back);

        backButton.setOnClickListener(new View.OnClickListener() { //  Return to MainFeed
            @Override
            public void onClick(View view) {finish();}
        });

        Log.d("TAG", "list size: " + playersList.size());
        Log.d("TAG", "userIDs: " + playersList);
        for (Object userID : playersList) { // Get info of each user that scanned qr and put into Player object
            DocumentReference userRef = db.collection("Users").document((String) userID);
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot userDoc = task.getResult();
                        if (userDoc.exists()) {
                            String username = userDoc.getString("username");
                            String email = userDoc.getString("email");
                            String phoneNumber = userDoc.getString("phoneNumber");
                            String location = userDoc.getString("location");
                            Log.d("TAG", "docItems: "+ username + email + phoneNumber + location);
                            Player newPlayer = new Player(username, email, location, phoneNumber);
                            players.add(newPlayer);
                            Log.d("TAG", "current players count: " + players.size());

                        }
                        else {
                            Log.d("TAG", "Document does not exist.");
                        }
                    }
                    else {
                        Log.d("TAG", "get failed with " + task.getException());
                    }
                    playerListAdapter = new PlayerListAdapter(players, getApplicationContext(), ScannedBy.this); // Update array per document iterations with new Player bc listener requires this implementation
                    listView.setAdapter(playerListAdapter);
                    playerListAdapter.notifyDataSetChanged();
                }

            });
        }

    }
}


