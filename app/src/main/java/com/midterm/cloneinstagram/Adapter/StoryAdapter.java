package com.midterm.cloneinstagram.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.Model.Storys;
import com.midterm.cloneinstagram.Controller.Activity.NewStory;
import com.midterm.cloneinstagram.R;
import com.midterm.cloneinstagram.Controller.Activity.StoryActivity;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder>{
    private Context mContext;
    private List<Storys> mStory;
    private Activity activity;

    public StoryAdapter(Context mContext, List<Storys> mStory, Activity activity) {
        this.mContext = mContext;
        this.mStory = mStory;
        this.activity = activity;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.add_story_item, parent, false);
            return new StoryAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.story_item, parent, false);
            return new StoryAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        holder.story_photo.getLayoutParams().height = (int)Math.round(height/10.24);
        if (position==0){
            String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
            FirebaseDatabase.getInstance().getReference()
                    .child("Story")
                    .child(FirebaseAuth.getInstance().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Storys storys = snapshot.getValue(Storys.class);
                                if(timeStamp.equals(storys.getDate())){
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(mContext, StoryActivity.class);
                                            intent.putExtra("userid", storys.getUsers().getUid());
                                            intent.putExtra("storyid", storys.getPostid());
                                            intent.putExtra("name", storys.getUsers().getName());
                                            intent.putExtra("image", storys.getUsers().getImageUri());
                                            intent.putExtra("imageStory", storys.getPostimage());
                                            mContext.startActivity(intent);
                                            activity.overridePendingTransition(R.anim.slide_out_down, R.anim.slide_up_dialog);
                                        }
                                    });
                                    Glide.with(mContext).load(storys.getPostimage())
                                            .transition(DrawableTransitionOptions.withCrossFade())
                                            .placeholder(mContext.getDrawable(R.drawable.accent)).into(holder.story_photo);
                                } else {
                                    FirebaseDatabase.getInstance().getReference().child("User")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("imageUri").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Intent intent = new Intent(mContext, NewStory.class);
                                                            mContext.startActivity(intent);
                                                            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left_1);
                                                        }
                                                    });
                                                    Glide.with(mContext).load(snapshot.getValue(String.class))
                                                            .transition(DrawableTransitionOptions.withCrossFade())
                                                            .placeholder(mContext.getDrawable(R.drawable.accent)).into(holder.story_photo);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }
                            } else {
                                FirebaseDatabase.getInstance().getReference().child("User")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .child("imageUri").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(mContext, NewStory.class);
                                                        mContext.startActivity(intent);
                                                        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left_1);
                                                    }
                                                });
                                                Glide.with(mContext).load(snapshot.getValue(String.class))
                                                        .transition(DrawableTransitionOptions.withCrossFade())
                                                        .placeholder(mContext.getDrawable(R.drawable.accent)).into(holder.story_photo);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            holder.cardView.setForeground(mContext.getDrawable(R.drawable.shape_1));
        }else {
            Storys storys =mStory.get(position-1);
            Glide.with(mContext).load(storys.getPostimage()).placeholder(mContext.getDrawable(R.drawable.accent)).into(holder.story_photo);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, StoryActivity.class);
                    intent.putExtra("userid", storys.getUsers().getUid());
                    intent.putExtra("storyid", storys.getPostid());
                    intent.putExtra("name", storys.getUsers().getName());
                    intent.putExtra("image", storys.getUsers().getImageUri());
                    intent.putExtra("imageStory", storys.getPostimage());
                    mContext.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_out_down, R.anim.slide_up_dialog);
                }
            });
            holder.story_username.setText(storys.getUsers().getName());
            FirebaseDatabase.getInstance().getReference().child("Story").child(storys.getUsers().getUid()).child("isSeen")
                    .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                holder.cardView.setForeground(mContext.getDrawable(R.drawable.shape_seen));
                            }else {
                                holder.cardView.setForeground(mContext.getDrawable(R.drawable.shape));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }
    }


    public int getItemViewType(int position){
        if(position == 0){
            return 0;
        }
        return 1;
    }

    @Override
    public int getItemCount() {
        return mStory.size()+1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView story_photo, story_plus, story_photo_seen;
        public TextView story_username, addstory_text;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            story_photo = itemView.findViewById(R.id.story_photo);
            story_plus = itemView.findViewById(R.id.story_plus);
//            story_photo_seen = itemView.findViewById(R.id.story_photo_seen);
            story_username = itemView.findViewById(R.id.story_username);
            addstory_text = itemView.findViewById(R.id.add_story_text);


            cardView = itemView.findViewById(R.id.card_view_for_image);
        }
    }

}
