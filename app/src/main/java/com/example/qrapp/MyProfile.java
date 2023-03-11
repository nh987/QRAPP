package com.example.qrapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyProfile extends AppCompatActivity {


    TextInputEditText usernameText;
    TextInputEditText emailText;

    ImageButton backButton;

    //TODO Stats section

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_user);

        //get the text views
        usernameText = findViewById(R.id.username);
        emailText = findViewById(R.id.email);
        backButton = findViewById(R.id.back);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String deviceID = "DefaultDummyDevice"; //TODO this should be the device ID once we set up registration
        //query the database for the user with the device ID
        db.collection("Users").whereEqualTo("DeviceID", deviceID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //check that the query returned a result
                if(task.getResult().getDocuments().size() == 0){
                    //raise an error toast and return to the previous activity
                    Toast toast = Toast.makeText(getApplicationContext(), "Error: No user found", Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                    return;
                }
                //get the first document in the query
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                //set the text views to the user's info
                usernameText.setText(document.getString("Username"));
                emailText.setText(document.getString("Email"));
            } else {
                //raise an error toast and return to the previous activity
                Toast toast = Toast.makeText(getApplicationContext(), "Error: Could not connect to database", Toast.LENGTH_SHORT);
                toast.show();
                finish();
                return;
            }
        });

        //close activity when back button is pressed
        backButton.setOnClickListener(v -> {
            finish();
        });

    }


}