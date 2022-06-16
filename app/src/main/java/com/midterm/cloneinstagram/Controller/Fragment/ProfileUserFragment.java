package com.midterm.cloneinstagram.Controller.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.Adapter.PostedAdapter;
import com.midterm.cloneinstagram.Adapter.StroriedAdapter;
import com.midterm.cloneinstagram.Controller.Activity.ChatActivity;
import com.midterm.cloneinstagram.Model.Notification;
import com.midterm.cloneinstagram.Model.Post;
import com.midterm.cloneinstagram.Model.Storys;
import com.midterm.cloneinstagram.Model.Users;
import com.midterm.cloneinstagram.PushNotify.FCMSend;
import com.midterm.cloneinstagram.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileUserFragment extends Fragment {
    private String idUser;
    private RecyclerView recyclerView;
    private TextView tvFollow;
    private PostedAdapter postedAdapter;
    private List<Post> list;
    private CircleImageView profile;
    private TextView name;
    private TextView tv_posts;
    private TextView tv_followers;
    private TextView tv_following;
    private TextView tv_close;
    private StroriedAdapter storyAdapter;
    private List<Storys> storysList;
    private LinearLayout imageView, imageView1;
    private LinearLayout select, select1;
    private TextView messages;
    private Animation animZoomIn;

    private static ProfileUserFragment instance;

    public static ProfileUserFragment getInstance() {
        instance = new ProfileUserFragment();
        return instance;
    }
    private ProfileUserFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                idUser = bundle.getString("idUser");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvFollow = view.findViewById(R.id.tv_follow);
        recyclerView = view.findViewById(R.id.rv_posted);
        profile = view.findViewById(R.id.profile);
        name = view.findViewById(R.id.tv_name);
        tv_posts = view.findViewById(R.id.tv_posts);
        tv_followers = view.findViewById(R.id.tv_followers);
        tv_following = view.findViewById(R.id.tv_following);
        imageView = view.findViewById(R.id.imageView);
        imageView1 = view.findViewById(R.id.imageView1);
        messages = view.findViewById(R.id.tv_message);
        select = view.findViewById(R.id.select);
        select1 = view.findViewById(R.id.select1);
        tv_close = view.findViewById(R.id.tv_close);
        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        tvFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvFollow.getText().equals("Follow")) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("User").child(idUser).child("follower")
                            .child(FirebaseAuth.getInstance().getUid())
                            .setValue(FirebaseAuth.getInstance().getUid());
                    FirebaseDatabase.getInstance().getReference()
                            .child("User").child(FirebaseAuth.getInstance().getUid())
                            .child("following").child(idUser).setValue(idUser);
                    tvFollow.setText("Following");
                    notifyApp(idUser);
                } else {
                    FirebaseDatabase.getInstance().getReference()
                            .child("User").child(idUser).child("follower")
                            .child(FirebaseAuth.getInstance().getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference()
                            .child("User").child(FirebaseAuth.getInstance().getUid())
                            .child("following").child(idUser).removeValue();
                    tvFollow.setText("Follow");
                    notifyApp2(idUser);
                }
            }
        });

        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("User")
                        .child(FirebaseAuth.getInstance().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        FirebaseDatabase.getInstance().getReference().child("User")
                                .child(idUser)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Users users1 = snapshot.getValue(Users.class);
                                Intent intent = new Intent(getContext(), ChatActivity.class);
                                intent.putExtra("Uid", users1.getUid());
                                intent.putExtra("Name", users1.getName());
                                intent.putExtra("ReceiverImg", users1.getImageUri());
                                intent.putExtra("idSend", users.getUid());
                                intent.putExtra("nameSend", users.getName());
                                intent.putExtra("avaSend", users.getImageUri());
                                getContext().startActivity(intent);
                                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left_1);
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
            }
        });


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                int spanCount = 3;
                int spacing = 10;

                if (position >= 0) {
                    int column = position % spanCount; // item column

                    outRect.left = spacing - column * spacing / spanCount;
                    outRect.right = (column + 1) * spacing / spanCount;

                    if (position < spanCount) {
                        outRect.top = spacing;
                    }
                    outRect.bottom = spacing;
                } else {
                    outRect.left = 0;
                    outRect.right = 0;
                    outRect.top = 0;
                    outRect.bottom = 0;
                }
            }
        });
        list = new ArrayList<>();
        postedAdapter = new PostedAdapter(getContext(), list, getActivity(), "profileUser");
        recyclerView.setAdapter(postedAdapter);
        storysList = new ArrayList<>();
        storyAdapter = new StroriedAdapter(getContext(), storysList, getActivity());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readPost();
                select1.setVisibility(View.INVISIBLE);
                animZoomIn = AnimationUtils.loadAnimation(getContext(),
                        R.anim.zoom_in);
                animZoomIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        select.setVisibility(View.VISIBLE);
                        select.clearAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                select.startAnimation(animZoomIn);
            }
        });
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readStory();
                select.setVisibility(View.INVISIBLE);
                animZoomIn = AnimationUtils.loadAnimation(getContext(),
                        R.anim.zoom_in);
                animZoomIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        select1.setVisibility(View.VISIBLE);
                        select1.clearAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                select1.startAnimation(animZoomIn);
            }
        });

        tv_followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowFollowOther nextFrag = ShowFollowOther.getInstance();
                Bundle bundle = new Bundle();
                bundle.putString("follow", "followers");
                bundle.putString("idUser", idUser);
                nextFrag.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_out_down, R.anim.slide_up_dialog);
                fragmentTransaction.add(R.id.fragment_container, nextFrag)
                        .addToBackStack(null)
                        .commit();
            }
        });
        tv_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowFollowOther nextFrag = ShowFollowOther.getInstance();
                Bundle bundle = new Bundle();
                bundle.putString("follow", "following");
                bundle.putString("idUser", idUser);
                nextFrag.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_out_down, R.anim.slide_up_dialog);
                fragmentTransaction.add(R.id.fragment_container, nextFrag)
                        .addToBackStack(null)
                        .commit();
            }
        });

        readPost();
        getData();
    }

    private void readStory() {
        recyclerView.setAdapter(storyAdapter);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storysList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Storys storys = snapshot.getValue(Storys.class);
                    if (storys.getUsers().getUid().equals(idUser)) {
                        storysList.add(0, storys);
                    }
                }
                storyAdapter.notifyDataSetChanged();
                recyclerView.animate().translationX(recyclerView.getWidth())
                        .setDuration(0)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                recyclerView.animate().translationX(0).setDuration(400);
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readPost() {

        recyclerView.setAdapter(postedAdapter);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getUsers().getUid().equals(idUser)) {
                        list.add(0, post);
                    }
                }
                tv_posts.setText(list.size() + "");
                postedAdapter.notifyDataSetChanged();
                recyclerView.animate().translationX(recyclerView.getWidth()*-1)
                        .setDuration(0)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                recyclerView.animate().translationX(0).setDuration(400);
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getData() {


        FirebaseDatabase.getInstance().getReference().child("User").child(idUser).child("follower").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tv_followers.setText(snapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("User").child(idUser).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tv_following.setText(snapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("User").child(idUser).child("follower").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    tvFollow.setText("Following");
                } else {
                    if (idUser.equals(FirebaseAuth.getInstance().getUid())) {
                        tvFollow.setEnabled(false);
                        tvFollow.setText("You");
                    } else {
                        tvFollow.setText("Follow");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("User").child(idUser).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                Picasso.get().load(users.getImageUri()).into(profile);
                name.setText(users.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

        FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                String timeStamp = new SimpleDateFormat("HH:mm dd/MM/yyyy")
                        .format(Calendar.getInstance().getTime());
                FirebaseDatabase.getInstance().getReference().child("User").child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users1 = snapshot.getValue(Users.class);
                        FCMSend.pushNotification(getContext(), users1.getToken(), "Follow", users.getName()+": Followed you "+ timeStamp, "", "", "", "", "", "");
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

        FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                String timeStamp = new SimpleDateFormat("HH:mm dd/MM/yyyy")
                        .format(Calendar.getInstance().getTime());
                FirebaseDatabase.getInstance().getReference().child("User").child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users1 = snapshot.getValue(Users.class);
                        FCMSend.pushNotification(getContext(), users1.getToken(), "Follow", users.getName()+": Unfollowed you "+ timeStamp, "", "", "", "", "", "");
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
    }
}