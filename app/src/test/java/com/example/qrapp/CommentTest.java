package com.example.qrapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import android.location.Location;

import com.google.firebase.firestore.GeoPoint;

import org.junit.Test;

/**
 * Test the Comment class
 */
public class CommentTest {
    private Comment comment;

    public Comment mockComment() {
        Comment comment = new Comment("1", "user1", "This is a test comment");
        return comment;
    }

    @Test
    public void testGetAuthorId() {
        Comment comment = mockComment();
        assertEquals("1", comment.getAuthorId());
    }

    @Test
    public void testSetAuthorId() {
        Comment comment = mockComment();
        comment.setAuthorId("2");
        assertEquals("2", comment.getAuthorId());
    }

    @Test
    public void testGetAuthorUsername() {
        Comment comment = mockComment();
        assertEquals("user1", comment.getAuthorUsername());
    }

    @Test
    public void testSetAuthorUsername() {
        Comment comment = mockComment();
        comment.setAuthorUsername("user2");
        assertEquals("user2", comment.getAuthorUsername());
    }

    @Test
    public void testGetCommentText() {
        Comment comment = mockComment();
        assertEquals("This is a test comment", comment.getCommentText());
    }

    @Test
    public void testSetCommentText() {
        Comment comment = mockComment();
        comment.setCommentText("This is a new comment");
        assertEquals("This is a new comment", comment.getCommentText());
    }
}

