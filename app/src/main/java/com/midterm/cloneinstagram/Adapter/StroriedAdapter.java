package com.midterm.cloneinstagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.midterm.cloneinstagram.Model.Storys;
import com.midterm.cloneinstagram.R;
import com.midterm.cloneinstagram.Controller.Activity.StoryActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StroriedAdapter  extends RecyclerView.Adapter<StroriedAdapter.ViewHolder>  {
    private Context mContext;
    private List<Storys> listImage;
    public FragmentActivity fragmentActivity;

    public StroriedAdapter(Context mContext, List<Storys> listImage, FragmentActivity fragmentActivity) {
        this.mContext = mContext;
        this.listImage = listImage;
        this.fragmentActivity = fragmentActivity;
    }
    @NonNull
    @Override
    public StroriedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_cell_posted, parent, false);
        return new StroriedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StroriedAdapter.ViewHolder holder, int position) {
        Storys storys = listImage.get(position);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        fragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        holder.image_post_personal.getLayoutParams().height = (int)Math.round(height/5.52);

        Glide.with(mContext).load(storys.getPostimage())
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(mContext.getDrawable(R.drawable.accent)).into(holder.image_post_personal);
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
                fragmentActivity.overridePendingTransition(R.anim.slide_out_down, R.anim.slide_up_dialog);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listImage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView image_post_personal;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_post_personal = itemView.findViewById(R.id.image_post_personal);

        }
    }
}
