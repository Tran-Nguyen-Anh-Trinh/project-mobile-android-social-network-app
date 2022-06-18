package com.midterm.cloneinstagram.Controller.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.Adapter.SearchUserChatAdapter;
import com.midterm.cloneinstagram.Adapter.UserChatAdapter;
import com.midterm.cloneinstagram.Model.Users;
import com.midterm.cloneinstagram.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeChatActivity extends AppCompatActivity implements LifecycleObserver {
    long pressedTime;

    RecyclerView mainUserRecycleView;
    UserChatAdapter adapter;
    ArrayList<Users> arrayListUser;
    CircleImageView circleImageView;
    TextView notify;
    Users InforUser;
    CircleImageView status;
    EditText searchbar;
    ImageView delete;
    RelativeLayout relativeLayout;
    SearchUserChatAdapter searchUserChatAdapter;
    RecyclerView searchRecycleView;


    public static String linkSendImg = "";
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    ArrayList<Users> tmpUserList;


    String Name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_chat);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        arrayListUser = new ArrayList<>();
        tmpUserList = new ArrayList<>();

        DatabaseReference reference = database.getReference().child("User");
        mainUserRecycleView = findViewById(R.id.mainRecycle);
        searchRecycleView = findViewById(R.id.searchRecycle);
        reference.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                InforUser = snapshot.getValue(Users.class);
                linkSendImg = InforUser.getImageUri();
                Picasso.get().load(InforUser.getImageUri()).into(circleImageView);
//                textView.setText(InforUser.getName());
                searchUserChatAdapter = new SearchUserChatAdapter(HomeChatActivity.this, HomeChatActivity.this, tmpUserList, mAuth.getUid(), InforUser.getName(), InforUser.getImageUri());

                adapter = new UserChatAdapter(HomeChatActivity.this, HomeChatActivity.this, arrayListUser, mAuth.getUid(), InforUser.getName(), InforUser.getImageUri());
                mainUserRecycleView.setLayoutManager(new LinearLayoutManager(HomeChatActivity.this));
                mainUserRecycleView.setAdapter(adapter);

                searchRecycleView.setLayoutManager(new LinearLayoutManager(HomeChatActivity.this));
                searchRecycleView.setAdapter(searchUserChatAdapter);
                if ("online".equals(InforUser.getStatus())) {
                    status.setImageResource(R.drawable.color_online);
                } else {
                    status.setImageResource(R.drawable.color_offline);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayListUser.clear();
                Users users;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    users = dataSnapshot.getValue(Users.class);
                    arrayListUser.add(users);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        status = findViewById(R.id.status);
        circleImageView = findViewById(R.id.profileImg_1);
        notify = findViewById(R.id.notify);

        relativeLayout = findViewById(R.id.relative_hide);

        searchRecycleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard(view);
                return false;
            }
        });

        mainUserRecycleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard(view);
                return false;
            }
        });


        delete = findViewById(R.id.btn_delete);
        searchbar = findViewById(R.id.search_bar);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchbar.setText("");
                notify.setVisibility(View.GONE);
                searchbar.clearFocus();
            }
        });

        searchbar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        searchbar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == keyEvent.KEYCODE_DEL && searchbar.getText().toString().isEmpty()){
                    tmpUserList.clear();
                    notify.setVisibility(View.GONE);
                    searchUserChatAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });


        searchbar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().isEmpty()){
                    tmpUserList.clear();
                    searchUserChatAdapter.notifyDataSetChanged();
                    delete.setVisibility(View.INVISIBLE);

                } else {
                    delete.setVisibility(View.VISIBLE);
                    searchUser(charSequence.toString().toLowerCase());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private void searchUser(String key) {
        FirebaseDatabase.getInstance().getReference().child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tmpUserList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);
                    if(users.getName().toLowerCase().contains(key)){
                        tmpUserList.add(users);
                    }
                }
                if(tmpUserList.isEmpty()){
                    notify.setVisibility(View.VISIBLE);
                } else {
                    notify.setVisibility(View.GONE);
                }
                searchUserChatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    void updateStatus(String status) {
        if(mAuth.getUid()!=null) {
            DatabaseReference databaseReference = database.getReference().child("User").child(mAuth.getUid()).child("status");
            databaseReference.setValue(status);
        }
    }


    @Override
    public void onBackPressed() {
        String from = getIntent().getStringExtra("from");
        if (from != null) {
            Intent intent = new Intent(HomeChatActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            HomeChatActivity.this.startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left_2, R.anim.slide_out_right_2);
        } else {
            finishAndRemoveTask();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right_1);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
        updateStatus("offline");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
        updateStatus("online");
    }
}