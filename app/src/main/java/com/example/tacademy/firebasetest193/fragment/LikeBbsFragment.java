package com.example.tacademy.firebasetest193.fragment;


import android.support.v4.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class LikeBbsFragment extends RootFragment {

    public LikeBbsFragment() {
        // Required empty public constructor
    }

    @Override
    public Query getBbsQuery(DatabaseReference databaseReference) {
        return databaseReference.child("bbs").orderByChild("likeCount");
    }
}
