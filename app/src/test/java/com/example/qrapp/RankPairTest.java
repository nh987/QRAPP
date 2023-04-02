package com.example.qrapp;

import static com.example.qrapp.RankFragment.kthLargestPair;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import android.content.Context;
import org.junit.Test;

import java.util.ArrayList;


/**
 * The RankPairTest class is used to test the RankPair and RankPairAdapter class.
 */
public class RankPairTest {
    public Context context;
    public ArrayList<RankPair> items;
    public RankPairAdapter adapter;

    /**
     * Set up the adapter
     */
    @Before
    public void setUpAdapter(){
        ArrayList<RankPair> items = new ArrayList<>();
        RankPair rankPair1 = new RankPair("test", 100);
        RankPair rankPair2 = new RankPair("test2", 200);
        items.add(rankPair1);
        items.add(rankPair2);

        adapter = new RankPairAdapter(items, context);
    }


    /**
     * Test that the adapter returns the correct values
     */
    @Test
    public void testRankPairValues(){
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
    public void testKthLargestPair() {
        ArrayList<RankPair> items = new ArrayList<>();
        RankPair rankPair1 = new RankPair("test", 150);
        RankPair rankPair2 = new RankPair("test2", 200);
        RankPair rankPair3 = new RankPair("test3", 175);
        items.add(rankPair1);
        items.add(rankPair2);
        items.add(rankPair3);
        adapter = new RankPairAdapter(items, context);

        RankPair largest = com.example.qrapp.RankFragment.kthLargestPair(items, 1);
        assertEquals("test2", largest.getPlayerName());
    }
}
