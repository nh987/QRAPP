package com.example.qrapp;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ScannedByTest {
    private ScannedBy scannedBy;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        scannedBy = mock(ScannedBy.class);
    }

    @Test
    public void testIntent() {
        when(scannedBy.getIntent()).thenReturn(null);
        assertEquals(null, scannedBy.getIntent()); // Man, I hate unit testing activities.
    }
}
