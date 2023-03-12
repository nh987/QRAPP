package com.example.qrapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class MyProfile extends AppCompatActivity {


    private TextInputEditText usernameText;
    private TextInputEditText emailText;
    private TextView highestQRCvalue;
    private TextView lowestQRCvalue;
    private TextView totalscoreValue;


    private ImageButton backButton;

    private String userID;

    private String username;

    private String email;

    private ArrayList<QRCode> QRCodeList;

    private FirebaseFirestore db;
    //TODO Stats section

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_user);

        //get the text views
        usernameText = findViewById(R.id.username);
        emailText = findViewById(R.id.email);
        backButton = findViewById(R.id.back);
        highestQRCvalue = findViewById(R.id.highestQRCvalue);
        lowestQRCvalue = findViewById(R.id.lowestQRCvalue);
        totalscoreValue = findViewById(R.id.totalscoreValue);

        db = FirebaseFirestore.getInstance();

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        // get the user data from the database
        updateUserInfo();

        //get all QRCodes scanned by the user
        QRCodeList = new ArrayList<>();
        getQRCodes();


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

    /**
     * Gets all the QRCodes that the user has scanned,
     * writes them to the QRCodeList
     * then calls updateScores() to update the stats
     */
    private void getQRCodes(){
        db.collection("QRCodes").whereArrayContains("playersScanned", userID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    Integer points = document.getLong("Points").intValue();
                    String name = document.getString("Name");
                    String icon = document.getString("icon");
                    Object playersScanned = document.get("playersScanned");
                    Object comments = document.get("Comments");
                    GeoPoint geolocation = document.getGeoPoint("Geolocation");

                    QRCode queriedQR = new QRCode(comments, points, name, icon, playersScanned, geolocation);
                    QRCodeList.add(queriedQR);
                }
                updateScores();
            } else {
                Toast.makeText(MyProfile.this, "Error getting user data", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        });
    }

    private void updateScores(){
        //print qr codes at time of calling to console
        System.out.println("QR Codes:");
        for (QRCode qrCode : QRCodeList) {
            System.out.println(qrCode.getName() + " " + qrCode.getPoints());
        }

        //find the highest, lowest and total scores
        int highest = 0;
        int lowest = 0;
        int total = 0;
        for (QRCode qrCode : QRCodeList) {
            int codePoints = Integer.parseInt(qrCode.getPoints());
            if (codePoints > highest) {
                highest = codePoints;
            }
            if (codePoints < lowest) {
                lowest = codePoints;
            }
            total += codePoints;
        }
        //set the text views
        highestQRCvalue.setText(String.valueOf(highest));
        lowestQRCvalue.setText(String.valueOf(lowest));
        totalscoreValue.setText(String.valueOf(total));
    }

    /**
     * Gets the username and email of the user from the database, sets the text views
     */
    private void updateUserInfo(){
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
    }


}