package com.example.qrapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyProfile extends AppCompatActivity {


    TextInputEditText usernameText;
    TextInputEditText emailText;

    ImageButton backButton;

    String userID;

    String username;

    String email;
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

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        // get the user data from the database
        db.collection("Users").document(userID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    //set the attributes
                    username = document.getString("username");
                    email = document.getString("email");

                    //set the text views
                    usernameText.setText(username);
                    emailText.setText(email);
                } else {
                    Toast.makeText(MyProfile.this, "Error getting user data", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            } else {
                Toast.makeText(MyProfile.this, "Error getting user data", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        });

        //watch for the user updating their username
        usernameText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !usernameText.getText().toString().equals(username)){
                //update the username in the database
                db.collection("Users").document(userID).update("username", usernameText.getText().toString());
            }
        });

        // watch for the user updating their email
        emailText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !emailText.getText().toString().equals(email)){
                //update the email in the database
                db.collection("Users").document(userID).update("email", emailText.getText().toString());
            }
        });

        //close activity when back button is pressed
        backButton.setOnClickListener(v -> finish());

    }


}