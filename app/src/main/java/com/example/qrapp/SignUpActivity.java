package com.example.qrapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    public FirebaseAuth auth;
    public FirebaseFirestore db;
    private CollectionReference UserCR; //reference to the players
    private String userID;

    private EditText emailField;
    private EditText passwordField;
    private EditText username;
    private Button signUpButton;


    //for location
    //used whenvever a new last location is requested
    private String my_region;
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d("CURRENT LOCATION Sign-Up", "Permission Granted");
                    updateRegion(); //have permission to use region postal code

                } else {
                    //Permission not granted
                    // Local Ranking will be unavailable or inaccurate
                    //IGNORED flow from here since assuming we are getting permission
                    Log.d("CURRENT LOCATION Sign-Up", "Permission Denied");
                }
            });

    /**
     * This method is called when the activity is created. The layout is set and the Firebase authentication and database are initialized.
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        auth = FirebaseAuth.getInstance();
        username = findViewById(R.id.username);
        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);
        signUpButton = findViewById(R.id.signup_button);
        db = FirebaseFirestore.getInstance();

        UserCR = db.collection("Users");
        //my_region=getDefaultRegion();
        my_region="";


        signUpButton.setOnClickListener(new View.OnClickListener() {
            /**
             * This method is called when the sign up button is clicked. The entered email, password, and username are retrieved. The username is checked to see if it already exists. If it does, a toast message is displayed. If it does not, a new user is created with the entered email and password.
             * @param v the view that was clicked
             */
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                String uname = username.getText().toString();
                UserCR
                        .whereEqualTo("username", uname)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            /**
                             * This method is called when the task is complete. If the username already exists, a toast message is displayed. If the username is unique, a new user is created with the entered email and password.
                             * @param task the completed task
                             */
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        Toast.makeText(SignUpActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    // Username is unique, create the new user
                                    auth.createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                                /**
                                                 * This method is called when the task is complete. If the task is successful, a new user is created in the database with the entered username, email, location, and phone number. If the task is unsuccessful, a toast message is displayed.
                                                 * @param task the completed task
                                                 */
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        Map<String, Object> newUser = new HashMap<>();
                                                        newUser.put("username", uname);
                                                        newUser.put("email", email);
                                                        //newUser.put("location",my_region);
                                                        newUser.put("phoneNumber", "");

                                                        // creates a new user in the database
                                                        userID = auth.getCurrentUser().getUid();
                                                        Log.d("REGION-SignUp", "BEFORE CHECK: User "+userID +" in "+my_region);
                                                        setRegion(); //if can get true location, get it
                                                        Log.d("REGION-SignUp", "AFTER CHECK: User "+userID +" in "+my_region);


                                                        //finally, add to db
                                                        newUser.put("location",my_region);
                                                        UserCR.document(userID).set(newUser);


                                                        Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {
                                                        Toast.makeText(SignUpActivity.this, "Sign up failed: " + task.getException().getMessage(),
                                                                Toast.LENGTH_SHORT).show();
                                                        System.out.println("Sign up failed: " + task.getException().getMessage());
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Error checking username: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                    System.out.println("Error checking username: " + task.getException().getMessage());
                                }
                            }
                        });
            }
        });
    }

    private void setRegion() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // requirement to check permission
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            //return;
        }else{
            updateRegion();
        }
    }

    @SuppressLint("MissingPermission")
    private void updateRegion() {

        Log.d("REGION","Updating Region...");
        //get their current region
        LocationManager locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            // If the location cannot be grabbed from GPS we grab it from network
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if(location!=null){//make changes, we have their true location
            Geocoder PostalCodeFinder= new Geocoder(this);
            try {// try to get current region
                String ANANAN = PostalCodeFinder.getFromLocation(location.getLatitude(),location.getLongitude(),1).get(0).getPostalCode();
                int start = 0, end = 3; // want ANA
                my_region = ANANAN.substring(start,end);
                Log.d("REGION", "CHANGED Region: User is in region "+my_region);


            } catch (IOException e) { //cant get a region, use whatever is in db
                Log.d("REGION","No Postal Code");
                e.printStackTrace();
                my_region = ""; //redundant
                //my_region = getDefaultRegion();
                //my_region = my_region; no change
            }

        }else{
            my_region="";//set to empty but redundant
            Log.d("REGION","Could not update region");
        }

    }


    private String getDefaultRegion() {
        Location location = getDefaultLocation();

        String region = "";
        Geocoder PostalCodeFinder = new Geocoder(this);
        try {// try to get current region
            String ANANAN = PostalCodeFinder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0).getPostalCode();
            int start = 0, end = 3; // want ANA
            region = ANANAN.substring(start, end);
            Log.d("REGION-SignUp", "User is in region " + my_region);
        } catch (IOException e) { //cant get a region, use whatever is in db
            Log.d("REGION-SignUp", "no location");
            e.printStackTrace();
            //update  location
        }

        return region;

    }
    private Location getDefaultLocation() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(53.4080);//somewhere near u of a hopefully
        location.setLongitude(-113.7605);
        return location;
    }


}
