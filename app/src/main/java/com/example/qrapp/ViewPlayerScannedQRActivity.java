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

public class ViewPlayerScannedQRActivity extends AppCompatActivity {


    private ListView qrListView;

    private ImageButton backButton;
    private QRcAdapter qRcAdapter;
    private ArrayList<QRCode> QRCodeList;
    private Boolean isCurrentUser;


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