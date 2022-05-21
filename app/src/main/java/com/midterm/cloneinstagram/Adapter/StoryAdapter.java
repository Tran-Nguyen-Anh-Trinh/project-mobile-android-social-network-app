package com.midterm.cloneinstagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm.cloneinstagram.Model.Story;
import com.midterm.cloneinstagram.R;
import com.midterm.cloneinstagram.StoryActivity;

import org.w3c.dom.Text;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder>{
    private Context mContext;
    private List<Story> mStory;

    public StoryAdapter(Context mContext, List<Story> mStory) {
        this.mContext = mContext;
        this.mStory = mStory;
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
//        Story story = mStory.get(position);
//        userInfo(holder, story.getUserid(), position);


        holder.itemView.setOnClickListener((view) -> {
            if (holder.getAdapterPosition() == 0) {

            }else {
                Intent intent = new Intent(mContext, StoryActivity.class);
                intent.putExtra("userid", "username");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView story_photo, story_plus, story_photo_seen;
        public TextView story_username, addstory_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            story_photo = itemView.findViewById(R.id.story_photo);
            story_plus = itemView.findViewById(R.id.story_plus);
            story_photo_seen = itemView.findViewById(R.id.story_photo_seen);
            story_username = itemView.findViewById(R.id.story_username);
            addstory_text = itemView.findViewById(R.id.add_story_text);

        }
    }

    public int getItemViewType(int position){
        if(position == 0){
            return 0;
        }
        return 1;
    }

    private void userInfo(ViewHolder viewHolder, String userid, int pos){

    }

    private void myStory(TextView textView, ImageView imageView, boolean click) {

    }

    private void seenStory(ViewHolder viewHolder, String userid){

    }
}
