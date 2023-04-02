package com.example.qrapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * This fragment shows a user's scanned QR codes. it is built to show either the current user's
 * or another user's scanned QR codes. If the current user is viewing their own QR codes, they
 * can delete them from the list. If they are viewing another user's QR codes, they can only
 * view them.
 */
public class ViewPlayerScannedQRActivity extends AppCompatActivity {


    private ListView qrListView;
    private ImageButton backButton;
    private QRcAdapter qRcAdapter;
    private ArrayList<QRCode> QRCodeList;
    private Boolean isCurrentUser;

    /**
     * This method is called when the activity is created. It sets up the list view and the
     * back button.
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // reusing the main fragment layout as they do the same thing, just with different data
        setContentView(R.layout.activity_player_scanned_qr);


        // get the list of QR codes and if viewing the current user from the intent
        Intent intent = getIntent();
        QRCodeList = (ArrayList<QRCode>) intent.getSerializableExtra("QRCodeList");
        isCurrentUser = intent.getBooleanExtra("isCurrentUser", false);

        // set up the list view
        qrListView = findViewById(R.id.item_listview);

        // set up the adapter for the list view
        if (isCurrentUser) {
            qRcAdapter = new RemovableQRCAdapter(QRCodeList, this);
        } else {
            qRcAdapter = new QRcAdapter(QRCodeList, this);
        }
        qrListView.setAdapter(qRcAdapter);


        // set the back button to go back to the previous activity
        backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> finish());

        //set a listener to open the QR profile when a QR code is clicked
        qrListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewPlayerScannedQRActivity.this, QRProfile.class);
                intent.putExtra("qr_code", QRCodeList.get(position));
                startActivity(intent);
            }
        });

    }
}