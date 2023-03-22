package com.example.qrapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.AbstractMap;
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

/**
 * ResultsActivity gets bundle from ScanActivity containing a SHA-256 hashed string of the barcode
 * and long score value of the hash according to the scoring system. Score is displayed in a TextView and
 * The hashed string is then compared to a Firebase DB query to check if A.) hash already exists in QRCodes
 * collection and B.) If it does exist, if in its field playerScanned arrayList does the UserID already exist inside
 * the list. If only A is met userID will be added into the playerScanned array. If neither condition is met, a new
 * barcode entry is created and the hash is used to generate a name and visual representation of the hash according to the first
 * 6 digits of the hash. User will have the option to Add Photo and Include Geolocation. Add Photo opens camera and
 * user takes photo. Image is sent into Cloud Storage with metadata containing userID. If checked - Include Geolocation
 * will get user permissions and get latitude and longitude of the device and will be used to create a GeoPoint.
 * After this the hash, score, name, visual, GeoPoint (can be null), Comments (array), playersScanned (array) are all combined
 * to make a single QRCode instance that is sent into the DB. The User's UserID is then appended into the
 * playersScanned array for the QRCode instance.
 */
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
    Bitmap imageBitmap;
    Intent results;
    EditText comment;
    private static final int CAMERA_REQUEST = 100;
    Double lat;
    Double lon;
    Button continueToPost;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
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
        comment = (EditText) findViewById(R.id.results_comment);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();

        // init db collectionsRefs
        final CollectionReference collectionReferenceQR = db.collection("QRCodes");
        final CollectionReference collectionReferencePlayer = db.collection("Users");
        final CollectionReference collectionReferenceComments = db.collection("Comments");

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
//                        checkBox.setVisibility(View.INVISIBLE);
//                        addPhoto.setVisibility(View.INVISIBLE);
                        List<String> scannedPlayers = (List<String>) document.get("playersScanned");
                        if (scannedPlayers != null) {
                            if (scannedPlayers.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) { // if user has already scanned QRCode
                                Toast.makeText(ResultsActivity.this, "User has already scanned this QRCode", Toast.LENGTH_SHORT).show();
                                hasScanned = true;
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

        // Add image
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isIntentAvailable(ResultsActivity.this, MediaStore.ACTION_IMAGE_CAPTURE);
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
//                addPhoto.setVisibility(View.INVISIBLE);
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
                if (!hasScanned && !doesExist) { // if qrc is completely new
                    Map<String,Object> newQRC = new HashMap<>();
                    newQRC.put("Name", name);
                    newQRC.put("icon",visual);
                    newQRC.put("Points",score);
                    newQRC.put("Hash", hashed);


                    if (includeGeolocation && lat != null && lon != null) {
                        GeoPoint geolocation = new GeoPoint(lat,lon);
                        Log.d("TAG", "GEOLOCATION "+geolocation);
                        newQRC.put("Geolocation", geolocation);
                    }
                    else {
                        newQRC.put("Geolocation", null);
                    }

                    String commentText = comment.getText().toString();
                    if (!TextUtils.isEmpty(commentText)) { // check if comment is not empty
                        Map<String, Object> commentMap = new HashMap<>();
                        commentMap.put("Comment", commentText);
                        commentMap.put("Author", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        commentMap.put("QRCode", hashed);
                        DocumentReference newCommentRef = db.collection("Comments").document();
                        final String commentRefId = newCommentRef.getId();
                        newCommentRef.set(commentMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // Add the comment ID to the 'comments' list
                                    }
                                });

                        comments.add(commentRefId);
                        newQRC.put("Comments", comments);
                    } else {
                        // handle empty comment here, e.g. show an error message to the user
                    }
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
                    // if the qr code already exists
                    Map<String, Object> commentMap = new HashMap<>();
                    commentMap.put("Comment", comment.getText().toString());
                    commentMap.put("Author", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    commentMap.put("QRCode", hashed);

                    DocumentReference newCommentRef = db.collection("Comments").document();
                    final String commentRefId = newCommentRef.getId();
                    newCommentRef.set(commentMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    // Add the comment ID to the 'comments' list
                                    comments.add(commentRefId);
                                }
                            });
                    Map<String, Object> updateComments = new HashMap<>();
                    updateComments.put("Comments", FieldValue.arrayUnion(commentRefId));
                    db.collection("QRCodes").document(hashed)
                            .update(updateComments);


                    if (!hasScanned) { // user has not scanned this QR code yet
                        final Map<String,Object> addUser = new HashMap<>();
                        addUser.put("playersScanned", FieldValue.arrayUnion(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                        db.collection("QRCodes").document(hashed)
                                .update(addUser);
                    }

                    final Map<String,Object> updateGeolocation = new HashMap<>();
                    if (includeGeolocation) { // reset geolocation
                        GeoPoint geolocation = new GeoPoint(lat,lon);
                        Log.d("TAG", "UPDATE GEOLOCATION "+geolocation);
                        updateGeolocation.put("Geolocation", geolocation);
                    }
                    else {
                        Log.d("TAG", "REMOVED GEOLOCATION");
                        updateGeolocation.put("Geolocation", null);
                    }
                    db.collection("QRCodes").document(hashed)
                            .update(updateGeolocation);
                }

                finish(); // return to main activity TODO: go to QRProfile instead
            }
        });

    }

    /**
     * Pass in hashed barcode string and take first 6 digits that are mapped to various words that are then concatenated together to create
     * a name.
     * @param hashed
     * @return string
     */
    public String createName(String hashed) {
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
    /**
     * Pass in hashed barcode string and take first 4 digits that are mapped to various emoticon (head/hat, eyes, nose, mouth
     * that are then concatenated together to create a visual representation.
     * @param hashed
     * @return string
     */
    public String createVisual (String hashed){
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
        hexMapNose.put('c', "^");
        hexMapNose.put('d', "~");
        hexMapNose.put('e', "y");
        hexMapNose.put('f', "0");

        HashMap<Character, String> hexMapMouth = new HashMap<Character, String>();
        hexMapMouth.put('0', "P");
        hexMapMouth.put('1', "B");
        hexMapMouth.put('2', "]");
        hexMapMouth.put('3', "[");
        hexMapMouth.put('4', ")");
        hexMapMouth.put('5', "(");
        hexMapMouth.put('6', "|");
        hexMapMouth.put('7', "3");
        hexMapMouth.put('8', "L");
        hexMapMouth.put('9', "{]"); // Mustaches
        hexMapMouth.put('a', "{[");
        hexMapMouth.put('b', "{)");
        hexMapMouth.put('c', "{(");
        hexMapMouth.put('d', "{|");
        hexMapMouth.put('e', "/");
        hexMapMouth.put('f', "{/");

        QRVisual = hexMapHead.get(hashedSubstring.charAt(0))+hexMapEyes.get(hashedSubstring.charAt(1))+hexMapNose.get(hashedSubstring.charAt(2))+hexMapMouth.get(hashedSubstring.charAt(3));
        Log.d("QRVisual:", QRVisual);
        return QRVisual;
    }

    /**
     * check/get intent permissions
     * @param context
     * @param action
     * @return boolean
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * Get bitmap image from Intent bundle.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            imageBitmap = (Bitmap) data.getExtras().get("data");
            uploadImage();

        }
    }

    /**
     * Convert bitmap to bytes and upload to Cloud Storage using barcode hash as jpg name. Includes userID as metadata of image.
     */
    public void uploadImage () {
        StorageReference storageRef = storage.getReference();
        StorageReference qrcRef = storageRef.child(hashed+".jpg"); // init storage ref image

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] data = baos.toByteArray();

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .setCustomMetadata("UserID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .build();

        UploadTask uploadTask = qrcRef.putBytes(data, metadata);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ResultsActivity.this,"Thumbnail upload failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ResultsActivity.this,"Thumbnail upload successful", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
