package com.example.qrapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * Class which creates a fragment from profile_user, and populates it with Search data from
 * SearchFragment.java
 *
 */
public class PlayerProfileFragment extends Fragment {

    private Player player;

    public PlayerProfileFragment(Player curPlayer) {
        this.player = curPlayer;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_user, container, false);
        // change 'My Profile' --> 'User123's profile'
        TextView profileHeader = view.findViewById(R.id.profile);
        String profile = player.getUsername() + "'s Profile";
        profileHeader.setText(profile);
        // Set player data
        TextView usernameText = view.findViewById(R.id.username);
        TextView emailText = view.findViewById(R.id.email);
        usernameText.setText(player.getUsername());
        emailText.setText(player.getEmail());

        return view;
    }

}
