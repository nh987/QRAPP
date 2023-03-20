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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

/**
 * ListView of players that have scanned a particular QR Code. Can click to view players' profiles.
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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {finish(); }
        });
        Log.d("TAG", "list size: " + playersList.size());
        Log.d("TAG", "userIDs: " + playersList);
        if (playersList.size() > 0) {
            for (int i = 0; i < playersList.size(); i++) { // get info of each user that scanned qr
                DocumentReference userRef = db.collection("Users").document(playersList.get(i).toString());
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
                    }
                });
            }
            Log.d("TAG", "final players count: " + players.size());
//            playerListAdapter = new PlayerListAdapter(players, this, ScannedBy.this);
//            listView.setAdapter(playerListAdapter);
//            playerListAdapter.notifyDataSetChanged();
        }
    }
}


