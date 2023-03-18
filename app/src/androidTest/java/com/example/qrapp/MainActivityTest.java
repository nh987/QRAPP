package com.example.qrapp;

import android.app.Activity;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.robotium.solo.Solo;

import org.checkerframework.common.subtyping.qual.Bottom;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test Navigation from the main activity to other activities
 */
public class MainActivityTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs beofer all tests
     * Create Solo Instance
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        //creates solo object with instrumentation and activity as args
        //Instrumentaion: allows test case to programically control UI and access key events
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public  void  start() throws  Exception{//gets the MainActivity
        Activity activity = rule.getActivity();
    }


    /**
     * Test Navigation to Scan page
     */
    @Test
    public void toScanActivity(){

        //Asserts current activity in MainActivity, otherwise show wrong activity.
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.clickOnView(solo.getView(R.id.button_scan));
        solo.waitForActivity("ScanActivity",5000);
        solo.assertCurrentActivity("Wrong Activity 2", ScanActivity.class);
    }

    /**
     * Test navigation to MYProfile page
     */
    @Test
    public void toMYProfileActivity(){

        //Asserts current activity in MainActivity, otherwise show wrong activity.
        solo.assertCurrentActivity("Wrong Activity3", MainActivity.class);

        solo.clickOnView(solo.getView(R.id.button_MYprofile));
        solo.waitForActivity("ScanActivity",5000);
        solo.assertCurrentActivity("Wrong Activity 4", MyProfile.class);

    }
}
