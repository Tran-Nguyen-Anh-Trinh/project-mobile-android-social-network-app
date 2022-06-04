package com.midterm.cloneinstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.midterm.cloneinstagram.Adapter.UserChatAdapter;
import com.midterm.cloneinstagram.Model.Users;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeChatActivity extends AppCompatActivity implements LifecycleObserver {
    long pressedTime;

    RecyclerView mainUserRecycleView;
    UserChatAdapter adapter;
    ArrayList<Users> arrayListUser;
    CircleImageView circleImageView;
    TextView textView;
    Users InforUser;
    CircleImageView status;

    public static String linkSendImg = "";
    FirebaseAuth mAuth;
    FirebaseDatabase database;


    String Name = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_chat);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        arrayListUser = new ArrayList<>();

        DatabaseReference reference = database.getReference().child("User");
        mainUserRecycleView = findViewById(R.id.mainRecycle);
        reference.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                InforUser = snapshot.getValue(Users.class);
                linkSendImg = InforUser.getImageUri();
                Picasso.get().load(InforUser.getImageUri()).into(circleImageView);
                textView.setText(InforUser.getName());
                adapter = new UserChatAdapter(HomeChatActivity.this, HomeChatActivity.this, arrayListUser, mAuth.getUid(), InforUser.getName(), InforUser.getImageUri());
                mainUserRecycleView.setLayoutManager(new LinearLayoutManager(HomeChatActivity.this));
                mainUserRecycleView.setAdapter(adapter);
                if("online".equals(InforUser.getStatus())){
                    status.setImageResource(R.drawable.color_online);
                }else{
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
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    users=dataSnapshot.getValue(Users.class);
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
        textView = findViewById(R.id.receiver_Name);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }



    void updateStatus(String status){
        DatabaseReference databaseReference = database.getReference().child("User").child(mAuth.getUid()).child("status");
        databaseReference.setValue(status);
    }


    @Override
    public void onBackPressed() {
        finishAndRemoveTask();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right_1);
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