package com.midterm.cloneinstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.Adapter.PostedAdapter;
import com.midterm.cloneinstagram.Model.Post;
import com.midterm.cloneinstagram.Model.Users;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvFollow;
    private PostedAdapter postedAdapter;
    private List<Post> list;
    private CircleImageView profile;
    private TextView name;
    private TextView tv_posts;
    private TextView tv_followers;
    private TextView tv_following;
    private String idUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        idUser = getIntent().getStringExtra("idUser");
        tvFollow = findViewById(R.id.tv_follow);
        tvFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvFollow.getText().equals("Follow")) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("User").child(idUser).child("follower")
                            .child(FirebaseAuth.getInstance().getUid())
                            .setValue(FirebaseAuth.getInstance().getUid());
                    FirebaseDatabase.getInstance().getReference()
                            .child("User").child(FirebaseAuth.getInstance().getUid())
                            .child("following").child(idUser).setValue(idUser);
                    tvFollow.setText("Following");
                } else {
                    FirebaseDatabase.getInstance().getReference()
                            .child("User").child(idUser).child("follower")
                            .child(FirebaseAuth.getInstance().getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference()
                            .child("User").child(FirebaseAuth.getInstance().getUid())
                            .child("following").child(idUser).removeValue();
                    tvFollow.setText("Follow");
                }
            }
        });
        recyclerView = findViewById(R.id.rv_posted);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(ProfileUserActivity.this, 3));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                int spanCount = 3;
                int spacing = 10;

                if (position >= 0) {
                    int column = position % spanCount; // item column

                    outRect.left = spacing - column * spacing / spanCount;
                    outRect.right = (column + 1) * spacing / spanCount;

                    if (position < spanCount) {
                        outRect.top = spacing;
                    }
                    outRect.bottom = spacing;
                } else {
                    outRect.left = 0;
                    outRect.right = 0;
                    outRect.top = 0;
                    outRect.bottom = 0;
                }
            }
        });
        list = new ArrayList<>();
        postedAdapter = new PostedAdapter(ProfileUserActivity.this, list, ProfileUserActivity.this);
        recyclerView.setAdapter(postedAdapter);

        profile = findViewById(R.id.profile);
        name = findViewById(R.id.tv_name);
        tv_posts = findViewById(R.id.tv_posts);
        tv_followers = findViewById(R.id.tv_followers);
        tv_following = findViewById(R.id.tv_following);
        readPost();
        getData();
    }
    private void readPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if(post.getUsers().getUid().equals(idUser)){
                        list.add(0, post);
                    }
                }
                tv_posts.setText(list.size()+"");
                postedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getData(){
        FirebaseDatabase.getInstance().getReference().child("User").child(idUser).addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                Picasso.get().load(users.getImageUri()).into(profile);
//                tv_followers.setText(users.getFollower().size()+"");
//                tv_following.setText(users.getFollowing().size()+"");
                name.setText(users.getName());
                if (idUser.equals(FirebaseAuth.getInstance().getUid())){
                    tvFollow.setEnabled(false);
                    tvFollow.setBackgroundColor(R.color.silver);
                    tvFollow.setTextColor(R.color.silver);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("User").child(idUser).child("follower").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tv_followers.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("User").child(idUser).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tv_following.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("User").child(idUser).child("follower").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    tvFollow.setText("Following");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}