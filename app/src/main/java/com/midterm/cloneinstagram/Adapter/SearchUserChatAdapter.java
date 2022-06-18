package com.midterm.cloneinstagram.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.Controller.Activity.ChatActivity;
import com.midterm.cloneinstagram.Model.Messages;
import com.midterm.cloneinstagram.Model.Users;
import com.midterm.cloneinstagram.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUserChatAdapter extends RecyclerView.Adapter<SearchUserChatAdapter.Viewholder> {
    Context homeActivity;
    ArrayList<Users> arrayListUser;
    Activity activity;

    FirebaseDatabase database;
    FirebaseAuth auth;
    ArrayList<String> listRoom;


    String idSend;
    String nameSend;
    String avaSend;

    public SearchUserChatAdapter(Context homeActivity, Activity activity, ArrayList<Users> arrayListUser, String idSend, String nameSend, String avaSend) {
        this.homeActivity = homeActivity;
        this.activity = activity;
        this.arrayListUser = arrayListUser;
        this.idSend = idSend;
        this.nameSend = nameSend;
        this.avaSend = avaSend;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(homeActivity).inflate(R.layout.search_user_chat, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        listRoom = new ArrayList<>();

        Users users = arrayListUser.get(position);
        if (auth.getUid().equals(users.getUid())) {
            holder.user_name.setText("You");
        } else {
            holder.user_name.setText(users.getName());
        }
        Picasso.get().load(users.getImageUri()).into(holder.user_profile);
        if ("online".equals(users.getStatus())) {
            holder.status.setImageResource(R.drawable.color_online);
        } else {
            holder.status.setImageResource(R.drawable.color_offline);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homeActivity, ChatActivity.class);
                intent.putExtra("Uid", users.getUid());
                intent.putExtra("Name", users.getName());
                intent.putExtra("ReceiverImg", users.getImageUri());
                intent.putExtra("idSend", idSend);
                intent.putExtra("nameSend", nameSend);
                intent.putExtra("avaSend", avaSend);
                homeActivity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left_1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListUser.size();
    }

    class Viewholder extends RecyclerView.ViewHolder {
        CircleImageView user_profile;
        TextView user_name;
        CircleImageView status;


        public Viewholder(@NonNull View itemView) {
            super(itemView);

            user_profile = itemView.findViewById(R.id.profileImg);
            user_name = itemView.findViewById(R.id.user_name);
            status = itemView.findViewById(R.id.status);
        }
    }
}
