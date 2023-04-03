package com.example.qrapp;

import android.app.Activity;
import android.view.View;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MyProfileWorkflowTests {

    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
       new ActivityTestRule<>(MainActivity.class, true, true);


    @Before
    public void setUp() throws Exception{

        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }


    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();

        //confirm on the main activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    @Test
    // try to navigate to my profile page
    public void navigateToMyProfile() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.button_MYprofile));
        solo.assertCurrentActivity("Wrong Activity", MyProfile.class);
    }

    //navigate to my profile page and check if user data loads
    @Test
    public void checkLoading() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.button_MYprofile));
        solo.assertCurrentActivity("Wrong Activity", MyProfile.class);
        // wait a few seconds for the data to load, then check to make sure no fields are the string value of the loading message

        //assert we haven't been kicked back to the main activity by an error
        solo.assertCurrentActivity("Wrong Activity", MyProfile.class);

        String loading = solo.getString(R.string.loading); // get the xml string resource for "loading"
        solo.sleep(5000); // wait 5 seconds for the data to load
        assertFalse(solo.searchText(loading));

    }

    //test viewing the users highest scoring qr code
    @Test
    public void viewHighestScoringQR() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.button_MYprofile));
        solo.assertCurrentActivity("Wrong Activity", MyProfile.class);
        solo.clickOnView(solo.getView(R.id.viewHighestQRCButton));

        solo.sleep(5000); // wait 5 seconds for the data to load

        // if the viewHighestQRCButton is visible, test its navigation
        if (solo.getView(R.id.viewHighestQRCButton).getVisibility() == View.VISIBLE) {
            solo.clickOnView(solo.getView(R.id.viewHighestQRCButton));
            solo.assertCurrentActivity("Wrong Activity", QRProfile.class);
        }
    }

    // test viewing the users lowest scoring qr code
    @Test
    public void viewLowestScoringQR() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.button_MYprofile));
        solo.assertCurrentActivity("Wrong Activity", MyProfile.class);
        solo.clickOnView(solo.getView(R.id.viewLowestQRCButton));

        solo.sleep(5000); // wait 5 seconds for the data to load

        // if the viewLowestQRCButton is visible, test its navigation
        if (solo.getView(R.id.viewLowestQRCButton).getVisibility() == View.VISIBLE) {
            solo.clickOnView(solo.getView(R.id.viewLowestQRCButton));
            solo.assertCurrentActivity("Wrong Activity", QRProfile.class);
        }
    }

    @Test
    public void testBackButton(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.button_MYprofile));
        solo.assertCurrentActivity("Wrong Activity", MyProfile.class);
        solo.clickOnView(solo.getView(R.id.back));
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    @Test
    public void testViewQR(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.button_MYprofile));
        solo.assertCurrentActivity("Wrong Activity", MyProfile.class);
        solo.clickOnView(solo.getView(R.id.myQRCbutton));
        solo.assertCurrentActivity("Wrong Activity", ViewPlayerScannedQRActivity.class);
    }
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }


}
