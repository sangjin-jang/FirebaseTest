package com.example.tacademy.firebasetest193.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tacademy.firebasetest193.R;
import com.example.tacademy.firebasetest193.model.Post;
import com.example.tacademy.firebasetest193.model.User;
import com.example.tacademy.firebasetest193.util.U;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends RootActivity {
    EditText postTitle, postContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 3. 사용자가 유효한가 체크 => 사용자들이 존재하는 가지에서 존재 여부를 검사 => 선행으로 유저 정보 저장 필요
                // 값 검사용 리스너 => addListenerForSingleValueEvent
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("users").child(getUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //검사 결과를 받아서 해당 값을 확인할 수 있는 그릇에 담았다.
                        User user = dataSnapshot.getValue(User.class);

                        if(user == null){// 회원이 아니다.
                            Toast.makeText(NewPostActivity.this, "회원이 아닙니다. 로그인 혹은 가입 후 이용해주세요.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // 사용자가 작성한 글을 게시판에 업로드 한다. (실시간 디비)
                            submit();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        postTitle = (EditText) findViewById(R.id.postTitle);
        postContent = (EditText) findViewById(R.id.postContent);
    }

    public void submit(){
        // 0. 사전 준비 : 유저 정보를 담는 그릇과 글 자체를 담는 그릇 설계가 필요하다.
        // 1. 작성내용 검사
        if(!isValid()) return;
        showPD();
        // 2. 그릇에 담는다.
        Post post = new Post(this.postTitle.getText().toString(), this.postContent.getText().toString(), getUser().getEmail().split("@")[0], getUser().getEmail());
        // 넣고자 하는 데이터가 2곳 이상이라면 setValue 보다는 update 개념으로 처리하는게 좀 더 낫다.
        // => 채팅 채널상에서 보이는 최신글과 채팅 안에서 보이는 최신글이 항상 일치하는 것이 대표적인 케이스이다.
        //
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        // => 키획득
        String key = databaseReference.child("bbs").push().getKey();
        //데이터를 쉽게 넣기 위해 map으로 획득
        Map<String, Object> data= post.toMap();
        //업데이트 할 내용들을 생성
        Map<String, Object> updates = new HashMap<>();
        updates.put("/bbs/"+key, data); //전체 게시물에 내용 추가
        updates.put("/mybbs/"+getUser().getUid()+"/"+key, data); //내가 쓴 글에 내용 추가
        //업데이트
        databaseReference.updateChildren(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null) {
                    U.getInstance().log(databaseError.toString());
                } else {
                    Toast.makeText(NewPostActivity.this, "글 쓰기 성공", Toast.LENGTH_SHORT).show();
                    finish();
                }
                stopPD();
            }
        });

        // 4. 디비에 추가 혹은 업데이트 혹은 전송
    }

    public boolean isValid() {
        String postTitle = this.postTitle.getText().toString();
        String postContent = this.postContent.getText().toString();

        if (TextUtils.isEmpty(postTitle)) {
            this.postTitle.setError("이메일을 입력해주세요.");
            return false;
        }
        if (TextUtils.isEmpty(postContent)) {
            this.postContent.setError("비밀번호를 입력해주세요.");
            return false;
        }

        return true;
    }
}
