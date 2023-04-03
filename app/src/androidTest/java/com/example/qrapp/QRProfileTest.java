package com.example.qrapp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.test.rule.ActivityTestRule;

import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.firestore.GeoPoint;
import com.robotium.solo.Solo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Assert.*;

import java.util.ArrayList;

public class QRProfileTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);


    @Before
    public void setUp() throws Exception {

        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void testProfile() throws Exception {
        solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("Wrong activity", MainActivity.class);
        String searchText = "Alpha BravoBravoJulietGolfIndia";
        ListView listView = (ListView) solo.getView(R.id.item_listview);
        Integer count = listView.getCount();
        for (int i = 0; i < count; i++) {
            View view = listView.getChildAt(i);
            TextView textView = (TextView) view.findViewById(R.id.QRCName);
            if (textView.getText().toString().contains(searchText)) {
                solo.clickOnView(view);
                break;
            }
        }
        solo.waitForActivity(QRProfile.class);
        solo.assertCurrentActivity("Wrong activity", QRProfile.class);
        assertTrue(solo.searchText("Alpha BravoBravoJulietGolfIndia"));
        assertTrue(solo.searchText("31 Points"));
        assertTrue(solo.searchText("C|;<{]"));
    }
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}