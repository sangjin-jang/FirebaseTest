package com.example.tacademy.firebasetest193.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tacademy.firebasetest193.R;
import com.example.tacademy.firebasetest193.model.Post;

/**
 * RecyclerView의 구성요소 중 커스텀셀과 연관되어 셀의 구성원을 멤버로 가지고 데이터 멤버에 설정하는 역할을 담당한다.
 */

public class PostViewHolder extends RecyclerView.ViewHolder {
    public ImageView profile;
    public TextView nickname, likeCount, title, content;
    public ImageButton likeBtn;

    //커스텀셀 객체를 만들어서 생성자에 전달된다.
    public PostViewHolder(View itemView) {
        super(itemView);

        profile = itemView.findViewById(R.id.profile);
        nickname = itemView.findViewById(R.id.nickname);
        likeCount = itemView.findViewById(R.id.likeCount);
        title = itemView.findViewById(R.id.title);
        content = itemView.findViewById(R.id.content);
        likeBtn = itemView.findViewById(R.id.likeBtn);
    }

    //데이터를 뷰에 설정
    public void toBind(Post post, View.OnClickListener clickListener){
        nickname.setText(post.getEmail().split("@")[0]);
        likeCount.setText(""+post.getLikeCount());
        title.setText(post.getTitle());
        content.setText(post.getContent());

        likeBtn.setOnClickListener(clickListener);
    }
}
