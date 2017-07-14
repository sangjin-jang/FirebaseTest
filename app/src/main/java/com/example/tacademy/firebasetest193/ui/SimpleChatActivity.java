package com.example.tacademy.firebasetest193.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tacademy.firebasetest193.R;
import com.example.tacademy.firebasetest193.model.ChatModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SimpleChatActivity extends RootActivity {
    ListView listView;
    EditText chatInput;
    LayoutInflater inflater;
    ChatAdapter chatAdapter;
    ArrayList<ChatModel> items;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_chat);

        listView = (ListView) findViewById(R.id.listView);
        chatInput = (EditText) findViewById(R.id.chatInput);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        items = new ArrayList<>();
        chatAdapter = new ChatAdapter();
        databaseReference = firebaseDatabase.getReference();

        databaseReference.child("chat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatModel cm = dataSnapshot.getValue(ChatModel.class);
                items.add(cm);
                chatAdapter.notifyDataSetChanged();
                listView.setSelection(items.size());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setAdapter(chatAdapter);
    }

    //채팅 메시지 전송
    public void sendMsg(View view) {
        String msg = chatInput.getText().toString();
        if (TextUtils.isEmpty(msg)) {
            chatInput.setError("입력 후 전송하세요.");
            return;
        }

        //GMT 시간 : 런던 +9시
        //그릇 준비
        ChatModel cm = new ChatModel(getUser().getEmail(), msg, System.currentTimeMillis());
        //데이터 추가하기
        databaseReference.child("chat").push().setValue(cm);

        chatInput.setText("");
    }

    class ViewHolder {
        TextView txt_left_name; //상대방 이메일
        TextView txt_left;      //상대방 메시지
        TextView txt_right;     //내 메시지
        LinearLayout left_container;
        LinearLayout right_container;
    }

    class ChatAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public ChatModel getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;

            if (view == null) {
                view = inflater.inflate(R.layout.sendbird_view_group_user_message, viewGroup, false);
                holder = new ViewHolder();
                holder.txt_left_name = view.findViewById(R.id.txt_left_name);
                holder.txt_left = view.findViewById(R.id.txt_left);
                holder.txt_right = view.findViewById(R.id.txt_right);
                holder.left_container = view.findViewById(R.id.left_container);
                holder.right_container = view.findViewById(R.id.right_container);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            ChatModel cm = getItem(i); //데이터 획득
            if (getUser().getEmail().equals(cm.getEmail())) {
                holder.left_container.setVisibility(View.GONE);
                holder.right_container.setVisibility(View.VISIBLE);
                holder.txt_right.setText(cm.getMsg());
            } else {
                holder.right_container.setVisibility(View.GONE);
                holder.left_container.setVisibility(View.VISIBLE);
                holder.txt_left.setText(cm.getMsg());
                holder.txt_left_name.setText(cm.getEmail());
            }

            return view;
        }
    }

}
