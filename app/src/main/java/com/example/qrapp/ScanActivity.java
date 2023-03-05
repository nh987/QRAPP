package com.example.qrapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ScanActivity extends AppCompatActivity {

    ImageButton BACK_ARROW;
    FloatingActionButton PHOTO;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        BACK_ARROW = findViewById(R.id.button_back_scan);
        PHOTO = findViewById(R.id.button_take_photo);

        //go back to main activity
        BACK_ARROW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //take a photo(not scan)
        PHOTO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //take photo if allowed
            }
        });


    }
}
