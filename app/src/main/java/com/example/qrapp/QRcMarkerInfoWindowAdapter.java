package com.example.qrapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
        String QRc_title = marker.getTitle();
        TextView textView_QRcTitle = view.findViewById(R.id.custom_title);
        if(!QRc_title.equals("")){textView_QRcTitle.setText(QRc_title);}

        String QRc_snippet = marker.getSnippet();
        TextView textView_QRcSnippet = view.findViewById(R.id.custom_snippet);
        if(!QRc_snippet.equals("")){textView_QRcSnippet.setText(QRc_snippet);}
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
