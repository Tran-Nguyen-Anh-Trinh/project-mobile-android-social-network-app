package com.midterm.cloneinstagram.Comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm.cloneinstagram.Model.Comment;
import com.midterm.cloneinstagram.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class userAdapterFeedback extends RecyclerView.Adapter<userAdapterFeedback.viewHolder>{

    private Context mContext;
    private ArrayList<Comment> listCmtRep;

    public userAdapterFeedback(Context mContext, ArrayList<Comment> listCmtRep) {
        this.mContext = mContext;
        this.listCmtRep = listCmtRep;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item_1, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Comment u = listCmtRep.get(position);
        Picasso.get().load(u.getUsers().getImageUri()).into(holder.img_user_rep);
        holder.name_rep.setText(u.getUsers().getName());
        holder.content_rep.setText(u.getComment());
        holder.fb.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return listCmtRep.size();
    }

    class viewHolder extends RecyclerView.ViewHolder{
        CircleImageView img_user_rep;
        TextView name_rep;
        TextView fb;
        TextView content_rep;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            img_user_rep = itemView.findViewById(R.id.imguser);
            name_rep = itemView.findViewById(R.id.name);
            content_rep = itemView.findViewById(R.id.content);
            fb = itemView.findViewById(R.id.feedback);
        }
    }
}


