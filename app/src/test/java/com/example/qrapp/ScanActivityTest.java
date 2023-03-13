package com.example.qrapp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ScanActivityTest extends ScanActivity {
    private String hashTest () {
        String hash = "696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6";
        return hash;
    }

    @Test
    void testScore() {
        hashed = hashTest();
        assertEquals(score(hashed), 115);
    }
}


