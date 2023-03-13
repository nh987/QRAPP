package com.example.qrapp;

import android.app.Activity;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import android.widget.EditText;
import android.widget.ListView;
import com.robotium.solo.Solo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SignUpActivityTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<SignUpActivity> rule =
            new ActivityTestRule<>(SignUpActivity.class, true, true);


    @Before
    public void setUp() throws Exception {

        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }


    @Test
    public void testSignUp() throws Exception {
        solo.waitForActivity(SignUpActivity.class);
        solo.assertCurrentActivity("Wrong activity", SignUpActivity.class);

        //Enter valid credentials and signup
        solo.enterText((EditText) solo.getView(R.id.username), "testuser");
        solo.enterText((EditText) solo.getView(R.id.email_field), "testuser@example.com");
        solo.enterText((EditText) solo.getView(R.id.password_field), "password123");
        solo.clickOnButton("Sign up");

        //Check if toast message appears and the user is navigated to the main activity
        assertTrue(solo.waitForText("Sign up successful", 1, 2000));
        solo.assertCurrentActivity("Wrong activity", MainActivity.class);
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}