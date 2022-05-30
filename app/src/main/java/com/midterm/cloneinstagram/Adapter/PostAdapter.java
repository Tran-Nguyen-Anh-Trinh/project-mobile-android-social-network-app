package com.midterm.cloneinstagram.Adapter;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.CommentActivity;
import com.midterm.cloneinstagram.Fragment.DetailPostFragment;
import com.midterm.cloneinstagram.Fragment.ShowFollowFragment;
import com.midterm.cloneinstagram.Model.Notification;
import com.midterm.cloneinstagram.Model.Post;
import com.midterm.cloneinstagram.Fragment.ProfileUserFragment;
import com.midterm.cloneinstagram.R;
import com.midterm.cloneinstagram.Model.Users;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public Context mContext;
    public List<Post> mPost;
    public FragmentActivity fragmentActivity;
    private static long LAST_CLICK_TIME = 0;
    private final int mDoubleClickInterval = 400; // Milliseconds
    private Animation zoom_in, zoom_out;
    private ImageView imageView2;
    private Activity activity;


    public PostAdapter(Context mContext, List<Post> mPost, FragmentActivity fragmentActivity, ImageView imageView2, Activity activity) {
        this.mContext = mContext;
        this.mPost = mPost;
        this.fragmentActivity = fragmentActivity;
        this.imageView2 = imageView2;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, viewGroup, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPost.get(position);


        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("idUser", post.getUsers().getUid());
                bundle.putString("type", "home");
                ProfileUserFragment nextFrag = new ProfileUserFragment();
                nextFrag.setArguments(bundle);

                FragmentTransaction fragmentTransaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                fragmentTransaction.replace(R.id.fragment_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("type", "home");
                bundle.putString("idUser", post.getUsers().getUid());
                ProfileUserFragment nextFrag = new ProfileUserFragment();
                nextFrag.setArguments(bundle);

                FragmentTransaction fragmentTransaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                fragmentTransaction.replace(R.id.fragment_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        holder.username.setText(post.getUsers().getName());
        Picasso.get().load(post.getUsers().getImageUri()).into(holder.image_profile);
        Picasso.get().load(post.getPostimage()).into(holder.post_image);

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long doubleClickCurrentTime = System.currentTimeMillis();
                long currentClickTime = System.currentTimeMillis();
                if (currentClickTime - LAST_CLICK_TIME <= mDoubleClickInterval) {
                    String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
                    String idNotify = new SimpleDateFormat("yyyyMMdd_HHmmssss").format(Calendar.getInstance().getTime());
                    idNotify += System.currentTimeMillis();
                    if (holder.likeed.getVisibility() == View.GONE) {
                        imageView2.setVisibility(View.VISIBLE);
                        imageView2.animate().scaleX(3.0f).scaleY(3.0f)
                                .alpha(1.0f)
                                .setDuration(300)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        imageView2.animate().scaleX(0.5f).scaleY(0.5f)
                                                .alpha(0.0f)
                                                .setDuration(700)
                                                .setListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        super.onAnimationEnd(animation);
                                                        imageView2.setVisibility(View.GONE);
                                                    }
                                                });
                                    }
                                });


                        holder.likeed.setVisibility(View.VISIBLE);
                        FirebaseDatabase.getInstance().getReference()
                                .child("Post").child(post.getPostid()).child("like").child(FirebaseAuth.getInstance().getUid())
                                .setValue(FirebaseAuth.getInstance().getUid());
                        Notification notification = new Notification();
                        notification.setIdNotify(idNotify);
                        notification.setIdUser(FirebaseAuth.getInstance().getUid());
                        notification.setType("like");
                        notification.setDate(timeStamp);
                        notification.setContent("liked your photo");
                        notification.setIdPost(post.getPostid());
                        notification.setIdPostLike(post.getPostid());

                        if (!post.getUsers().getUid().equals(FirebaseAuth.getInstance().getUid())) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("Notify").child(post.getUsers().getUid())
                                    .child("isRead").push()
                                    .setValue(post.getUsers().getUid());
                            FirebaseDatabase.getInstance().getReference()
                                    .child("Notify").child(post.getUsers().getUid())
                                    .push().setValue(notification);
                        }

                    } else {
                        holder.likeed.setVisibility(View.GONE);
                        FirebaseDatabase.getInstance().getReference()
                                .child("Notify").child(post.getUsers().getUid())
                                .child("isRead").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            dataSnapshot.getRef().removeValue();
                                            break;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                        FirebaseDatabase.getInstance().getReference()
                                .child("Notify").child(post.getUsers().getUid()).orderByChild("idPostLike").equalTo(post.getPostid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            dataSnapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                        FirebaseDatabase.getInstance().getReference()
                                .child("Post").child(post.getPostid()).child("like")
                                .child(FirebaseAuth.getInstance().getUid()).removeValue();
                    }
                } else {
                    LAST_CLICK_TIME = System.currentTimeMillis();
                }
            }
        });

        holder.publisher.setText(post.getUsers().getName());
        if (post.getDescription().equals("")) {
            holder.publisher.setVisibility(View.GONE);
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.publisher.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }
        FirebaseDatabase.getInstance().getReference().child("Post").child(post.getPostid()).child("like").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.likes.setText(snapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Post").child(post.getPostid()).child("like")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.likeed.setVisibility(View.VISIBLE);
                        } else {
                            holder.likeed.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
                String idNotify = new SimpleDateFormat("yyyyMMdd_HHmmssss").format(Calendar.getInstance().getTime());
                idNotify += System.currentTimeMillis();
                if (holder.likeed.getVisibility() == View.GONE) {
                    holder.likeed.setVisibility(View.VISIBLE);
                    FirebaseDatabase.getInstance().getReference()
                            .child("Post").child(post.getPostid()).child("like").child(FirebaseAuth.getInstance().getUid())
                            .setValue(FirebaseAuth.getInstance().getUid());
                    Notification notification = new Notification();
                    notification.setIdNotify(idNotify);
                    notification.setIdUser(FirebaseAuth.getInstance().getUid());
                    notification.setType("like");
                    notification.setDate(timeStamp);
                    notification.setContent("liked your photo");
                    notification.setIdPost(post.getPostid());
                    notification.setIdPostLike(post.getPostid());

                    if (!post.getUsers().getUid().equals(FirebaseAuth.getInstance().getUid())) {
                        FirebaseDatabase.getInstance().getReference()
                                .child("Notify").child(post.getUsers().getUid())
                                .child("isRead").push()
                                .setValue(post.getUsers().getUid());
                        FirebaseDatabase.getInstance().getReference()
                                .child("Notify").child(post.getUsers().getUid())
                                .push().setValue(notification);
                    }

                } else {
                    holder.likeed.setVisibility(View.GONE);
                    FirebaseDatabase.getInstance().getReference()
                            .child("Notify").child(post.getUsers().getUid())
                            .child("isRead").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        dataSnapshot.getRef().removeValue();
                                        break;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                    FirebaseDatabase.getInstance().getReference()
                            .child("Notify").child(post.getUsers().getUid()).orderByChild("idPostLike").equalTo(post.getPostid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        dataSnapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                    FirebaseDatabase.getInstance().getReference()
                            .child("Post").child(post.getPostid()).child("like")
                            .child(FirebaseAuth.getInstance().getUid()).removeValue();
                }
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("id", post.getPostid());
                intent.putExtra("idUser", post.getUsers().getUid());
                mContext.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_out_down, R.anim.slide_up_dialog);
            }
        });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("id", post.getPostid());
                intent.putExtra("idUser", post.getUsers().getUid());
                mContext.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_out_down, R.anim.slide_up_dialog);
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Post").child(post.getPostid()).child("comment").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.comments.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowFollowFragment nextFrag = new ShowFollowFragment();
                Bundle bundle = new Bundle();
                bundle.putString("idPost", post.getPostid());
                bundle.putString("type", "home");
                nextFrag.setArguments(bundle);

                FragmentTransaction fragmentTransaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_out_down, R.anim.slide_up_dialog);
                fragmentTransaction.replace(R.id.fragment_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        holder.date.setText(post.getDate());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailPostFragment nextFrag = new DetailPostFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", post.getPostid());
                bundle.putString("type", "home");
                nextFrag.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                fragmentTransaction.replace(R.id.fragment_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_profile, post_image, like, likeed, comment, save;
        public TextView username, likes, publisher, description, comments, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            comments = itemView.findViewById(R.id.comments);
            likes = itemView.findViewById(R.id.likes);
            like = itemView.findViewById(R.id.like);
            likeed = itemView.findViewById(R.id.likeed);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            username = itemView.findViewById(R.id.username);
            publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
        }
    }

    private void publisherInfo(ImageView image_profile, TextView username, TextView publisher, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                Glide.with(mContext).load(user.getImageUri()).into(image_profile);
                username.setText(user.getName());
                publisher.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

