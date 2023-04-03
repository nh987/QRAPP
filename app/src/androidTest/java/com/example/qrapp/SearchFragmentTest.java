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
        solo.assertCurrentActivity("Wrong activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.search_tab));
        solo.waitForActivity(String.valueOf(SearchFragment.class));
        solo.clickOnView(solo.getView(R.id.QRCButton));
        solo.sleep(7000);
        Spinner spinner = (Spinner) solo.getView(R.id.spinner);
        ListView listView = (ListView) solo.getView(R.id.searchResults);
        solo.clickInList(0);
        solo.waitForActivity(QRProfile.class);
        solo.assertCurrentActivity("Wrong activity", QRProfile.class);
    }

    @Test
    public void testSearchView() throws Exception {
        solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("Wrong activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.search_tab));
        solo.waitForActivity(String.valueOf(SearchFragment.class));
        solo.clickOnView(solo.getView(R.id.QRCButton));
        solo.clickOnView(solo.getView(R.id.playerButton));
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) solo.getView(R.id.searchView);
        ImageView searchIcon = (ImageView) searchView.findViewById(androidx.appcompat.R.id.search_button);
        solo.clickOnView(searchIcon);
        solo.typeText(0, "mo_ketchum");
        solo.sendKey(Solo.ENTER);
        solo.sleep(6000);
        solo.clickOnView(solo.getView(R.id.viewProfile));
        solo.waitForActivity(PlayerProfileActivity.class);
        assertTrue(solo.searchText("mo_ketchum"));
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}