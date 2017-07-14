package com.example.tacademy.firebasetest193.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tacademy.firebasetest193.R;
import com.example.tacademy.firebasetest193.model.User;
import com.example.tacademy.firebasetest193.util.U;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends RootActivity {
    FirebaseAuth firebaseAuth;
    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //익명 로그인한 유저 객체 획득 -> 로그아웃을 하지 않았다면 객체(세션) 획득 가능
        /*if(getUser() != null){
            goChatService();
        }*/
    }

    public void goChatService(){
        //startActivity(new Intent(this, SimpleChatActivity.class));
        startActivity(new Intent(this, BBSActivity.class));
    }

    public void initUI() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //익명 로그인을 수행하라
                if (getUser() != null) {
                    firebaseAuth.signOut();
                    Toast.makeText(LoginActivity.this, "로그아웃 성공", Toast.LENGTH_SHORT).show();
                } else {
                    anonymouslySignup();
                }
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        //버튼 이벤트
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAnLinkEmail();
            }
        });
        //이메일로 로그인
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEmailSignup();
            }
        });
        //이메일로 가입
        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEmailJoin();
            }
        });

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
    }

    public void anonymouslySignup() {
        showPD();
        //익명 로그인을 진행하고 그 결과를 비동기적으로 받아서 결과를 리스너 객체를 통해 전달한다.
        firebaseAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = getUser();

                    if (user != null) {
                        U.getInstance().log(user.getUid());
                        U.getInstance().log(user.getEmail());
                    }
                    Toast.makeText(LoginActivity.this, "익명계정 생성 성공", Toast.LENGTH_SHORT).show();
                    //goChatService();

                } else {
                    Toast.makeText(LoginActivity.this, "익명계정 생성 실패", Toast.LENGTH_SHORT).show();
                }
                stopPD();
            }
        });
    }

    //익명계정으로부터 이메일 전환 관련
    //1. 이메일, 비번 입력
    //2. 유효성 검사(TextUtil.isEmpty(), EditText.setError)
    //3. FB Auth 중 메소드를 통해 처리!!
    public void onAnLinkEmail() {
        if (!isValid()) return;
        //익명계정과 이메일 비번을 연결하는 구간
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();
        getUser().linkWithCredential(EmailAuthProvider.getCredential(email, password))
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            U.getInstance().log("연결 완료");
                            Toast.makeText(LoginActivity.this, "이메일 연결 성공", Toast.LENGTH_SHORT).show();
                            goChatService();
                        } else {
                            //실패사유 보여주기
                            Toast.makeText(LoginActivity.this, "이메일 연결 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    //이메일 비번으로 부터 가입 및 로그인
    //1. 이메일, 비번 입력
    //2. 유효성 검사(TextUtil.isEmpty(), EditText.setError)
    //3. FB Auth 중 메소드를 통해 처리!!
    public void onEmailSignup() {
        if (!isValid()) return;

        //1. 로그아웃
        //2. 이메일, 비번 입력 후 로그인 버튼 하나 추가하여
        //3. 클릭시 아래 함수를 호출하여 로그인되게 처리
        showPD();
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "이메일 로그인 성공", Toast.LENGTH_SHORT).show();
                    goChatService();
                } else {
                    Toast.makeText(LoginActivity.this, "이메일 로그인 실패", Toast.LENGTH_SHORT).show();
                }

                stopPD();
            }
        });
    }

    public void onEmailJoin(){
        if (!isValid()) return;

        showPD();
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();
        // 이메일 비번으로 회원가입
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //실시간 디비에 회원 정보 삽입
                    insertUserInfo();
                    /*getUser().sendEmailVerification().addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "정상 이메일 가입 성공", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "비정상 이메일", Toast.LENGTH_SHORT).show();
                            }

                            stopPD();
                        }
                    });*/
                } else {
                    Toast.makeText(LoginActivity.this, "가입 실패"+task.getResult().toString(), Toast.LENGTH_SHORT).show();
                    stopPD();
                }
            }
        });
    }

    public boolean isValid() {
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();

        if (TextUtils.isEmpty(email)) {
            this.email.setError("이메일을 입력해주세요.");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            this.password.setError("비밀번호를 입력해주세요.");
            return false;
        }

        return true;
    }

    public void insertUserInfo(){
        // 1. 그릇에 데이터 담기
        User user = new User(getUser().getEmail(), getUser().getEmail().split("@")[0], "", "", System.currentTimeMillis());
        // 2. 디비 경로에 맞춰서 경로를 정의하고 데이터를 추가
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();  // => /
        databaseReference.child("users").child(getUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // 3. 성공과 실패 여부에 따라 이동
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "회원가입 및 로그인 성공", Toast.LENGTH_SHORT).show();
                    goChatService();
                }else{

                }
            }
        });
    }
}
