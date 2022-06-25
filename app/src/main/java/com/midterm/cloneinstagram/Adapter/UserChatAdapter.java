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

import com.bumptech.glide.Glide;
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

public class UserChatAdapter  extends RecyclerView.Adapter<UserChatAdapter.Viewholder> {
    Context homeActivity;
    ArrayList<Users> arrayListUser;
    Activity activity;

    FirebaseDatabase database;
    FirebaseAuth auth;
    ArrayList<String> listRoom;


    String idSend;
    String nameSend;
    String avaSend;

    public UserChatAdapter(Context homeActivity, Activity activity, ArrayList<Users> arrayListUser, String idSend, String nameSend, String avaSend) {
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
        View view = LayoutInflater.from(homeActivity).inflate(R.layout.item_user_row, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        listRoom = new ArrayList<>();

        Users users = arrayListUser.get(position);
        String chatRoom = auth.getUid() + users.getUid();
        String chatRoom1 = users.getUid() + auth.getUid();
        FirebaseDatabase.getInstance().getReference().child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (chatRoom.equals(data.getKey())) {
                        Query postTop = FirebaseDatabase.getInstance()
                                .getReference()
                                .child("Chats")
                                .child(chatRoom)
                                .child("Messages")
                                .limitToLast(1);
                        postTop.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Messages messages;
                                if (!snapshot.exists()) {
                                    return;
                                }
                                DataSnapshot dataSnapshot = snapshot.getChildren().iterator().next();
                                messages = dataSnapshot.getValue(Messages.class);
                                holder.user_time.setText(" • " + messages.getTimeStamp());
                                if (messages.getMessages().isEmpty() && messages.getUriVid().isEmpty()) {
                                    String mess;
                                    if (auth.getUid().equals(messages.getSenderID())) {
                                        holder.user_messages.setText("You: Image messages");
                                    } else {
                                        mess = users.getName() + ": Image messages";
                                        holder.user_messages.setText(mess);
                                    }
                                } else if (messages.getUriImg().isEmpty() && messages.getUriVid().isEmpty()) {
                                    String mess;
                                    if (auth.getUid().equals(messages.getSenderID())) {
                                        mess = "You: " + messages.getMessages();
                                    } else {
                                        mess = users.getName() + ": " + messages.getMessages();
                                    }
                                    holder.user_messages.setText(mess);
                                } else {
                                    String mess;
                                    if (auth.getUid().equals(messages.getSenderID())) {
                                        holder.user_messages.setText("You: Video messages");
                                    } else {
                                        mess = users.getName() + ": Video messages";
                                        holder.user_messages.setText(mess);
                                    }
                                }
                                database.getReference().child("Chats").child(chatRoom).child("IsRead").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String check = snapshot.getValue(String.class);
                                        if ("true".equals(check)) {
                                            holder.user_messages.setTypeface(null, Typeface.BOLD);
                                            holder.user_time.setTypeface(null, Typeface.BOLD);
                                        } else {
                                            holder.user_messages.setTypeface(null, Typeface.NORMAL);
                                            holder.user_time.setTypeface(null, Typeface.NORMAL);
                                            Picasso.get().load(users.getImageUri()).into(holder.isRead);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                database.getReference().child("Chats").child(chatRoom1).child("IsRead").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String check = snapshot.getValue(String.class);
                                        if ("true".equals(check)) {
                                            holder.isRead.setVisibility(View.GONE);
                                        } else {
                                            Picasso.get().load(users.getImageUri()).into(holder.isRead);
                                            if (messages.getSenderID().equals(auth.getUid())){
                                                holder.isRead.setVisibility(View.VISIBLE);
                                            } else {
                                                holder.isRead.setVisibility(View.GONE);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else {
                        holder.user_messages.setText("...");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (auth.getUid().equals(users.getUid())) {
            holder.user_name.setText("You");
        } else {
            holder.user_name.setText(users.getName());
        }
        Glide.with(homeActivity).load(users.getImageUri()).into(holder.user_profile);
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
        TextView user_messages;
        TextView user_time;
        CircleImageView status;
        CircleImageView isRead;


        public Viewholder(@NonNull View itemView) {
            super(itemView);

            user_profile = itemView.findViewById(R.id.profileImg);
            user_name = itemView.findViewById(R.id.user_name);
            user_messages = itemView.findViewById(R.id.user_messages);
            user_time = itemView.findViewById(R.id.user_time);
            status = itemView.findViewById(R.id.status);
            isRead = itemView.findViewById(R.id.isRead);
        }
    }
}
