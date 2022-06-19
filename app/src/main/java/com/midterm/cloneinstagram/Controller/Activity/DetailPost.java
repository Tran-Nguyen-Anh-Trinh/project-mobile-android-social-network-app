package com.midterm.cloneinstagram.Controller.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.Model.Post;
import com.midterm.cloneinstagram.R;
import com.squareup.picasso.Picasso;

public class DetailPost extends AppCompatActivity {
    public TextView username, likes, publisher, description, comments, delete, back;
    String idPost;
    Post post;
    public ImageView image_profile, post_image, like, likeed,  comment, save;
    private static long LAST_CLICK_TIME = 0;
    private final int mDoubleClickInterval = 400; // Milliseconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_detail_post);
        idPost = getIntent().getStringExtra("id");
        setUp();
        addEvent();
        setData();
    }

    private void setUp(){
        image_profile = findViewById(R.id.image_profile);
        post_image = findViewById(R.id.post_image);
        comments = findViewById(R.id.comments);
        likes = findViewById(R.id.likes);
        comment = findViewById(R.id.comment);
        save = findViewById(R.id.save);
        username = findViewById(R.id.username);
        publisher = findViewById(R.id.publisher);
        like  = findViewById(R.id.like);
        description = findViewById(R.id.description);
        delete = findViewById(R.id.btn_delete_post);
        back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        likeed  = findViewById(R.id.likeed);

    }
    private void setData(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post").child(idPost);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    post = snapshot.getValue(Post.class);
                    if(post.getUsers().getUid().equals(FirebaseAuth.getInstance().getUid())){
                        delete.setVisibility(View.VISIBLE);
                    }
                    getData();
                }else{
                    Toast.makeText(DetailPost.this, "Post does not exist", Toast.LENGTH_SHORT).show();
                    finishAndRemoveTask();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Post").child(idPost).child("comment").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    comments.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getData(){
        FirebaseDatabase.getInstance().getReference().child("Post").child(idPost).child("users").child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username.setText(post.getUsers().getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Post").child(idPost).child("users").child("imageUri").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Picasso.get().load(snapshot.getValue(String.class)).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("Post").child(idPost).child("postimage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Picasso.get().load(post.getPostimage()).into(post_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long doubleClickCurrentTime = System.currentTimeMillis();
                long currentClickTime = System.currentTimeMillis();
                if (currentClickTime - LAST_CLICK_TIME <= mDoubleClickInterval)
                {
                    if (likeed.getVisibility() == View.GONE) {
                        likeed.setVisibility(View.VISIBLE);
                        FirebaseDatabase.getInstance().getReference()
                                .child("Post").child(post.getPostid()).child("like").child(FirebaseAuth.getInstance().getUid())
                                .setValue(FirebaseAuth.getInstance().getUid());

                    } else {
                        likeed.setVisibility(View.GONE);
                        FirebaseDatabase.getInstance().getReference()
                                .child("Post").child(post.getPostid()).child("like").child(FirebaseAuth.getInstance().getUid()).removeValue();
                    }
                }
                else
                {
                    LAST_CLICK_TIME = System.currentTimeMillis();
                }
            }
        });

        publisher.setText(post.getUsers().getName());
        if(post.getDescription().equals("")) {
            description.setVisibility(View.GONE);
        }else {
            description.setVisibility(View.VISIBLE);
            description.setText(post.getDescription());
        }
        FirebaseDatabase.getInstance().getReference().child("Post").child(idPost).child("like").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // !Warning, Single click action pr

        FirebaseDatabase.getInstance().getReference().child("Post").child(idPost).child("like")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            likeed.setVisibility(View.VISIBLE);
                        } else {
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (likeed.getVisibility() == View.GONE) {
                    likeed.setVisibility(View.VISIBLE);
                    FirebaseDatabase.getInstance().getReference()
                            .child("Post").child(post.getPostid()).child("like").child(FirebaseAuth.getInstance().getUid())
                            .setValue(FirebaseAuth.getInstance().getUid());

                } else {
                    likeed.setVisibility(View.GONE);
                    FirebaseDatabase.getInstance().getReference()
                            .child("Post").child(post.getPostid()).child("like").child(FirebaseAuth.getInstance().getUid()).removeValue();
                }
            }
        });

        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailPost.this, CommentActivity.class);
                intent.putExtra("id", post.getPostid());
                DetailPost.this.startActivity(intent);
            }
        });
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailPost.this, CommentActivity.class);
                intent.putExtra("id", post.getPostid());
                DetailPost.this.startActivity(intent);
            }
        });
    }

    private void addEvent(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAndRemoveTask();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post").child(idPost);
                reference.removeValue();
                finishAndRemoveTask();
                Toast.makeText(DetailPost.this, "Deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }
}