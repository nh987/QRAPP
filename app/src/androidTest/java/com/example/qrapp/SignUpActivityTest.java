package com.example.qrapp;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import android.widget.EditText;
import com.robotium.solo.Solo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


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

        //Enter valid credentials and signup -- uses random id so test can reoccur
        solo.enterText((EditText) solo.getView(R.id.username), "MyTestUser" + System.currentTimeMillis());
        solo.enterText((EditText) solo.getView(R.id.email_field), "test1234" + System.currentTimeMillis() + "@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.password_field), "password123!!");
        solo.clickOnButton("Sign Up");

        //Check if the user is logged in
        solo.waitForActivity(MainActivity.class);
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
