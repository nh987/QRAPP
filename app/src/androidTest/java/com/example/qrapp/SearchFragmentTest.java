package com.example.qrapp;


import static org.junit.Assert.assertTrue;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

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
        solo.clickOnView(solo.getView(R.id.QRCButton));
        Spinner spinner = (Spinner) solo.getView(R.id.spinner);
        assertTrue(solo.searchText("Juliet CharlieNovemberDeltaBravoDelta"));
        String searchText = "Juliet CharlieNovemberDeltaBravoDelta";
        ListView listView = (ListView) solo.getView(R.id.searchResults);
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
        assertTrue(solo.searchText("Juliet CharlieNovemberDeltaBravoDelta"));
        assertTrue(solo.searchText("22 Points"));
        assertTrue(solo.searchText("[I$,["));
    }

    @Test
    public void testSearchView() throws Exception {
        solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("Wrong activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.search_tab));
        solo.clickOnView(solo.getView(R.id.QRCButton));
        solo.clickOnView(solo.getView(R.id.playerButton));
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) solo.getView(R.id.searchView);
        ImageView searchIcon = (ImageView) searchView.findViewById(androidx.appcompat.R.id.search_button);
        solo.clickOnView(searchIcon);
        solo.typeText(0, "dds");
        solo.sendKey(Solo.ENTER);
        solo.clickOnView(solo.getView(R.id.viewProfile));
        solo.waitForActivity(PlayerProfileActivity.class);
        assertTrue(solo.searchText("dds's Profile"));
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}