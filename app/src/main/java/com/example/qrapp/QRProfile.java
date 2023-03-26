package com.example.qrapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.example.qrapp.Comment;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.qrapp.QRCode;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class QRProfile extends AppCompatActivity {

    private TextView QRCName;
    private TextView points;
    private ImageView back;
    private ListView comments;
    private TextView icon;
    private ImageView image;
    private TextView scannedBy;
    private AppCompatButton players;
    private ArrayList playersList;
    private ArrayList commentsList;
    private ListView commentListView;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    /**
     * On create method for QRProfile activity
     * @param savedInstanceState a Bundle object to be saved
     */
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

        image = findViewById(R.id.qrprofile_image);
        scannedBy = findViewById(R.id.qrprofile_scannedby);
        players = findViewById(R.id.qrprofile_players_btn);
        commentListView = findViewById(R.id.commentsList);

        playersList = (ArrayList) qrCode.getPlayersScanned();
        commentsList = (ArrayList) qrCode.getComments();
        AtomicInteger completedCallbacks = new AtomicInteger(0);
        ArrayList<Comment> commentList = new ArrayList<>();

        // iterate through the comments list
        if (commentsList != null){
            for (int i = 0; i < commentsList.size(); i++) {
                String commentId = (String) commentsList.get(i);
                // get the comment document so we can get the comment text and author
                DocumentReference commentRef = db.collection("Comments").document(commentId);
                commentRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    // Document found, process the data
                                    String commentText = documentSnapshot.getString("Comment");
                                    String author = documentSnapshot.getString("Author");
                                    // get the user document so we can get the user id
                                    DocumentReference userRef = db.collection("Users").document(author);
                                    userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot userDocumentSnapshot) {
                                            if (userDocumentSnapshot.exists()) {
                                                String username = userDocumentSnapshot.getString("username");
                                                Comment comment = new Comment(author, username, commentText);
                                                commentList.add(comment);

                                                if (completedCallbacks.incrementAndGet() == commentsList.size()) {
                                                    CommentAdapter adapter = new CommentAdapter(QRProfile.this, R.layout.item_comment, commentList, FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                    commentListView.setAdapter(adapter);
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        });
            }
        }
        int size;
        try{
            size  = playersList.size();
        }
        catch (Exception e)
        {
            size = 0;
        }
        scannedBy.setText("Scanned by "+size+" player(s).");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        QRCName.setText(qrCode.getName()); // set the name text
        points.setText(qrCode.getPoints() + " Points"); // set the points text
        icon.setText(qrCode.getIcon()); // set the icon text

        getImage(qrCode); // check cloud storage for image

        players.setOnClickListener(new View.OnClickListener() { // see players who have scanned qr code
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QRProfile.this, ScannedBy.class);
                intent.putParcelableArrayListExtra("userIDs",playersList);
                QRProfile.this.startActivity(intent);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * On click method for back button
             * @param v a View object
             */
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Display thumbnail image if exists for qrc in cloud storage.
     * @param qrCode get the hash from the qrCode object
     */
    public void getImage(QRCode qrCode) {
        StorageReference storageRef = storage.getReference();
        Log.d("TAG", "hash: "+qrCode.getHashed()+".jpg");
        StorageReference imageRef = storageRef.child(qrCode.getHashed()+".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                image.setImageBitmap(bitmap);
            }
        }) .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(QRProfile.this,"No thumbnail to display", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
