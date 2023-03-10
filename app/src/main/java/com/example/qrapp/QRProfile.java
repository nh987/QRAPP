package com.example.qrapp;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qrapp.QRCode;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class QRProfile extends AppCompatActivity {

    private TextView QRCName;
    private TextView points;
    private ImageView back;
    private ListView comments;
    private TextView icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_qrc);

        Intent intent = getIntent();
        QRCode qrCode = intent.getParcelableExtra("qr_code"); // get the passed item

        QRCName = findViewById(R.id.QRCName);
        points = findViewById(R.id.textView3);
        back = findViewById(R.id.back);
        comments = findViewById(R.id.commentsList);
        icon = findViewById(R.id.icon);

        QRCName.setText(qrCode.getName()); // set the name text
        points.setText(qrCode.getPoints());
        icon.setText(qrCode.getIcon());

        // set the comments list
        List<Object> commentObjects = (List<Object>) qrCode.getComments(); // get the comments list
        List<String> commentStrings = new ArrayList<>();

        for (Object comment : commentObjects) {
            if (comment != null) {
                commentStrings.add(comment.toString());
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, commentStrings);
        comments.setAdapter(adapter);





        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
