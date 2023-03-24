package com.example.qrapp;

import static java.lang.Math.toRadians;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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

/**
 * This fragment shows a user's scanned QR codes. it is built to show either the current user's
 * or another user's scanned QR codes. If the current user is viewing their own QR codes, they
 * can delete them from the list. If they are viewing another user's QR codes, they can only
 * view them.
 */
public class ViewPlayerScannedFragment extends Fragment {

    private ListView qrListView;
    private QRcAdapter qRcAdapter;
    private ArrayList<QRCode> QRCodeList;

    private Boolean isCurrentUser;
    private ListenerRegistration qrCodeListener;

    //I added a "Scrolling" interface to hide the nav bar when scrolling down.
    //I noticed that the very last item in the Listview is difficult if not impossible
    //to see. Not a tremendous issue but addressed it since I could
    //See->"for scrolling interface" below
    public ViewPlayerScannedFragment(ArrayList<QRCode> list, Boolean isCurrentUser)
    {
        this.QRCodeList = list;
        isCurrentUser = isCurrentUser;
    }

    public interface Scrollable {
        public void Scrollable(int scrollState);
    }
    private Scrollable dataPasser;
    //


    @Nullable
    @Override
    /**
     * Create a list of all the QR codes in the database in a "feed style" list
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return contentView
     */
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_main, container, false);
        qrListView = contentView.findViewById(R.id.item_listview);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Ok future William, qrCodelist is instantiated properly, but this is erroring
        // you can delete the query and you need to refactor this class
        // however I believe the logic will wo rk





        qRcAdapter = new QRcAdapter(QRCodeList, getContext());
        qrListView.setAdapter(qRcAdapter);

        //interface for scrolling
        qrListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int LastFirstVisibleItem;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(LastFirstVisibleItem<firstVisibleItem)
                {
                    //Log.i("SCROLLING DOWN","TRUE");
                    passData(2);
                }
                if(LastFirstVisibleItem>firstVisibleItem)
                {
                    passData(1);
                    //Log.i("SCROLLING UP","TRUE");
                }
                LastFirstVisibleItem=firstVisibleItem;

            }
        });
        //interface for scrolling

        return contentView;
    }


    //for scrolling interface
    /*Overview
    The Scrollable Interface works by passing Scrolling data from the Main
    Fragment to the Main Activity. I has been set as so
     1) the Main Fragment determines the scroll direction and has a Scrollable
      Interface object whose sole purpose is receiving data from the fragment
     2) Main Fragment passes scroll direction(a number) to Main Activity by using a scroll listener on the Main Fragments ListView
     3) Main Activity implements the Main Fragments Scrollable Interface so it can receive data from any Main Fragment Object
     4) The bottom nav bar is hidden when scrolling down(2) and reappears when scrolling up(not 2)

    The solution here is a bit tacky but seems to work ok
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (Scrollable) context;
    }

    public void passData(int data) {
        dataPasser.Scrollable(data);
    }
    //// End of Scroll Interface

    @Override
    /**
     * Removes the listener when the fragment is destroyed
     */
    public void onDestroyView() {
        super.onDestroyView();

        // get rid of the listener when fragment view is destroyed so it doesn't keep listening
        if (qrCodeListener != null) {
            qrCodeListener.remove();
        }
    }
}
