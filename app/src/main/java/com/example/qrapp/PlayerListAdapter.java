package com.example.qrapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PlayerListAdapter extends BaseAdapter
{
    Context context;
    ArrayList<Player> plys;

    public PlayerListAdapter(ArrayList<Player> players, Context cxt)
    {
        super();
        this.plys = players;
        this.context = cxt;

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.text_layout, viewGroup, false);
        Player player = plys.get(i);
        TextView playerUserName = row.findViewById(R.id.textinlist);
        playerUserName.setText(player.getUsername());
        return row;
    }
}
