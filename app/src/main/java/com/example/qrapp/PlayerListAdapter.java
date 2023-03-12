package com.example.qrapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for defining a custom adapter for a ListView for player Searching
 *
 *
 */
class PlayerListAdapter extends BaseAdapter {
    ArrayList<Player> items;
    Context mycontext;

    Activity myActivity;

    public PlayerListAdapter(ArrayList<Player> items, Context context, Activity activity) {
        super();
        this.mycontext = context;
        this.items = items;
        this.myActivity = activity;
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
        View row = inflater.inflate(R.layout.text_layout, viewGroup, false);
        Player player = items.get(i);
        TextView username = row.findViewById(R.id.textinlist);
        username.setText(player.getUsername().toString());

        Button button = row.findViewById(R.id.viewProfile);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                // TODO actually have it open a fragment lol
                try {
                    Intent playerProfileIntent = new Intent(myActivity, PlayerProfileActivity.class);
                    playerProfileIntent.putExtra("player", player.getUsername());
                    myActivity.startActivity(playerProfileIntent);
                }
                catch (Exception e) {
                    Log.d("myTag", e.toString());
                    Toast errorToast = Toast.makeText(mycontext, "An error occurred, please try again", Toast.LENGTH_SHORT);
                    errorToast.show();
                }
            }
        });

        return row;
    }

}
