package com.midterm.cloneinstagram.Adapter;


import static com.midterm.cloneinstagram.Controller.Activity.ChatActivity.rImage;
import static com.midterm.cloneinstagram.Controller.Activity.ChatActivity.sImage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.Model.Messages;
import com.midterm.cloneinstagram.R;
import com.midterm.cloneinstagram.Controller.Activity.seeImage;
import com.midterm.cloneinstagram.Controller.Activity.seeVideo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Messages> messagesArrayList;
    String Uid;
    String ReceiverImg;
    int ITEM_SEND = 1;
    int ITEM_RECEIVE = 2;
    int ITEM_SEND_IMAGE = 3;
    int ITEM_RECEIVE_IMAGE = 4;

    public MessagesAdapter(Context context, ArrayList<Messages> messagesArrayList, String Uid, String ReceiverImg) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
        this.Uid = Uid;
        this.ReceiverImg = ReceiverImg;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new sentViewholder(view);
        } else if (viewType == ITEM_RECEIVE) {
            View view = LayoutInflater.from(context).inflate(R.layout.receive_layout, parent, false);
            return new receiverViewholder(view);
        } else if (viewType == ITEM_SEND_IMAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.img_send_layout, parent, false);
            return new sendImgViewhoder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.img_receive_layout, parent, false);
            return new receiveImgViewholder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Messages messages = messagesArrayList.get(position);
        if (holder.getClass() == sentViewholder.class) {
            sentViewholder viewholder = (sentViewholder) holder;
            viewholder.txtMessages.setText(messages.getMessages());
            viewholder.txtDatetime.setText(messages.getTimeStamp());
            viewholder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    visibleAndInvisible(viewholder.txtDatetime, viewholder.txtSeen);
                }
            });
            String chatRoom = Uid + FirebaseAuth.getInstance().getUid();
            FirebaseDatabase.getInstance().getReference().child("Chats").child(chatRoom).child("IsRead").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String check = snapshot.getValue(String.class);
                    if ("true".equals(check)) {
                        viewholder.txtSeen.setText("");
                        viewholder.circleImageView.setVisibility(View.INVISIBLE);
                    }

                    else {
                        viewholder.txtSeen.setText("Seen");
                        if (position == messagesArrayList.size() -1) {
                            viewholder.circleImageView.setVisibility(View.VISIBLE);
                            Picasso.get().load(rImage).into(viewholder.circleImageView);
                        } else {
                            viewholder.circleImageView.setVisibility(View.INVISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else if (holder.getClass() == receiverViewholder.class) {
            receiverViewholder viewholder = (receiverViewholder) holder;
            viewholder.txtMessages.setText(messages.getMessages());
            viewholder.txtDateTime.setText(messages.getTimeStamp());
            viewholder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    visibleAndInvisible(viewholder.txtDateTime, viewholder.txtSeen);

                }
            });
            Picasso.get().load(rImage).into(viewholder.circleImageView);
        } else if (holder.getClass() == sendImgViewhoder.class) {
            sendImgViewhoder viewholder = (sendImgViewhoder) holder;
            viewholder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (viewholder.txtDateTime.getVisibility() == View.GONE) {
                        viewholder.txtDateTime.setText(messages.getTimeStamp());
                        viewholder.txtDateTime.setVisibility(View.VISIBLE);
                    } else {
                        viewholder.txtDateTime.setVisibility(View.GONE);
                    }
                }
            });

            viewholder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (messages.getUriImg().isEmpty()) {
                        Intent intent = new Intent(context, seeVideo.class);
                        intent.putExtra("linkURL", messages.getUriVid());
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, seeImage.class);
                        intent.putExtra("linkURL", messages.getUriImg());
                        context.startActivity(intent);
                    }
                }
            });
            if (messages.getUriVid().isEmpty()) {
                viewholder.btnPlay.setVisibility(View.INVISIBLE);
                Picasso.get().load(messages.getUriImg()).into(viewholder.imageView);
            } else {
                viewholder.imageView.setImageResource(R.drawable.background_black);
                viewholder.btnPlay.setVisibility(View.VISIBLE);
            }
            String chatRoom = Uid + FirebaseAuth.getInstance().getUid();
            FirebaseDatabase.getInstance().getReference().child("Chats").child(chatRoom).child("IsRead").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String check = snapshot.getValue(String.class);
                    if ("true".equals(check)) {
                    } else {
                        if (position == messagesArrayList.size() - 1) {
                            viewholder.circleImageView.setVisibility(View.VISIBLE);
                            Picasso.get().load(rImage).into(viewholder.circleImageView);
                        } else {
                            viewholder.circleImageView.setVisibility(View.INVISIBLE);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            Picasso.get().load(sImage).into(viewholder.circleImageView);
        } else if (holder.getClass() == receiveImgViewholder.class) {
            receiveImgViewholder viewholder = (receiveImgViewholder) holder;
            viewholder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (viewholder.txtDateTime.getVisibility() == View.GONE) {
                        viewholder.txtDateTime.setText(messages.getTimeStamp());
                        viewholder.txtDateTime.setVisibility(View.VISIBLE);
                    } else {
                        viewholder.txtDateTime.setVisibility(View.GONE);
                    }
                }
            });
            viewholder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (messages.getUriImg().isEmpty()) {
                        Intent intent = new Intent(context, seeVideo.class);
                        intent.putExtra("linkURL", messages.getUriVid());
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, seeImage.class);
                        intent.putExtra("linkURL", messages.getUriImg());
                        context.startActivity(intent);
                    }
                }
            });
            if (messages.getUriVid().isEmpty()) {
                viewholder.btnPlay.setVisibility(View.INVISIBLE);
                Picasso.get().load(messages.getUriImg()).into(viewholder.imageView);
            } else {
                viewholder.imageView.setImageResource(R.drawable.background_black);
                viewholder.btnPlay.setVisibility(View.VISIBLE);
            }
            Picasso.get().load(rImage).into(viewholder.circleImageView);
        }
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages = messagesArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderID())) {
            if (messages.getMessages().isEmpty() && messages.getUriVid().isEmpty()
                    || messages.getMessages().isEmpty() && messages.getUriImg().isEmpty()) {
                return ITEM_SEND_IMAGE;
            } else {
                return ITEM_SEND;
            }
        } else {
            if (messages.getMessages().isEmpty() && messages.getUriVid().isEmpty()
                    || messages.getMessages().isEmpty() && messages.getUriImg().isEmpty()) {
                return ITEM_RECEIVE_IMAGE;
            } else {
                return ITEM_RECEIVE;
            }
        }
    }

    class sentViewholder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView txtMessages;
        TextView txtDatetime;
        TextView txtSeen;

        public sentViewholder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.profile_image);
            txtMessages = itemView.findViewById(R.id.textSentMess);
            txtDatetime = itemView.findViewById(R.id.dateTime);
            txtSeen = itemView.findViewById(R.id.txtSeen);

        }
    }

    class receiverViewholder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView txtMessages;
        TextView txtDateTime;
        TextView txtSeen;

        public receiverViewholder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.profile_image);
            txtMessages = itemView.findViewById(R.id.textRecMess);
            txtDateTime = itemView.findViewById(R.id.dateTime);
            txtSeen = itemView.findViewById(R.id.txtSeen);
        }
    }

    class sendImgViewhoder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        ImageView imageView;
        TextView btnPlay;
        TextView txtDateTime;

        public sendImgViewhoder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.profile_image);
            txtDateTime = itemView.findViewById(R.id.dateTime);
            imageView = itemView.findViewById(R.id.sendImg);
            btnPlay = itemView.findViewById(R.id.btnPlay);
        }
    }

    class receiveImgViewholder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        ImageView imageView;
        TextView btnPlay;
        TextView txtDateTime;

        public receiveImgViewholder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.profile_image);
            imageView = itemView.findViewById(R.id.receiveImg);
            txtDateTime = itemView.findViewById(R.id.dateTime);
            btnPlay = itemView.findViewById(R.id.btnPlay);
        }
    }

    void visibleAndInvisible(TextView date, TextView txtSeen) {
        if (date.getVisibility() == View.GONE) {
            date.setVisibility(View.VISIBLE);
        } else {
            date.setVisibility(View.GONE);
        }
        if (txtSeen.getVisibility() == View.INVISIBLE) {
            txtSeen.setVisibility(View.VISIBLE);
        } else {
            txtSeen.setVisibility(View.INVISIBLE);
        }

    }
}
