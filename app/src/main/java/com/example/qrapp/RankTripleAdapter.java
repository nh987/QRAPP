package com.example.qrapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RankTripleAdapter extends BaseAdapter {

    ArrayList<RankTriple> items;
    Context mycontext;

    /**
     * Constructor for QRcAdapter
     * @param items list of QRCodes
     * @param context context
     */
    public RankTripleAdapter(ArrayList<RankTriple> items, Context context) {
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
        View row = inflater.inflate(R.layout.item_ranktriple, viewGroup, false);

        RankTriple row_data = items.get(i);

        TextView tv_rankN = row.findViewById(R.id.triple_ranknumber);
        TextView tv_playerName = row.findViewById(R.id.triple_playername);
        TextView tv_codeFace = row.findViewById(R.id.triple_codeface);
        TextView tv_codeScore = row.findViewById(R.id.triple_codepoints);

        tv_rankN.setText(String.valueOf(i+1));
        tv_playerName.setText(row_data.PlayerID);
        tv_codeFace.setText(row_data.QRcFace);
        tv_codeScore.setText(String.valueOf(row_data.QRcPoints));

        return row;
    }

}
