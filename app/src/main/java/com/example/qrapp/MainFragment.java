package com.example.qrapp;

import static java.lang.Math.toRadians;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainFragment extends Fragment {

    private ListView qrListView;
    private QRcAdapter qRcAdapter;
    private ArrayList<QRCode> QRCodeList;
    private ListenerRegistration qrCodeListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_main, container, false);
        qrListView = contentView.findViewById(R.id.item_listview);

        QRCodeList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add a real-time listener for updates to the database
        qrCodeListener = db.collection("QRCodes").addSnapshotListener((value, error) -> {
            QRCodeList.clear();
            for (DocumentSnapshot document : value.getDocuments()) {
                Integer points = document.getLong("Points").intValue();
                String name = document.getString("Name");
                String icon = document.getString("icon");
                Object playersScanned = document.get("playersScanned");
                GeoPoint geolocation = document.getGeoPoint("Geolocation");
                Object comments = document.get("Comments");

                QRCode queriedQR = new QRCode(comments, points, name, icon, playersScanned, geolocation);
                QRCodeList.add(queriedQR);
            }

            qRcAdapter.notifyDataSetChanged();
            qrListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    System.out.println(QRCodeList.size());
                    QRCode qrCode = QRCodeList.get(i);
                    Intent intent = new Intent(getActivity(), QRProfile.class);
                    intent.putExtra("qr_code", qrCode); // pass the clicked item to the QRCProfile class
                    startActivity(intent);
                }
            });
        });

        qRcAdapter = new QRcAdapter(QRCodeList, getContext());
        qrListView.setAdapter(qRcAdapter);

        return contentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // get rid of the listener when fragment view is destroyed so it doesn't keep listening
        if (qrCodeListener != null) {
            qrCodeListener.remove();
        }
    }
}
