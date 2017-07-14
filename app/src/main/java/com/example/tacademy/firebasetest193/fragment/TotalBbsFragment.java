package com.example.tacademy.firebasetest193.fragment;


import android.support.v4.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class TotalBbsFragment extends RootFragment {

    public TotalBbsFragment() {
        // Required empty public constructor
    }

    @Override
    public Query getBbsQuery(DatabaseReference databaseReference) {
        return databaseReference.child("bbs").limitToFirst(100);
    }
}
