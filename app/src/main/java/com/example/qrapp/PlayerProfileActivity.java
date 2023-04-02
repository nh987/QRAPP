package com.example.qrapp;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.qrapp.QRCode;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.checkerframework.checker.units.qual.A;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Activity class to display searched Player profiles from SearchFragment.java
 */
public class PlayerProfileActivity extends AppCompatActivity {

    TextView profileHeader;
    TextView usernameText;
    TextView emailText;
    ImageView back;
    TextView highestQrc;
    TextView lowestQrc;
    TextView ranking;
    TextView totalScore;
    TextView totalCodesScanned;
    ImageView viewHighestQRCButton;
    ImageView viewLowestQRCButton;

    Button viewCodesScanned;


    public PlayerProfileActivity() {

    }


    @SuppressLint("SetTextI18n")
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
         profileHeader = findViewById(R.id.profile);
         usernameText = findViewById(R.id.username);
         emailText = findViewById(R.id.email);
         back = findViewById(R.id.back);
         highestQrc = findViewById(R.id.highestQRCvalue);
         lowestQrc = findViewById(R.id.lowestQRCvalue);
         ranking = findViewById(R.id.rankingValue);
         totalScore = findViewById(R.id.totalscoreValue);
         totalCodesScanned = findViewById(R.id.codesScannedValue);
         viewHighestQRCButton = findViewById(R.id.viewHighestQRCButton);
         viewLowestQRCButton = findViewById(R.id.viewLowestQRCButton);
         viewCodesScanned = findViewById(R.id.myQRCbutton);
        String profile = username + "'s Profile";
        profileHeader.setText(profile);
        // Set player data, init db
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String finalUsername = username;
        hideButtons();

