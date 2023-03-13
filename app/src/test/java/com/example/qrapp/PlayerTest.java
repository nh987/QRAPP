package com.example.qrapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.Test;

/**
 * Testing Class to test the player object
 * These should always pass, if not theres a serious problem
 */
public class PlayerTest {

    public Player MockPlayer() {
        Player player = new Player(null, null, null, null);
        return player;
    }
    @Test
    public void PlayerSetTest() {
        Player player = MockPlayer();
        player.setEmail("test@test.ca");
        player.setLocation("edmonton");
        player.setPhoneNumber("1234");
        player.setUsername("Steve");
        assertEquals(player.getUsername(), "Steve");
        assertEquals(player.getEmail(), "test@test.ca");
        assertEquals(player.getLocation(), "edmonton");
        assertEquals(player.getPhoneNumber(), "1234");
    }
    @Test
    public void PlayerGetTest() {
        Player player = MockPlayer();
        assertNull(player.getUsername());
        assertNull(player.getEmail());
        assertNull(player.getLocation());
        assertNull(player.getPhoneNumber());
    }

}