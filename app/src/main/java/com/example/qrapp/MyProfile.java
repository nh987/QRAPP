package com.example.qrapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

/**
 * The MyProfile class is the activity that is used to display the user's profile
 *  and their stats, when called, it will get the user's UID from auth, then
 *  get the user's data from the database, then get the QRCodes they have scanned
 *  and display the stats & information based on that data.
 *  It also has a button to view the highest and lowest QRCodes scanned by the user, which
 *  will open a new activity "QRProfile", passing the QRCode object to it.
 *  it displays the user's username, email, highest and lowest QRCodes scanned, total score,
 *  and total number of QRCodes scanned. It has buttons to view the highest and lowest QRCodes, and
 *  a back button to return to the previous activity.
 */
public class MyProfile extends AppCompatActivity {


    private TextView usernameText;
    private TextView emailText;
    private TextView highestQRCvalue;
    private TextView lowestQRCvalue;
    private TextView totalscoreValue;
    private TextView codesScannedValue;

    private ImageButton backButton;
    private ImageButton viewHighestQRCButton;
    private ImageButton viewLowestQRCButton;

    private String userID;

    private String username;

    private String email;

    private ArrayList<QRCode> QRCodeList;

    private FirebaseFirestore db;
    //TODO Stats section

    /**
     * the onCreate method finds all the views and sets the onclick listeners,
     *  as well as establishing the database connection, then calls updateUserInfo()
     *  and getQRCodes() in order to get the user data and the QRCodes they have scanned
     * @param savedInstanceState the saved instance state of the activity, not used except to call the super method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_user);

        //get the views
        usernameText = findViewById(R.id.username);
        emailText = findViewById(R.id.email);
        backButton = findViewById(R.id.back);
        highestQRCvalue = findViewById(R.id.highestQRCvalue);
        lowestQRCvalue = findViewById(R.id.lowestQRCvalue);
        totalscoreValue = findViewById(R.id.totalscoreValue);
        codesScannedValue = findViewById(R.id.codesScannedValue);
        viewHighestQRCButton = findViewById(R.id.viewHighestQRCButton);
        viewLowestQRCButton = findViewById(R.id.viewLowestQRCButton);

        db = FirebaseFirestore.getInstance();

        // if you don't log in this will error...
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        // get the user data from the database
        updateUserInfo();

        //get all QRCodes scanned by the user
        QRCodeList = new ArrayList<>();
        getQRCodes();


        //close activity when back button is pressed
        backButton.setOnClickListener(v -> finish());

        //set the view highest and lowest buttons to invisible and disabled until if and when the highest and lowest scores are found
        viewHighestQRCButton.setEnabled(false);
        viewHighestQRCButton.setVisibility(View.INVISIBLE);
        viewLowestQRCButton.setEnabled(false);
        viewLowestQRCButton.setVisibility(View.INVISIBLE);

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
                    String hashed = document.getString("Hash");

                    QRCode queriedQR = new QRCode(comments, points, name, icon, playersScanned, geolocation, hashed);
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

    /**
     * UpdateScores finds the highest, lowest and total scores of the user using the QRCodeList,
     * sets the text views to their corresponding values,
     * sets if the view highest and lowest buttons are enabled or not,
     * sets the buttons to open the QRProfile activity with the highest and lowest QRCode respectively
     */
    private void updateScores(){

        if (QRCodeList.size() == 0){
            //if the user has not scanned any QR codes, set the text views to null representing values
            highestQRCvalue.setText("N/A");
            lowestQRCvalue.setText("N/A");
            totalscoreValue.setText("0");
            codesScannedValue.setText("0");

            return;
        }

        //find the highest, lowest QR code and total scores
        QRCode highestQR = QRCodeList.get(0);
        QRCode lowestQR = QRCodeList.get(0);
        int total = 0;
        for (QRCode qrCode : QRCodeList) {
            int currentScore = Integer.parseInt(qrCode.getPoints());
            int highestScore = Integer.parseInt(highestQR.getPoints());
            int lowestScore = Integer.parseInt(lowestQR.getPoints());
            if (currentScore > highestScore){
                highestQR = qrCode;
            }
            if (currentScore < lowestScore){
                lowestQR = qrCode;
            }
            total += currentScore;
        }
        //set the text views
        highestQRCvalue.setText(String.valueOf(highestQR.getPoints()));
        lowestQRCvalue.setText(String.valueOf(lowestQR.getPoints()));
        totalscoreValue.setText(String.valueOf(total));
        codesScannedValue.setText(String.valueOf(QRCodeList.size()));

        // enable the buttons
        viewHighestQRCButton.setEnabled(true);
        viewHighestQRCButton.setVisibility(View.VISIBLE);
        viewLowestQRCButton.setEnabled(true);
        viewLowestQRCButton.setVisibility(View.VISIBLE);

        //set the buttons to open the QRProfile activity
        final QRCode finalLowestQR = lowestQR;
        final QRCode finalHighestQR = highestQR;
        viewHighestQRCButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfile.this, QRProfile.class);
            intent.putExtra("qr_code", finalHighestQR);
            startActivity(intent);
        });
        viewLowestQRCButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfile.this, QRProfile.class);
            intent.putExtra("qr_code", finalLowestQR);
            startActivity(intent);
        });
    }

    /**
     * UpdateUserInfo Gets the username and email of the user from the database,
     * and sets the text views to their corresponding values
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
