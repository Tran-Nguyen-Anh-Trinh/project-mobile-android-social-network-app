package com.midterm.cloneinstagram.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.Fragment.DetailPostFragment;
import com.midterm.cloneinstagram.Fragment.ProfileUserFragment;
import com.midterm.cloneinstagram.Model.Notification;
import com.midterm.cloneinstagram.Model.Users;
import com.midterm.cloneinstagram.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {
    ArrayList<Notification> listNotify;
    public static Context context;
    public FragmentActivity fragmentActivity;

    public ActivityAdapter(ArrayList<Notification> listNotify, Context context, FragmentActivity fragmentActivity) {
        this.listNotify = listNotify;
        this.context = context;
        this.fragmentActivity = fragmentActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.notify_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = listNotify.get(position);
        FirebaseDatabase.getInstance().getReference().child("User").child(notification.getIdUser()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                String content = users.getName() + " " + notification.getContent()+". On "+ notification.getDate();
                holder.content.setText(content);
                Picasso.get().load(users.getImageUri()).into(holder.profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notification.getType().equals("follow")){
                    Bundle bundle = new Bundle();
                    bundle.putString("idUser", notification.getIdUser());
                    ProfileUserFragment nextFrag= new ProfileUserFragment();
                    nextFrag.setArguments(bundle);

                    fragmentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, nextFrag, "findThisFragment")
                            .addToBackStack(null)
                            .commit();
                } else {
                    DetailPostFragment nextFrag = new DetailPostFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", notification.getIdPost());
                    nextFrag.setArguments(bundle);

                    fragmentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, nextFrag, "findThisFragment")
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        if(notification.getType().equals("follow")
        && notification.getContent().equals("followed you")){
            holder.btn_follow.setVisibility(View.VISIBLE);
        }

        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.btn_follow.getText().equals("Follow")) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("User").child(notification.getIdUser()).child("follower")
                            .child(FirebaseAuth.getInstance().getUid())
                            .setValue(FirebaseAuth.getInstance().getUid());
                    FirebaseDatabase.getInstance().getReference()
                            .child("User").child(FirebaseAuth.getInstance().getUid())
                            .child("following").child(notification.getIdUser()).setValue(notification.getIdUser());
                    holder.btn_follow.setText("Following");
                    notifyApp(notification.getIdUser());
                } else {
                    FirebaseDatabase.getInstance().getReference()
                            .child("User").child(notification.getIdUser()).child("follower")
                            .child(FirebaseAuth.getInstance().getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference()
                            .child("User").child(FirebaseAuth.getInstance().getUid())
                            .child("following").child(notification.getIdUser()).removeValue();
                    holder.btn_follow.setText("Follow");
                    notifyApp2(notification.getIdUser());
                }
            }
        });

        FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getUid()).child("following").child(notification.getIdUser()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holder.btn_follow.setText("Following");
                } else {
                    if(notification.getIdUser().equals(FirebaseAuth.getInstance().getUid())){
                        holder.btn_follow.setText("You");
                        holder.btn_follow.setEnabled(false);
                    } else {
                        holder.btn_follow.setText("Follow");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return listNotify.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView content, btn_follow;
        CircleImageView profile;

        public ViewHolder(View view) {
            super(view);
            content = view.findViewById(R.id.content);
            btn_follow = view.findViewById(R.id.btn_follow);
            profile = view.findViewById(R.id.profile);
        }
    }

    static <T> List<T> reverse(final List<T> list) {
        final List<T> result = new ArrayList<>(list);
        Collections.reverse(result);
        return result;
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
