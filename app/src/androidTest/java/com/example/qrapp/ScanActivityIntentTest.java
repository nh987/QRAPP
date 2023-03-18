package com.example.qrapp;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import android.widget.EditText;
import com.robotium.solo.Solo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

// This only tests the back button functionality because you need to manually scan in a barcode
// to proceed this workflow. In a future sprint maybe I will continue this workflow by standing by
// having the app stand by for the QRC.
public class ScanActivityIntentTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<ScanActivity> rule =
            new ActivityTestRule<>(ScanActivity.class, true, true);


    @Before
    public void setUp() throws Exception {

        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }


    @Test
    public void testBackButton() throws Exception {
        solo.waitForActivity(ScanActivity.class);
        solo.assertCurrentActivity("Wrong activity", ScanActivity.class);

        // Wait 5 seconds for camera to initialize
        solo.sleep(5000);

        // Click on back button and return to MainActivity
        solo.clickOnImage(0);
        solo.waitForActivity(MainActivity.class);
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
