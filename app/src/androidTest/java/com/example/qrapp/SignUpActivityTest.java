package com.example.qrapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertNotNull;

import android.provider.ContactsContract;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SignUpActivityTest {

    private ActivityScenario<SignUpActivity> scenario;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(SignUpActivity.class);
    }

    @Test
    public void testActivityCreation() {
        // Verify that the activity is created
        scenario.onActivity(activity -> assertNotNull(activity));
    }

    @Test
    public void switchToMainActivityUsingSignUpButton() {
        Intents.init();

        getInstrumentation().waitForIdleSync();

        scenario.onActivity(activity -> {});

        onView(withId(R.id.signup_button)).perform(click());

        Intents.intended(hasComponent(MainActivity.class.getCanonicalName()));
        Intents.release();
    }

    @After
    public void tearDown() {
        scenario.close();
    }
}
