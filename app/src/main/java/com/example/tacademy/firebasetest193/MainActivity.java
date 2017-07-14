package com.example.tacademy.firebasetest193;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.tacademy.firebasetest193.ui.LoginActivity;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.i("FCM","onTokenRefresh : "+ refreshedToken);

        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
}
