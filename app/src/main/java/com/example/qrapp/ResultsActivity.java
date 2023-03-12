package com.example.qrapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import android.provider.Settings.Secure;
import android.Manifest;
import android.widget.Toast;

public class ResultsActivity extends AppCompatActivity {
    String hashed;
    long score;
    String name;
    String visual;
    Boolean includeGeolocation = false; // init false
    List<String> comments = new ArrayList<>();
    List<String> playersScanned = new ArrayList<>();
    Boolean hasScanned = false;
    Boolean doesExist = false;
    TextView textViewScore;
    TextView textViewVisual;
    TextView textViewName;
    CheckBox checkBox;
    Button addPhoto; // TODO: addPhotoFragment -> CameraX integration
    Image image; //  init as null

    Double lat;
    Double lon;
    Button continueToPost;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            hashed = extras.getString("hashed");
            score = extras.getLong("score");
        }

        // init clients and layout views
        setContentView(R.layout.activity_results);
        textViewName = (TextView) findViewById(R.id.results_name);
        textViewScore = (TextView) findViewById(R.id.results_score);
        textViewVisual = (TextView) findViewById(R.id.results_visual);
        addPhoto = (Button) findViewById(R.id.results_add_photo_btn);
        checkBox = (CheckBox) findViewById(R.id.results_checkbox);
        continueToPost = (Button) findViewById(R.id.results_continue_btn);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();

        // init db collectionsRefs
        final CollectionReference collectionReferenceQR = db.collection("QRCodes");
        final CollectionReference collectionReferencePlayer = db.collection("Users");

       // Checking if QR Code exists..
        DocumentReference QRCExists = db.collection("QRCodes").document(hashed);
        QRCExists.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) { // if QRCode already exists in DB
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        Log.d("TAG", "QR Code already exists in DB!");
                        Toast.makeText(ResultsActivity.this, "QR Code already exists", Toast.LENGTH_SHORT).show();
                        doesExist = true;
                        checkBox.setVisibility(View.INVISIBLE);
                        addPhoto.setVisibility(View.INVISIBLE);
                        List<String> scannedPlayers = (List<String>) document.get("playersScanned");
                        if (scannedPlayers != null) {
                            if (scannedPlayers.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) { // if user has already scanned QRCode
                                Toast.makeText(ResultsActivity.this, "User has already scanned this QRCode", Toast.LENGTH_SHORT).show();
                                hasScanned = true;
                                // TODO: GOTO: QRProfile...
                            }
                        }


                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        // Create name and visual icon for new QRCode
        name = createName(hashed);
        visual = createVisual(hashed);

        // Display score, name. visual:
        textViewName.setText(name);
        textViewScore.setText(""+score+" points!");
        textViewVisual.setText(visual);

        // TODO: Add photo (frick man)
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Location permission handling
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION, false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                            } else {
                                // No location access granted.
                            }
                        }
                );

        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        // Get geolocation...
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!includeGeolocation) {
                    includeGeolocation = true;

                    // check location permissions
                    if (ActivityCompat.checkSelfPermission(ResultsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ResultsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        locationPermissionRequest.launch(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        });

                        return;
                    }
                    CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cancellationTokenSource.getToken())
                            .addOnSuccessListener(ResultsActivity.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        lat = location.getLatitude();
                                        lon = location.getLongitude();
                                    }
                                    Log.w("TAG", "No current location could be found");
                                }
                            });
                }
                else { // reset
                    includeGeolocation =  false;
                    lat = null;
                    lon = null;

                }
            }
        });

        // Update DB and return to MainFeed
        continueToPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasScanned && !doesExist) {
                    // TODO: Send new QRCode to DB, update Player scanned QRCodes
                    Map<String,Object> newQRC = new HashMap<>();

//                HashMap<String, String> nameDB = new HashMap<>();
                    newQRC.put("Name", name);
//                HashMap<String, String> visualDB = new HashMap<>();
                    newQRC.put("icon",visual);
//                HashMap<String, Number> scoreDB = new HashMap<>();
                    newQRC.put("Points",score);
//                HashMap<String, String> hashedDB = new HashMap<>();
                    newQRC.put("Hash", hashed);
//                HashMap<String, Location> locationDB = new HashMap<>();
                    if (includeGeolocation && lat != null && lon != null) { // TODO: WHY THE FUCK IS IT NULL SOMETIMES??? MAYBE SLOW TO GET COORDS?? - CORDS ARE SET TO GOOGLE'S LOCATION FOR EMULATOR BTW.
                        GeoPoint geolocation = new GeoPoint(lat,lon);
                        Log.d("TAG", "GEOLOCATION "+geolocation);
                        newQRC.put("Geolocation", geolocation);
                    }
                    else {
                        newQRC.put("Geolocation", null);
                    }

                    newQRC.put("Comments", comments);
                    playersScanned.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    newQRC.put("playersScanned", playersScanned);

                    // Write new QRC to DB
                    db.collection("QRCodes").document(hashed) // DocIDs will be set to hashed
                            .set(newQRC)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Log.d("TAG", "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("TAG", "Error writing document", e);
                                }
                            });
                }

                else {

                    if (!hasScanned) {
                        final Map<String,Object> addUser = new HashMap<>();
                        addUser.put("playersScanned", FieldValue.arrayUnion(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                        db.collection("QRCodes").document(hashed)
                                .update(addUser);
                    }

                }
                finish();

            }
        });

    }

    private String createName(String hashed) {
        String hashedSubstring = hashed.substring(0,6);
        String QRName = "";

        // 16^5 = 1.04 million unique combos.
        HashMap<Character, String> hexMapName = new HashMap<Character, String>();
        hexMapName.put('0', "Alpha");
        hexMapName.put('1', "Bravo");
        hexMapName.put('2', "Charlie");
        hexMapName.put('3', "Delta");
        hexMapName.put('4', "Echo");
        hexMapName.put('5', "Foxtrot");
        hexMapName.put('6', "Golf");
        hexMapName.put('7', "Hotel");
        hexMapName.put('8', "India");
        hexMapName.put('9', "Juliet");
        hexMapName.put('a', "Kilo");
        hexMapName.put('b', "Lima");
        hexMapName.put('c', "Mike");
        hexMapName.put('d', "November");
        hexMapName.put('e', "Oscar");
        hexMapName.put('f', "Papa");

        QRName = hexMapName.get(hashedSubstring.charAt(0))+" "+hexMapName.get(hashedSubstring.charAt(1))+hexMapName.get(hashedSubstring.charAt(2))+hexMapName.get(hashedSubstring.charAt(3))+hexMapName.get(hashedSubstring.charAt(4))+hexMapName.get(hashedSubstring.charAt(5));
        Log.d("QRName:", QRName);
        return QRName;
    }

    private String createVisual (String hashed){
        String hashedSubstring = hashed.substring(0,4);
        String QRVisual = "";

        // 16^4 = 65K combos (65K X 1.04 Million = 1.1*10^12 combos)
        HashMap<Character, String> hexMapHead = new HashMap<Character, String>();
        hexMapHead.put('0', "C|");
        hexMapHead.put('1', "[|");
        hexMapHead.put('2', "<|");
        hexMapHead.put('3', "E|");
        hexMapHead.put('4', "#|");
        hexMapHead.put('5', "(|");
        hexMapHead.put('6', "F|");
        hexMapHead.put('7', "{|");
        hexMapHead.put('8', "d");
        hexMapHead.put('9', "[I");
        hexMapHead.put('a', "<=|");
        hexMapHead.put('b', "+=|");
        hexMapHead.put('c', "*(|");
        hexMapHead.put('d', "<)");
        hexMapHead.put('e', "c|");
        hexMapHead.put('f', "*=|");

        HashMap<Character, String> hexMapEyes = new HashMap<Character, String>();
        hexMapEyes.put('0', ":");
        hexMapEyes.put('1', ";");
        hexMapEyes.put('2', "$");
        hexMapEyes.put('3', "B");
        hexMapEyes.put('4', "X");
        hexMapEyes.put('5', "K");
        hexMapEyes.put('6', ">:");
        hexMapEyes.put('7', ">;");
        hexMapEyes.put('8', ">B");
        hexMapEyes.put('9', ">X");
        hexMapEyes.put('a', "=");
        hexMapEyes.put('b', "%");
        hexMapEyes.put('c', ">%");
        hexMapEyes.put('d', ">=");
        hexMapEyes.put('e', "D");
        hexMapEyes.put('f', ">D");

        HashMap<Character, String> hexMapNose = new HashMap<Character, String>();
        hexMapNose.put('0', "c");
        hexMapNose.put('1', "<");
        hexMapNose.put('2', ">");
        hexMapNose.put('3', "v");
        hexMapNose.put('4', "O");
        hexMapNose.put('5', "o");
        hexMapNose.put('6', "*");
        hexMapNose.put('7', "-");
        hexMapNose.put('8', "u");
        hexMapNose.put('9', "x");
        hexMapNose.put('a', "J");
        hexMapNose.put('b', "7");
        hexMapNose.put('c', ".");
        hexMapNose.put('d', ",");
        hexMapNose.put('e', "^");
        hexMapNose.put('f', "'");

        HashMap<Character, String> hexMapMouth = new HashMap<Character, String>();
        hexMapMouth.put('0', "b");
        hexMapMouth.put('1', "B");
        hexMapMouth.put('2', "]");
        hexMapMouth.put('3', "[");
        hexMapMouth.put('4', ")");
        hexMapMouth.put('5', "(");
        hexMapMouth.put('6', "|");
        hexMapMouth.put('7', "3");
        hexMapMouth.put('8', "6");
        hexMapMouth.put('9', "{]"); // Mustaches
        hexMapMouth.put('a', "{[");
        hexMapMouth.put('b', "{)");
        hexMapMouth.put('c', "{(");
        hexMapMouth.put('d', "{|");
        hexMapMouth.put('e', "D");
        hexMapMouth.put('f', "{D");

        QRVisual = hexMapHead.get(hashedSubstring.charAt(0))+hexMapEyes.get(hashedSubstring.charAt(1))+hexMapNose.get(hashedSubstring.charAt(2))+hexMapMouth.get(hashedSubstring.charAt(3));
        Log.d("QRVisual:", QRVisual);
        return QRVisual;
    }

    // TODO: Functions
    private void getImage() {} // will return Image from CameraX fragment...


}
