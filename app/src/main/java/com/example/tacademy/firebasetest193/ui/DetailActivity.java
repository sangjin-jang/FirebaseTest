package com.example.tacademy.firebasetest193.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tacademy.firebasetest193.R;
import com.example.tacademy.firebasetest193.holder.CommentViewHolder;
import com.example.tacademy.firebasetest193.model.Comment;
import com.example.tacademy.firebasetest193.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailActivity extends RootActivity {
    EditText comments;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Comment, CommentViewHolder> adapter;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        key = getIntent().getStringExtra("key");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        comments = (EditText) findViewById(R.id.comments);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //리사이클러뷰의 방향성 결정
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(Comment.class, R.layout.cell_comment_layout, CommentViewHolder.class, FirebaseDatabase.getInstance().getReference()
                .child("comments").child(key).orderByChild("regdate")) {
            @Override
            protected void populateViewHolder(CommentViewHolder viewHolder, Comment model, int position) {
                viewHolder.toBind(model);
            }
        };

        recyclerView.setAdapter(adapter);
    }

    public void onSendComments(View view) {
        if (!isValid()) return;

        showPD();
        final Comment comment = new Comment(getUser().getEmail().split("@")[0], comments.getText().toString(), System.currentTimeMillis());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(getUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user == null){
                    Toast.makeText(DetailActivity.this, "회원이 아닙니다.", Toast.LENGTH_SHORT).show();
                    stopPD();
                } else {
                        FirebaseDatabase.getInstance().getReference().child("comments").child(key).push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(DetailActivity.this, "댓글이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                                comments.setText("");
                                stopPD();
                            }
                        });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DetailActivity.this, "댓글이 등록에 실패했습니다."+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                comments.setText("");
                stopPD();
            }
        });

        /*FirebaseDatabase.getInstance().getReference().child("comments").child(key).push().setValue(comment, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Toast.makeText(DetailActivity.this, "댓글이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailActivity.this, "댓글이 등록에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
                comments.setText("");
                stopPD();
            }
        });*/
    }

    public boolean isValid() {
        String comments = this.comments.getText().toString();

        if (TextUtils.isEmpty(comments)) {
            this.comments.setError("댓글을 입력해주세요.");
            return false;
        }

        return true;
    }
}
