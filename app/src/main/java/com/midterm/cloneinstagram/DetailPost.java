package com.midterm.cloneinstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.Model.Post;
import com.squareup.picasso.Picasso;

public class DetailPost extends AppCompatActivity {
    public ImageView image_profile, post_image, like, comment, save;
    public TextView username, likes, publisher, description, comments, delete, back;
    String idPost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_item);
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
        description = findViewById(R.id.description);
        delete = findViewById(R.id.btn_delete_post);
        delete.setVisibility(View.VISIBLE);
        back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);

    }
    private void setData(){


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post").child(idPost);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                username.setText(post.getUsers().getName());
                Picasso.get().load(post.getUsers().getImageUri()).into(image_profile);
                Picasso.get().load(post.getPostimage()).into(post_image);
                if(post.getDescription().equals("")) {
                    description.setVisibility(View.GONE);
                }else {
                    description.setVisibility(View.VISIBLE);
                    description.setText(post.getDescription());
                }
                publisher.setText(post.getUsers().getName());
                likes.setText(post.getLike()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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