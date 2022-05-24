package com.midterm.cloneinstagram.Adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm.cloneinstagram.DetailPost;
import com.midterm.cloneinstagram.Model.Post;
import com.midterm.cloneinstagram.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{
    private Context mContext;
    private List<Post> listImage;

    public SearchAdapter(Context mContext, List<Post> listImage) {
        this.mContext = mContext;
        this.listImage = listImage;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_search_item, parent, false);
        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        Post post = listImage.get(position);
        Picasso.get().load(post.getPostimage()).into(holder.all_image_personal);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DetailPost.class);
                intent.putExtra("id", post.getPostid());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listImage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView all_image_personal;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            all_image_personal = itemView.findViewById(R.id.image_seach);

        }
    }
}
