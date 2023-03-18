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

@RunWith(JUnit4.class)
public class ScanActivityTest{
    private ScanActivity scanActivity;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        scanActivity = mock(ScanActivity.class);
    }
    private String hashTest() {
        String hash = "696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6";
        return hash;
    }

    @Test
    public void testScore() {
        String hexHash = hashTest();
        when(scanActivity.score(hexHash)).thenReturn((long) 115);
        assertEquals((long) 115, scanActivity.score(hexHash));
    }
}


