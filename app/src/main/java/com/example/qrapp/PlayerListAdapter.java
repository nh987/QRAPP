package com.example.qrapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
/**
 * Class for playerList (search functionality) to display queried users in a listview
 *
 *
 */
public class PlayerListAdapter extends ArrayAdapter<Player>
{
    Context context;
    ArrayList<Player> plys;

    public PlayerListAdapter(ArrayList<Player> players, Context cxt)
    {
        super(cxt, R.layout.text_layout, players);
        //LayoutInflater view = LayoutInflater.from(context);
        this.plys = players;
        this.context = cxt;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Player getItem(int i) {
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
        // might have to set button listener here
        return row;
    }
}
