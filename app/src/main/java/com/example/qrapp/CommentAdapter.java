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

    public CommentAdapter(@NonNull Context context, int resource, @NonNull List<Comment> comments, String currentUserId) {
        super(context, resource, comments);
        this.context = context;
        this.comments = comments;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View rowView = inflater.inflate(R.layout.item_comment, parent, false);

        TextView authorUsername = rowView.findViewById(R.id.username);
        TextView commentText = rowView.findViewById(R.id.commentText);
        EditText commentEditText = rowView.findViewById(R.id.commentEditText);
        ImageButton editText = rowView.findViewById(R.id.editButton);

        Comment comment = comments.get(position);
        authorUsername.setText(comment.getAuthorUsername());
        commentText.setText(comment.getCommentText());
        if (comment.getAuthorId().equals(currentUserId)) {
            editText.setVisibility(View.VISIBLE);
        } else {
            editText.setVisibility(View.GONE);
        }

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentText.setVisibility(View.GONE);
                commentEditText.setVisibility(View.VISIBLE);
                commentEditText.setText(comment.getCommentText());
            }
        });

        // ALSO UPDATE THE CHANGE IN THE DATABASE

        return rowView;
    }
}



