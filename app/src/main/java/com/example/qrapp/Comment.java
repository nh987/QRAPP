package com.example.qrapp;

public class Comment {
    private String authorId;
    private String authorUsername;
    private String commentText;

    /**
     *`Constructor for the Comment class
     * @param authorId The id of the user who wrote the comment
     * @param authorUsername The username of the user who wrote the comment
     * @param commentText The text of the comment
     */
    public Comment(String authorId, String authorUsername, String commentText) {
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.commentText = commentText;
    }

    /**
     * Returns the id of the user who wrote the comment
     * @return The id of the user who wrote the comment
     */
    public String getAuthorId() {
        return authorId;
    }

    /**
     * Sets the id of the user who wrote the comment
     * @param authorId
     */
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    /**
     * Returns the username of the user who wrote the comment
     * @return The username of the user who wrote the comment
     */
    public String getAuthorUsername() {
        return authorUsername;
    }

    /**
     * Sets the username of the user who wrote the comment
     * @param authorUsername The username of the user who wrote the comment
     */
    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    /**
     * Returns the text of the comment
     * @return The text of the comment
     */
    public String getCommentText() {
        return commentText;
    }

    /**
     * Sets the text of the comment
     * @param commentText The text of the comment
     */
    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

}


