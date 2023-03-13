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

/**
 * Adapter for QRCode
 */
class QRcAdapter extends BaseAdapter {
    ArrayList<QRCode> items;
    Context mycontext;

    /**
     * Constructor for QRcAdapter
     * @param items
     * @param context
     */
    public QRcAdapter(ArrayList<QRCode> items, Context context) {
        super();
        this.mycontext = context;
        this.items = items;
    }

    @Override
    /**
     * Get the count of items
     * @return count
     */
    public int getCount() {
//        return items.size();
        return items.size();
    }

    @Override
    /**
     * Get the item at position i
     * @param i
     * @return item
     */
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    /**
     * Get the item id at position i
     * @param i
     * @return item id
     */
    public long getItemId(int i) {
        return items.get(i).hashCode();
    }

    @Override
    /**
     * Get the view at position i
     * @param i
     * @param view
     * @param viewGroup
     * @return row
     */
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(mycontext);
        View row = inflater.inflate(R.layout.item_qrc, viewGroup, false);
        QRCode qrCode = items.get(i);
        TextView qrCodeName = row.findViewById(R.id.QRCName);
        TextView qrCodePoints = row.findViewById(R.id.score);
        TextView qrCodeIcon = row.findViewById(R.id.visual);
        qrCodeName.setText(qrCode.getName());
        qrCodePoints.setText(qrCode.getPoints().toString());
        qrCodeIcon.setText(qrCode.getIcon());
        return row;
    }

}
