package com.midterm.cloneinstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.Model.Comment;
import com.midterm.cloneinstagram.Model.Users;
import com.midterm.cloneinstagram.comment.userAdapter;
import com.midterm.cloneinstagram.comment.userCmt;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    userAdapter adapter;
    ArrayList<Comment> listCmt= new ArrayList<>();
    public static  String idPost;
    ImageView send;
    TextView btnCC, duocRep, huyRep;
    EditText comment;
    LinearLayout rep;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_comment);
        idPost = getIntent().getStringExtra("id");
        send = findViewById(R.id.send);
        comment = findViewById(R.id.sendComment);
        btnCC = findViewById(R.id.btnCC);
        duocRep = findViewById(R.id.duocRep);
        huyRep = findViewById(R.id.huyRep);
        rep = findViewById(R.id.rep);

        adapter = new userAdapter(CommentActivity.this, listCmt, comment, send, duocRep, huyRep, rep);
        recyclerView = findViewById(R.id.list_cmt);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        setUp();
        getDataComment();
    }

    private void getDataComment(){
        FirebaseDatabase.getInstance().getReference().child("Post").child(idPost).child("comment").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCmt.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    listCmt.add(comment);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUp(){
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(comment.getText().toString().isEmpty()){
                    return;
                }
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-ssss");
                Date date = new Date();
                String datetime = dateFormat.format(date);
                Comment comment1 = new Comment(datetime, Users.getInstance(), comment.getText().toString().trim());
                rep.setVisibility(View.GONE);
                if(!userAdapter.idPost.isEmpty()){
                    FirebaseDatabase.getInstance().getReference()
                            .child("Post").child(idPost).child("comment").child(userAdapter.idPost)
                            .child("RepComment").child(datetime).setValue(comment1)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        comment.setText("");
                                        userAdapter.idPost = "";
                                        Toast.makeText(CommentActivity.this, "Added comment", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(CommentActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Post").child(idPost).child("comment").child(datetime).setValue(comment1)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        comment.setText("");
                                        userAdapter.idPost = "";
                                        Toast.makeText(CommentActivity.this, "Added comment", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CommentActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        btnCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAndRemoveTask();
            }
        });
        huyRep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rep.setVisibility(View.GONE);
                userAdapter.idPost = "";
                comment.setText("");
                if(userAdapter.idPost.isEmpty()){

                    System.out.println("da rong");
                }
            }
        });
    }
}