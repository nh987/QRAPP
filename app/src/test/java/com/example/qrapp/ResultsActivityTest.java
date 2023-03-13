package com.example.qrapp;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ResultsActivityTest{
//    @Rule
//    public TestRule rule = new InstantTaskExecutorRule();

    private String hashTest() {
        String hash = "696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6";
        return hash;
    }

    @Test
    public void testCreateName() {
        String hexHash = hashTest();
        assertEquals(new ResultsActivity().createName(hexHash), "Golf JulietGolfMikeOscarEcho");
    }

    @Test
    public void testCreateVisual() {
        String hexHash = hashTest();
        assertEquals(new ResultsActivity().createVisual(hexHash), "F|>X*{(");
    }
}
