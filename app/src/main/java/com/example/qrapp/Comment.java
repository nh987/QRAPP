package com.example.qrapp;

public class Comment {
    private String authorId;
    private String authorUsername;
    private String commentText;

    public Comment(String authorId, String authorUsername, String commentText) {
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.commentText = commentText;
    }

    // Add the getter for the authorId field
    public String getAuthorId() {
        return authorId;
    }

    // Add the setter for the authorId field
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    // Add the getter for the authorUsername field
    public String getAuthorUsername() {
        return authorUsername;
    }

    // Add the setter for the authorUsername field
    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    // Add the getter for the commentText field
    public String getCommentText() {
        return commentText;
    }

    // Add the setter for the commentText field
    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

}


