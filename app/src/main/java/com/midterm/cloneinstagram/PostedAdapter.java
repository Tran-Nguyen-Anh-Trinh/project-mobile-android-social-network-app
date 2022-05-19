package com.midterm.cloneinstagram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostedAdapter  extends RecyclerView.Adapter<PostedAdapter.ViewHolder> {
    private Context mContext;
    private List<String> listImage;

    public PostedAdapter(Context mContext, List<String> listImage) {
        this.mContext = mContext;
        this.listImage = listImage;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_cell_posted, parent, false);
        return new PostedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView userName;
        private  TextView fullName;
        private TextView button;
        private CircleImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.username);
            fullName = itemView.findViewById(R.id.fullname);
            button = itemView.findViewById(R.id.btn_follow);
            imageView = itemView.findViewById(R.id.image_profile);

        }
    }
}
