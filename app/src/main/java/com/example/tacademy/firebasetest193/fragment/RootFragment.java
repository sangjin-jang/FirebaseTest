package com.example.tacademy.firebasetest193.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tacademy.firebasetest193.R;
import com.example.tacademy.firebasetest193.holder.PostViewHolder;
import com.example.tacademy.firebasetest193.model.Post;
import com.example.tacademy.firebasetest193.ui.BBSActivity;
import com.example.tacademy.firebasetest193.ui.DetailActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

public abstract class RootFragment extends Fragment {
    //리스트를 표현하는 최종 뷰
    //1. RecyclerView <=> ListView
    //2. CustomCell(주로 CardView를 사용한다.) <=> CustomCell
    //3. 데이터
    //4. RecyclerView.Adapter <=> BaseAdapter
    //5. ViewHolder <=> ViewHolder(최적화 때문에 이용)
    //6. 3개의 방향성 (선형, 그리드형, ex확장형(자유도가 높은)) LayoutManager
    RecyclerView recyclerView;
    // 화면 처리를 위한 부분 ===============================================================
    FirebaseRecyclerAdapter<Post, PostViewHolder> adapter;

    public RootFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_total_bbs, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //리사이클러뷰의 방향성 결정
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //리사이클러뷰에 아답터 연결
        adapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(Post.class, R.layout.cell_post_layout, PostViewHolder.class, getBbsQuery(FirebaseDatabase.getInstance().getReference())) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, Post model, int position) {
                // 해당글의 키(데이터의 근본이 되는 줄기값)
                final DatabaseReference key = getRef(position);
                //셀 자체를 클릭하여 상세보기
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        intent.putExtra("key", key.getKey());
                        startActivity(intent);
                    }
                });

                //내가 좋아요 했다면!!
                if (model.getLike().containsKey(getUid())) {
                    viewHolder.likeBtn.setImageResource(android.R.drawable.presence_online);
                } else {
                    viewHolder.likeBtn.setImageResource(android.R.drawable.presence_invisible);
                }

                //데이터를 받아서 뷰 홀더에 세팅해 준다!!
                viewHolder.toBind(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 좋아요 일 때 누르면 초기화 되고 반대면은 좋아요가 된다.
                        //1. 기둥 획득
                        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
                        //2. 업데이트 대상
                        DatabaseReference target = root.child("bbs").child(key.getKey());
                        updateLikeState(target);

                        DatabaseReference target1 = root.child("mybbs").child(getUid()).child(key.getKey());
                        updateLikeState(target1);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    public String getUid() {
        return ((BBSActivity) getActivity()).getUser().getUid();
    }

    public void updateLikeState(DatabaseReference target) {
        //해당 줄기에 트랜잭션을 건다.
        target.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                //데이터 획득
                Post post = mutableData.getValue(Post.class);
                if (post == null) return Transaction.success(mutableData);
                //수정
                //이글을 내가 좋아요를 한적이 있는가
                String uid = getUid();
                //값 변경
                if (post.getLike().containsKey(uid)) {
                    post.getLike().remove(uid);
                    post.setLikeCount(post.getLikeCount() - 1);
                } else {
                    post.getLike().put(uid, true);
                    post.setLikeCount(post.getLikeCount() + 1);
                }
                //데이터 다시 설정
                mutableData.setValue(post);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                //트랜잭션 해제(처리 완료)
                if (databaseError == null) {
                    Toast.makeText(getActivity(), "좋아요 추가 성공", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "좋아요 추가 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public abstract Query getBbsQuery(DatabaseReference databaseReference);
}

