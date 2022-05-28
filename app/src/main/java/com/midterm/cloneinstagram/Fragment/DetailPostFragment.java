package com.midterm.cloneinstagram.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.CommentActivity;
import com.midterm.cloneinstagram.Model.Post;
import com.midterm.cloneinstagram.R;
import com.squareup.picasso.Picasso;

public class DetailPostFragment extends Fragment {
    public TextView username, likes, publisher, description, comments, delete, back , date;
    String idPost;
    Post post;
    public ImageView image_profile, post_image, like, likeed,  comment, save;
    private static long LAST_CLICK_TIME = 0;
    private final int mDoubleClickInterval = 400; // Milliseconds
    public DetailPostFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idPost = getArguments().getString("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        image_profile = view.findViewById(R.id.image_profile);
        post_image = view.findViewById(R.id.post_image);
        comments = view.findViewById(R.id.comments);
        likes = view.findViewById(R.id.likes);
        comment = view.findViewById(R.id.comment);
        save = view.findViewById(R.id.save);
        username = view.findViewById(R.id.username);
        publisher = view.findViewById(R.id.publisher);
        like  = view.findViewById(R.id.like);
        description = view.findViewById(R.id.description);
        delete = view.findViewById(R.id.btn_delete_post);
        back = view.findViewById(R.id.back);
        date = view.findViewById(R.id.date);
        back.setVisibility(View.VISIBLE);
        likeed  = view.findViewById(R.id.likeed);
        setData();
        addEvent();
    }
    private void setData(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post").child(idPost);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                post = snapshot.getValue(Post.class);
                if(post.getUsers().getUid().equals(FirebaseAuth.getInstance().getUid())){
                    delete.setVisibility(View.VISIBLE);
                }
                getData();
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

        date.setText(post.getDate());
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
                Intent intent = new Intent(getContext(), CommentActivity.class);
                intent.putExtra("id", post.getPostid());
                getContext().startActivity(intent);
            }
        });
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CommentActivity.class);
                intent.putExtra("id", post.getPostid());
                getContext().startActivity(intent);
            }
        });
    }

    private void addEvent(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post").child(idPost);
                reference.removeValue();
                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowFollowFragment nextFrag= new ShowFollowFragment();
                Bundle bundle = new Bundle();
                bundle.putString("idPost", post.getPostid());
                nextFrag.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

    }
}