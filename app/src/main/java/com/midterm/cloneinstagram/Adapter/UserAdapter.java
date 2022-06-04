package com.midterm.cloneinstagram.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.Fragment.DetailPostFragment;
import com.midterm.cloneinstagram.Fragment.ProfileFragment;
import com.midterm.cloneinstagram.Fragment.ProfileUserFragment;
import com.midterm.cloneinstagram.Model.Notification;
import com.midterm.cloneinstagram.Model.Post;
import com.midterm.cloneinstagram.Model.Users;
import com.midterm.cloneinstagram.PushNotify.FCMSend;
import com.midterm.cloneinstagram.R;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<Users> mUsers;
    public FragmentActivity fragmentActivity;

    public UserAdapter(Context mContext, List<Users> mUsers, FragmentActivity fragmentActivity) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.fragmentActivity = fragmentActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_follow, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Users users = mUsers.get(position);
        holder.userName.setText(users.getName());
        holder.fullName.setText(users.getEmail());
        Picasso.get().load(users.getImageUri()).into(holder.imageView);


        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.button.getText().equals("Follow")) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("User").child(users.getUid()).child("follower")
                            .child(FirebaseAuth.getInstance().getUid())
                            .setValue(FirebaseAuth.getInstance().getUid());
                    FirebaseDatabase.getInstance().getReference()
                            .child("User").child(FirebaseAuth.getInstance().getUid())
                            .child("following").child(users.getUid()).setValue(users.getUid());
                    holder.button.setText("Following");
                    notifyApp(users.getUid());
                } else {
                    FirebaseDatabase.getInstance().getReference()
                            .child("User").child(users.getUid()).child("follower")
                            .child(FirebaseAuth.getInstance().getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference()
                            .child("User").child(FirebaseAuth.getInstance().getUid())
                            .child("following").child(users.getUid()).removeValue();
                    holder.button.setText("Follow");
                    notifyApp2(users.getUid());
                }
            }
        });

        FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getUid()).child("following").child(users.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        holder.button.setText("Following");
                    } else {
                        if(users.getUid().equals(FirebaseAuth.getInstance().getUid())){
                            holder.button.setText("You");
                            holder.button.setEnabled(false);
                        } else {
                            holder.button.setText("Follow");
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("idUser", users.getUid());
                ProfileUserFragment nextFrag= new ProfileUserFragment();
                nextFrag.setArguments(bundle);

                FragmentTransaction fragmentTransaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                fragmentTransaction.add(R.id.fragment_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
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

    private void notifyApp(String idUser){
        String idNotify = new SimpleDateFormat("yyyyMMdd_HHmmssss").format(Calendar.getInstance().getTime());
        idNotify += System.currentTimeMillis();
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());

        Notification notification = new Notification();
        notification.setIdNotify(idNotify);
        notification.setIdUser(FirebaseAuth.getInstance().getUid());
        notification.setType("follow");
        notification.setDate(timeStamp);
        notification.setContent("followed you");
        notification.setIdUser(FirebaseAuth.getInstance().getUid());
        FirebaseDatabase.getInstance().getReference()
                .child("Notify").child(idUser)
                .child("isRead").push()
                .setValue(idUser);

        FirebaseDatabase.getInstance().getReference()
                .child("Notify").child(idUser)
                .push().setValue(notification);
    }
    private void notifyApp2(String idUser){
        String idNotify = new SimpleDateFormat("yyyyMMdd_HHmmssss").format(Calendar.getInstance().getTime());
        idNotify += System.currentTimeMillis();
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());

        Notification notification = new Notification();
        notification.setIdNotify(idNotify);
        notification.setIdUser(FirebaseAuth.getInstance().getUid());
        notification.setType("follow");
        notification.setDate(timeStamp);
        notification.setContent("unfollowed you");
        notification.setIdUser(FirebaseAuth.getInstance().getUid());
        FirebaseDatabase.getInstance().getReference()
                .child("Notify").child(idUser)
                .child("isRead").push()
                .setValue(idUser);

        FirebaseDatabase.getInstance().getReference()
                .child("Notify").child(idUser)
                .push().setValue(notification);
    }
}
