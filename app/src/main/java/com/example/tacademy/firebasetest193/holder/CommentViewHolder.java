package com.example.tacademy.firebasetest193.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.tacademy.firebasetest193.R;
import com.example.tacademy.firebasetest193.model.Comment;

/**
 * Created by Tacademy on 2017-07-13.
 */

public class CommentViewHolder extends RecyclerView.ViewHolder {
    TextView nickname, comments;

    public CommentViewHolder(View itemView) {
        super(itemView);

        nickname = itemView.findViewById(R.id.nickname);
        comments = itemView.findViewById(R.id.comments);
    }

    public void toBind(Comment comment){
        nickname.setText(comment.getNickname());
        comments.setText(comment.getComment());
    }
}
