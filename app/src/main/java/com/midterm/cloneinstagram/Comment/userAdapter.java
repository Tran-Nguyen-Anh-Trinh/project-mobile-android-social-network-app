package com.midterm.cloneinstagram.Comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.Controller.Activity.CommentActivity;
import com.midterm.cloneinstagram.Model.Comment;
import com.midterm.cloneinstagram.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class userAdapter extends RecyclerView.Adapter<userAdapter.Viewholder> {

    private static final String TAG = "MyActivity";
    Context mainActivity;
    ArrayList<Comment> listCmt;
    EditText sendComment;
    ImageView send;
    TextView duocRep, huyRep;
    LinearLayout rep;
    public static String idPost = "";


    public userAdapter(
            Context mainActivity,
            ArrayList<Comment> listCmt,
            EditText sendComment,
            ImageView send,
            TextView duocRep,
            TextView huyRep,
            LinearLayout rep) {
        this.mainActivity = mainActivity;
        this.listCmt = listCmt;

        this.send = send;
        this.sendComment = sendComment;
        this.duocRep = duocRep;
        this.huyRep = huyRep;
        this.rep = rep;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.user_item_1, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Comment u = listCmt.get(position);
        holder.name.setText(u.getUsers().getName());
        holder.content.setText(u.getComment());
        Glide.with(mainActivity).load(u.getUsers().getImageUri())
                .placeholder(mainActivity.getDrawable(R.drawable.accent)).into(holder.imgUser);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        ArrayList<Comment> list = new ArrayList<>();
        userAdapterFeedback adapterFeedback = new userAdapterFeedback(mainActivity, list);
        holder.recyclerView.setAdapter(adapterFeedback);
        holder.recyclerView.setVisibility(View.VISIBLE);
        holder.linearLayout.setVisibility(View.GONE);
        FirebaseDatabase.getInstance().getReference()
                .child("Post").child(CommentActivity.idPost).child("comment")
                .child(u.getId())
                .child("RepComment").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Comment comment = dataSnapshot.getValue(Comment.class);
                            list.add(comment);
                        }
                        if (list.isEmpty()){
                            holder.linearLayoutHide.setVisibility(View.GONE);
                        } else {
                            holder.linearLayoutHide.setVisibility(View.VISIBLE);
                        }
                        adapterFeedback.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.linearLayoutHide.setVisibility(View.VISIBLE);
                holder.linearLayout.setVisibility(View.GONE);
                holder.recyclerView.setVisibility(View.VISIBLE);

                FirebaseDatabase.getInstance().getReference()
                        .child("Post").child(CommentActivity.idPost).child("comment")
                        .child(u.getId())
                        .child("RepComment").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                list.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Comment comment = dataSnapshot.getValue(Comment.class);
                                    list.add(comment);
                                }
                                adapterFeedback.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
        });

        holder.linearLayoutHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.linearLayoutHide.setVisibility(View.GONE);
                holder.linearLayout.setVisibility(View.VISIBLE);
                holder.recyclerView.setVisibility(View.GONE);
            }
        });


        holder.feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idPost = u.getId();
                sendComment.setText("@" + u.getUsers().getName() + " ");
                sendComment.setSelection(sendComment.getText().length());
                sendComment.requestFocus();
                InputMethodManager imm = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(sendComment, InputMethodManager.SHOW_IMPLICIT);
                rep.setVisibility(View.VISIBLE);
                duocRep.setText(u.getUsers().getName());

            }
        });
    }

    @Override
    public int getItemCount() {
        return listCmt.size();
    }

    class Viewholder extends RecyclerView.ViewHolder {
        CircleImageView imgUser, img_user_rep;
        TextView name, name_rep;
        TextView content, content_rep;
        TextView feedback;
        RecyclerView recyclerView;
        LinearLayout linearLayout;
        LinearLayout linearLayoutHide;


        public Viewholder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imguser);
            name = itemView.findViewById(R.id.name);
            content = itemView.findViewById(R.id.content);
            feedback = itemView.findViewById(R.id.feedback);

            recyclerView = itemView.findViewById(R.id.list_cmt_rep);
            linearLayout = itemView.findViewById(R.id.liner_rep);
            linearLayoutHide = itemView.findViewById(R.id.liner_hide);

        }
    }

}
