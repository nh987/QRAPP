package com.example.qrapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CommentAdapter extends ArrayAdapter<Comment> {
    private Context context;
    private List<Comment> comments;
    private String currentUserId;

    /**
     * Constructor for the CommentAdapter class
     * @param context
     * @param resource
     * @param comments
     * @param currentUserId
     */
    public CommentAdapter(@NonNull Context context, int resource, @NonNull List<Comment> comments, String currentUserId) {
        super(context, resource, comments);
        this.context = context;
        this.comments = comments;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    /**
     * Returns the view for the comment
     * @param position The position of the comment in the list
     * @param convertView The view to be converted
     * @param parent The parent view
     * @return The view for the comment
     */
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View rowView = inflater.inflate(R.layout.item_comment, parent, false);

        TextView authorUsername = rowView.findViewById(R.id.username);
        TextView commentText = rowView.findViewById(R.id.commentText);


        Comment comment = comments.get(position);
        authorUsername.setText(comment.getAuthorUsername());
        commentText.setText(comment.getCommentText());

        return rowView;
    }
}





