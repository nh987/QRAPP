package com.example.qrapp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.widget.Spinner;

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
    public void testSearchButton() throws Exception {
        solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("Wrong activity", MainActivity.class);
        solo.clickInList(1);
        solo.waitForActivity(QRProfile.class);
        solo.assertCurrentActivity("Wrong activity", QRProfile.class);
        assertTrue(solo.searchText("Alpha EchoCharlieEchoJulietHotel"));
        assertTrue(solo.searchText("0 Points"));
        assertTrue(solo.searchText("C|X>)"));
    }
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}