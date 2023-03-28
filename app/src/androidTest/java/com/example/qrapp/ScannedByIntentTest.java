package com.example.qrapp;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ScannedByIntentTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);


    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }


    @Test
    public void testScannedBy() throws Exception {
        solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("Wrong activity", MainActivity.class);

        solo.clickInList(0);
        solo.waitForActivity("QRProfile",1000);
        solo.assertCurrentActivity("Wrong Activity", QRProfile.class);

        solo.clickOnView(solo.getView(R.id.qrprofile_players_btn));
        solo.waitForActivity("ScannedBy",1000);
        solo.assertCurrentActivity("Wrong Activity", ScannedBy.class);

        solo.clickOnView(solo.getView(R.id.viewProfile));
        solo.waitForActivity("PlayerProfileActivity",1000);
        solo.assertCurrentActivity("Wrong Activity", PlayerProfileActivity.class);

        solo.clickOnView(solo.getView(R.id.back));
        solo.clickOnView(solo.getView(R.id.scannedby_back));
        solo.clickOnView(solo.getView(R.id.back));
        solo.waitForActivity("MainActivity",1000);
        solo.assertCurrentActivity("Wrong activity", MainActivity.class);
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}

