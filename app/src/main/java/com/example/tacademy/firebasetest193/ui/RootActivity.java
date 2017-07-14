package com.example.tacademy.firebasetest193.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Tacademy on 2017-07-05.
 */

public class RootActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    ProgressDialog pd;

    public void showPD(){
        if(pd==null){
            pd = new ProgressDialog(this);
            pd.setCancelable(false);
            pd.setMessage("...loaidng....");
        }
        pd.show();
    }

    public void stopPD(){
        if( pd!=null && pd.isShowing()){
            pd.dismiss();
        }
    }
    //현재 로그인한 유저의 fb 유저 정보
    public FirebaseUser getUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public void signOut(){ FirebaseAuth.getInstance().signOut();}
}
