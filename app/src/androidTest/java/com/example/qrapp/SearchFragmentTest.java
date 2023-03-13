package com.example.qrapp;


import static org.junit.Assert.assertTrue;

import android.widget.Spinner;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SearchFragmentTest {
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
        // Check if we are on main activity
        solo.assertCurrentActivity("Wrong activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.search_tab));
        // Make sure we are now in the search fragment
        solo.waitForActivity(String.valueOf(SearchFragment.class));
        // Click on the search button
        solo.clickOnView(solo.getView(R.id.button2));
        Spinner spinner = (Spinner) solo.getView(R.id.spinner);
        assertTrue(solo.searchText("Juliet CharlieNovemberDeltaBravoDelta"));
        solo.clickInList(1);
        solo.waitForActivity(QRProfile.class);
        solo.assertCurrentActivity("Wrong activity", QRProfile.class);
        assertTrue(solo.searchText("Juliet CharlieNovemberDeltaBravoDelta"));
        assertTrue(solo.searchText("22 Points"));
        assertTrue(solo.searchText("[I$,["));
    }
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}