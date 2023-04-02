package com.example.qrapp;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import android.content.Context;
import org.junit.Test;

import java.util.ArrayList;


/**
 * The RankTripleTest class is used to test the RankTriple and RankTripleAdapter class.
 */
public class RankTripleTest {
    public Context context;

    public ArrayList<RankTriple> items;
    public RankTripleAdapter adapter;

    /**
     * Set up the adapter
     */
    @Before
    public void setUpAdapter(){
        ArrayList<RankTriple> items = new ArrayList<>();
        RankTriple rankTriple1 = new RankTriple("test", ":)", 100);
        RankTriple rankTriple2 = new RankTriple("test2", ":^)", 200);
        items.add(rankTriple1);
        items.add(rankTriple2);

        adapter = new RankTripleAdapter(items, context);
    }

    /**
     * Test that the adapter returns the correct values
     */
    @Test
    public void testRankTripleValues(){
        assertEquals("test", adapter.getItem(0).getPlayerName());
        assertEquals("test2", adapter.getItem(1).getPlayerName());
    }

    /**
     * Test the adapter getCount method
     */
    @Test
    public void testGetCount() {
        assertEquals(2, adapter.getCount());
    }

    /**
     * Test the kthLargest method
     */
    @Test
    public void testKthLargestTriple() {
        ArrayList<RankTriple> items = new ArrayList<>();
        RankTriple rankTriple1 = new RankTriple("test", ";^)", 350);
        RankTriple rankTriple2 = new RankTriple("test2", ":}", 200);
        RankTriple rankTriple3 = new RankTriple("test3", ":,(", 275);
        items.add(rankTriple1);
        items.add(rankTriple2);
        items.add(rankTriple3);
        adapter = new RankTripleAdapter(items, context);

        RankTriple largest = com.example.qrapp.RankFragment.kthLargestTriple(items, 1);
        assertEquals(";^)", largest.getQRcFace());
    }
}