        db.collection("Users").whereEqualTo("username", username).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                String email = document.getString("email");
                usernameText.setText(finalUsername);
                emailText.setText(email);
            }
        });
        // view QR codes


        // stats here
        // So because playersScanned in QrCodes is device ID, we need to first query user's until we find a matching deviceID-username pair
        db.collection("Users").whereEqualTo("username", username).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String deviceId = document.getId();
                        // next query all QRCodes playersScanned Fields, add to arrayList (need points as well)
                        db.collection("QRCodes").whereArrayContains("playersScanned", deviceId).get().addOnCompleteListener(newTask -> {
                            if (newTask.isSuccessful()) {
                                try {
                                    DocumentSnapshot test = newTask.getResult().getDocuments().get(0);
                                }
                                catch (IndexOutOfBoundsException e) {
                                    String noCodes = "N/A";
                                    highestQrc.setText(noCodes);
                                    lowestQrc.setText(noCodes);
                                    totalScore.setText(noCodes);
                                    ranking.setText(noCodes);
                                    totalCodesScanned.setText(noCodes);
                                    Log.d("myTag", "Nothing scanned");
                                    return;
                                }
                                // iterate through all respective documents where playersScanned contains deviceID
                                List<DocumentSnapshot> documents = newTask.getResult().getDocuments();
                                ArrayList<Long> pointsList = new ArrayList<>();
                                ArrayList<QRCode> QrList = new ArrayList<>();
                                for (DocumentSnapshot iterativeDocument : documents) {
                                    // make QR codes here
                                    long points = (long) iterativeDocument.get("Points");
                                    pointsList.add(points);
                                    String name = iterativeDocument.getString("Name");
                                    String icon = iterativeDocument.getString("icon");
                                    Object playersScanned = iterativeDocument.get("playersScanned");
                                    Object comments = iterativeDocument.get("Comments");
                                    GeoPoint geolocation = iterativeDocument.getGeoPoint("Geolocation");
                                    String hashed = iterativeDocument.getString("Hash");
                                    int intPoints = (int) points;
                                    QRCode queriedQR = new QRCode(comments, intPoints, name, icon, playersScanned, geolocation, hashed);
                                    QrList.add(queriedQR);
                                }
                                long highest = Collections.max(pointsList);
                                long lowest = Collections.min(pointsList);
                                long totalScoreValue = 0;
                                long totalCodesScannedValue = pointsList.size();
                                for(int i = 0; i < pointsList.size(); i++) {
                                    totalScoreValue += pointsList.get(i);
                                }
                                highestQrc.setText(Long.toString(highest));
                                lowestQrc.setText(Long.toString(lowest));
                                totalScore.setText(Long.toString(totalScoreValue));



                                String filler = "Loading...";
                                ranking.setText(filler);




                                totalCodesScanned.setText(Long.toString(totalCodesScannedValue));
                                // find highest and lowest QR
                                QRCode highestQR = new QRCode(null, -1, null, null, null, null, null);
                                QRCode lowestQR = new QRCode(null, 100000, null, null, null, null, null);
                                for (QRCode qrCode : QrList) {
                                    int loopPoints = Integer.parseInt(qrCode.getPoints());
                                    int highestQRpoints = Integer.parseInt(highestQR.getPoints());
                                    int lowestQRpoints = Integer.parseInt(lowestQR.getPoints());
                                    if(loopPoints > highestQRpoints) {
                                        highestQR = qrCode;
                                    }
                                    if(loopPoints < lowestQRpoints){
                                        lowestQR = qrCode;
                                    }
                                }
                                // set the buttons to open the QRProfile activity
                                setViewButtons(highestQR, lowestQR);

                                // set the view QR codes button to open a listview activity?
                                setPlayerCodesScanned(QrList);

                                // calculate ranking
                                calculateRanking(db, deviceId, ranking);

                                // show buttons now that querying is done
                                showButtons();

                            }
                            });
                        }
                    });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * On click method for back button
             * @param v
             */
            public void onClick(View v) {
                finish();
            }
        });

    }
    /**
     * Hides clickable buttons until query and value init are all done
     */
    public void hideButtons()
    {
        // set invisible until queries are done
        viewHighestQRCButton.setEnabled(false);
        viewHighestQRCButton.setVisibility(View.INVISIBLE);
        viewLowestQRCButton.setEnabled(false);
        viewLowestQRCButton.setVisibility(View.INVISIBLE);
        viewCodesScanned.setEnabled(false);
        viewCodesScanned.setVisibility(View.INVISIBLE);
    }

    /**
     * shows clickable buttons once all queries and value init are all done
     */
    public void showButtons()
    {
        // enable the buttons
        viewHighestQRCButton.setEnabled(true);
        viewHighestQRCButton.setVisibility(View.VISIBLE);
        viewLowestQRCButton.setEnabled(true);
        viewLowestQRCButton.setVisibility(View.VISIBLE);
        viewCodesScanned.setEnabled(true);
        viewCodesScanned.setVisibility(View.VISIBLE);
    }

    /**
     * sets values for viewing highest/ lowest qr codes
     * @param highest the highest scoring QR code the player has scanned
     * @param lowest the lowest scoring QR code the player has scanned
     */

    public void setViewButtons(QRCode highest, QRCode lowest)
    {
        final QRCode finalLowestQR = lowest;
        final QRCode finalHighestQR = highest;
        viewHighestQRCButton.setOnClickListener(v -> {
            Intent intent = new Intent(PlayerProfileActivity.this, QRProfile.class);
            intent.putExtra("qr_code", finalHighestQR);
            startActivity(intent);
        });
        viewLowestQRCButton.setOnClickListener(v -> {
            Intent intent = new Intent(PlayerProfileActivity.this, QRProfile.class);
            intent.putExtra("qr_code", finalLowestQR);
            startActivity(intent);
        });
    }

    /**
     * sets the button for view player's QR codes
     * @param qrList the highest scoring QR code the player has scanned
     */
    public void setPlayerCodesScanned(ArrayList<QRCode> qrList) {
        viewCodesScanned.setOnClickListener(v -> {
            Intent intent = new Intent(PlayerProfileActivity.this, ViewPlayerScannedQRActivity.class);
            intent.putExtra("QRCodeList", qrList);
            intent.putExtra("isCurrentUser", false);
            startActivity(intent);
        });

    }
    /**
     * calculates the player's ranking for highest unique QR code
     * @param db passed instance of firebase database
     * @param deID the deviceId to calculate the ranking of
     * @param rnk the textView to change once rank has been calculated
     */
    public void calculateRanking(FirebaseFirestore db, String deID, TextView rnk) {
        // As a player, I want an estimate of my ranking for the highest scoring unique QR code
        // 1. go through all players, get their highest QR code that only they scanned, store it in a dict
        // query all player's deviceID, add them to a dict with highest = 0
        Hashtable<String, Integer> deviceIdDict = new Hashtable<String, Integer>();
        db.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                for (DocumentSnapshot iterativeDocument : documents) {
                    String deviceId = iterativeDocument.getId();
                    deviceIdDict.put(deviceId, 0);
                }
                //2. Go through all playersScanned fields, find highest QR code that only they scanned
                Set<String> setofKeys = deviceIdDict.keySet();
                for (String key : setofKeys) {
                    db.collection("QRCodes").whereArrayContains("playersScanned", key).get().addOnCompleteListener(newTask -> {
                        if (newTask.isSuccessful()) {
                            try {
                                DocumentSnapshot test = newTask.getResult().getDocuments().get(0);
                            }
                            catch (IndexOutOfBoundsException e) {
                                Log.d("myTag", "Nothing scanned");
                            }
                            // loop through QR codes where playersScanned contains DeviceID
                            // check if ONLY they scanned it
                            int highestUnique = 0;
                            List<DocumentSnapshot> newTaskDocuments = newTask.getResult().getDocuments();
                            for (DocumentSnapshot newDocument : newTaskDocuments) {
                                ArrayList<String> pS = new ArrayList<>();
                                pS = (ArrayList<String>) newDocument.get("playersScanned");
                                int size = pS.size();
                                if(size == 1) { // now we know that the only player who scanned it is the 'key'
                                    long points = (long) newDocument.get("Points");
                                    if(points > highestUnique) {
                                        highestUnique =  (int) points;
                                    }
                                }
                            }
                            Log.d("myTag", Integer.toString(highestUnique));
                            deviceIdDict.put(key, highestUnique);
                        }
                    });
                }
                // we have a dict of highest uniques now (confirmed working), just need to sort it so (highest = index 0)
                // yeah I know this is ugly I used a bad datatype
                ArrayList<Integer> highestArrayList = new ArrayList<>();
                ArrayList<String> highestStringArrayList = new ArrayList<>();
                Collection<Integer> highestSet = deviceIdDict.values();
                Set<String> highestDvID = deviceIdDict.keySet();
                highestArrayList.addAll(highestSet);
                highestStringArrayList.addAll(highestDvID);
                Collections.sort(highestArrayList, Collections.reverseOrder());
                int playerScore = deviceIdDict.get(deID);
                int index = 1;
                for(int score : highestArrayList) {
                    if(score == playerScore) {
                        String stringRank = Integer.toString(index);
                        rnk.setText(stringRank);
                        break;
                    }
                    index += 1;
                }
            }
        });

    }


}



