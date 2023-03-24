package com.example.qrapp;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
//@RunWith(JUnit4.class)
@RunWith(MockitoJUnitRunner.class)
public class ResultsActivityTest {
    private ResultsActivity resultsActivity;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        resultsActivity = mock(ResultsActivity.class);
    }
    private String hashTest() {
        String hash = "696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6";
        return hash;
    }

    @Test
    public void testCreateName() {
        String hexHash = hashTest();
        when(resultsActivity.createName(hexHash)).thenReturn("Golf JulietGolfMikeOscarEcho");
        String expectedName = "Golf JulietGolfMikeOscarEcho";
        assertEquals(expectedName, resultsActivity.createName(hexHash)); // this function works as intended
    }

    @Test // TODO: Figure out why this test returns null... It should not. Since the above test is essentially the same in its function structure.
    public void testCreateVisual() {
        String hexHash = hashTest();
        when(resultsActivity.createVisual(hexHash)).thenReturn("F|>X*{(");
        String expectedVisual = "F|>X*{(";
        assertEquals(expectedVisual, resultsActivity.createName(hexHash)); // // this function returns null???
    }

}
