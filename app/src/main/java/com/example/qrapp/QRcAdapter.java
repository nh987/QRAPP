package com.example.qrapp;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

//CUSTOM ADAPTER FOR QRc Object
// Used to show them in format we want
class QRcAdapter extends BaseAdapter {
    ArrayList<String> items;
    Context mycontext;

    public QRcAdapter(ArrayList<String> items, Context context) {
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
        TextView textView = new TextView(mycontext);
        textView.setText(items.get(i));
        return textView;
    }
}