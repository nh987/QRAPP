package com.example.qrapp;

import android.content.Context;


import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
/**
 * Test the QRcAdapter
 */
public class QRcAdapterTest {
    private Context context;
    private ArrayList<QRCode> items;
    private QRcAdapter adapter;

    @Before
    /**
     * Set up the adapter
     */
    public void setUp() {
        ArrayList<QRCode> items = new ArrayList<>();
        QRCode qr1 = new QRCode("comments", 10, "test", ":)", null, null, null);
        QRCode qr2 = new QRCode("comments", 100, "test2", ":(", null, null, null);
        items.add(qr1);
        items.add(qr2);

        adapter = new QRcAdapter(items, context);
    }
    /**
     * Test that the adapter is not null
     */
    @Test
    public void testGetCount() {
        assertEquals(2, adapter.getCount());
    }


}
