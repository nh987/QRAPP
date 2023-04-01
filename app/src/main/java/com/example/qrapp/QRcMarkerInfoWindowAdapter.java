package com.example.qrapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class QRcMarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private View markerInfoWindow;
    private Context my_context;

    public QRcMarkerInfoWindowAdapter(Context context){
        this.my_context = context;
        this.markerInfoWindow = LayoutInflater.from(this.my_context).inflate(R.layout.qrc_marker_info_window, null);

    }


    private void render(Marker marker, View view){
        //set the title==icon + points + [scanned by user]scanned
        LinearLayout windowLout = view.findViewById(R.id.Lout_window);
        String QRc_title = marker.getTitle();
        TextView textView_QRcTitle = view.findViewById(R.id.custom_title);
        if(!QRc_title.equals("")){textView_QRcTitle.setText(QRc_title);}

        //set the snippet==unique name
        String QRc_snippet = marker.getSnippet();
        TextView textView_QRcSnippet = view.findViewById(R.id.custom_snippet);
        if(!QRc_snippet.equals("")){textView_QRcSnippet.setText(QRc_snippet);}

        Log.d("M",String.valueOf(marker.getAlpha()));
        float x = marker.getAlpha(); //will determine a scanned qrc by giving it a specific alpha
        float scanned_alpha_min = 0.94f,
                scanned_alpha_max = 0.96f,
                scanned_alpha_dragmin = 0.44f,
                scanned_alpha_dragmax = 0.46f;
        if(scanned_alpha_min<=x&&x<=scanned_alpha_max || scanned_alpha_dragmin<=x&&x<=scanned_alpha_dragmax) {
            //scanned Info Window
            textView_QRcTitle.setTextColor(ContextCompat.getColor(my_context, R.color.white));
            textView_QRcSnippet.setTextColor(ContextCompat.getColor(my_context, R.color.white));
            windowLout.setBackground(ContextCompat.getDrawable(my_context, R.drawable.qrc_window_two));
        }else{
            //regulare
            textView_QRcTitle.setTextColor(ContextCompat.getColor(my_context, R.color.main));
            textView_QRcSnippet.setTextColor(ContextCompat.getColor(my_context, R.color.main));
            windowLout.setBackground(ContextCompat.getDrawable(my_context, R.drawable.gradient_bg));

        }
    }


    @Nullable
    @Override
    public View getInfoContents(@NonNull com.google.android.gms.maps.model.Marker marker) {
        // return null; the default
        render(marker,markerInfoWindow);
        return markerInfoWindow;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull com.google.android.gms.maps.model.Marker marker) {
        // return null; the default
        render(marker,markerInfoWindow);
        return markerInfoWindow;
    }


}
