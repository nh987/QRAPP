package com.example.qrapp;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

//CUSTOM ADAPTER FOR QRc Object
// Used to show them in format we want
class QRcAdapter extends BaseAdapter {
    ArrayList<QRCode> items;
    Context mycontext;

    public QRcAdapter(ArrayList<QRCode> items, Context context) {
        super();
        this.mycontext = context;
        this.items = items;
    }

    @Override
    public int getCount() {
//        return items.size();
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return items.get(i).hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(mycontext);
        View row = inflater.inflate(R.layout.item_qrc, viewGroup, false);
        QRCode qrCode = items.get(i);
        TextView qrCodeName = row.findViewById(R.id.QRCName);
        TextView qrCodePoints = row.findViewById(R.id.score);
        qrCodeName.setText(qrCode.getName());
        qrCodePoints.setText(qrCode.getPoints().toString());
        return row;
    }

}
