package com.example.qrapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;

/**
 * Testing Class to the functionality of searching for a User
 */
public class SearchPlayerTest {

    // methodology:
    // firstly:   test whether the query "User" returns User1 and User2
    // secondly:  test whether the adapter and listview correctly have the data
    // We can then conclude that all backend logic is functional

    /*
    @Test
    public void TestQuery() {

        FirebaseFirestore db = FirebaseFirestore.getInstance(); // this breaks everything for some reason...
        String searchText = "User";
        db.collection("Users").orderBy("Username").startAt(searchText).endAt(searchText + "\uf8ff").get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                DocumentSnapshot test1 = task.getResult().getDocuments().get(0);
                DocumentSnapshot test2 = task.getResult().getDocuments().get(1);
                String username1 = test1.getString("Username");
                String username2 = test2.getString("Username");
                assertEquals("User1", username1);
                assertEquals("User2", username2);
            }

        });
    }

    @Test
    public void TestAdapterAndListView() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String searchText = "User";
        ArrayList<Player> playerList = new ArrayList<Player>();
        SearchFragment fragment = new SearchFragment();
        ListView listview = new ListView(fragment.getContext());
        db.collection("Users").orderBy("Username").startAt(searchText).endAt(searchText + "\uf8ff").get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                DocumentSnapshot test1 = task.getResult().getDocuments().get(0);
                DocumentSnapshot test2 = task.getResult().getDocuments().get(1);
                String username1 = test1.getString("Username");
                String username2 = test2.getString("Username");
                String phoneNumber1 = test1.getString("phoneNumber");
                String phoneNumber2 = test2.getString("phoneNumber");
                String email1 = test1.getString("Email");
                String email2 = test2.getString("Email");
                Player player1 = new Player(username1, email1, "edmonton", phoneNumber1);
                Player player2 = new Player(username2, email2, "edmonton", phoneNumber2);
                playerList.add(player1);
                playerList.add(player2);
                PlayerListAdapter playerListAdapter = new PlayerListAdapter(playerList, fragment.getContext() , fragment.getActivity());
                listview.setAdapter(playerListAdapter);
                assertEquals(2, playerListAdapter.getCount());
                assertEquals(2, listview.getCount());
            }

        });

    }
*/
}