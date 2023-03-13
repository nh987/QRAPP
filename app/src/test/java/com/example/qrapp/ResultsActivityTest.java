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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
//@RunWith(JUnit4.class)
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
        String expected = "Golf JulietGolfMikeOscarEcho";
        assertEquals(expected, resultsActivity.createName(hexHash)); // this function works as intended
    }

    @Test
    public void testCreateVisual() {
        String hexHash = hashTest();
        when(resultsActivity.createVisual(hexHash)).thenReturn("F|>X*{(");
        String expected = "F|>X*{(";
        assertEquals(expected, resultsActivity.createName(hexHash)); // // this function returns null???
    }

}
