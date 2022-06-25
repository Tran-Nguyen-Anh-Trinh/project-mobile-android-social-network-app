package com.midterm.cloneinstagram.Controller.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.Adapter.PostAdapter;
import com.midterm.cloneinstagram.Adapter.StoryAdapter;
import com.midterm.cloneinstagram.Controller.Activity.HomeChatActivity;
import com.midterm.cloneinstagram.Model.Post;
import com.midterm.cloneinstagram.Model.Storys;
import com.midterm.cloneinstagram.Controller.Activity.PostActivity;
import com.midterm.cloneinstagram.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postLists;

    private RecyclerView recyclerView_story;
    private StoryAdapter storyAdapter;
    private List<Storys> storyLists;
    private TextView notify;
    private ImageView imageView2;
    private ImageView addPost;
    private ImageView add_chat;
    private List<String> followingList;
    private TextView noti;
    private List<String> listUserID;

    private static HomeFragment instance;

    public static HomeFragment getInstance() {
        instance = new HomeFragment();
        return instance;
    }

    private HomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.homeFragment);
//        getActivity().getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        notify = view.findViewById(R.id.notify);

        imageView2 = view.findViewById(R.id.imageView2);
        addPost = view.findViewById(R.id.add_post);
        add_chat = view.findViewById(R.id.add_chat);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postLists, getActivity(), imageView2, getActivity());
        recyclerView.setAdapter(postAdapter);
        noti = view.findViewById(R.id.noti);

        recyclerView_story = view.findViewById(R.id.recycler_view_story);
        recyclerView_story.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView_story.setLayoutManager(linearLayoutManager1);
        storyLists = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(), storyLists, getActivity());
        recyclerView_story.setAdapter(storyAdapter);
        listUserID = new ArrayList<>();
        addEvent();
        checkFollowing();
        return view;
    }

    private void checkFollowing() {
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    followingList.add(snapshot.getKey());
                }
                readPost();
                readStory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postLists.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if(post.getUsers().getUid()!=null){
                        if (followingList.contains(post.getUsers().getUid())) {
                            postLists.add(post);
                        }
                        if (post.getUsers().getUid().equals(FirebaseAuth.getInstance().getUid())) {
                            postLists.add(post);
                        }
                    }
                }
                if (postLists.isEmpty()) {
                    notify.setVisibility(View.VISIBLE);
                } else {
                    notify.setVisibility(View.GONE);
                }

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readStory() {
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storyLists.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Storys storys = snapshot.getValue(Storys.class);
                    if(storys!=null){
                        if (followingList.contains(storys.getUsers().getUid())) {
                            if (timeStamp.equals(storys.getDate())) {
                                storyLists.add(0, storys);
                            }
                        }
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addEvent() {
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PostActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left_1);
            }
        });

        add_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), HomeChatActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left_1);
            }
        });


//        FirebaseDatabase.getInstance().getReference().child("Chats")
//                .orderByChild("IsRead").equalTo("true")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
//                            String keyRoom = dataSnapshot.getKey().substring(0, 28);
//                            if (keyRoom.equals(FirebaseAuth.getInstance().getUid())) {
//                                System.out.println("do chua xem");
//                                noti.setVisibility(View.VISIBLE);
//                                break;
//                            } else {
//                                System.out.println("do da xem");
//                                noti.setVisibility(View.INVISIBLE);
//
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
        FirebaseDatabase.getInstance().getReference().child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String keyRoom = dataSnapshot.getKey().substring(0, 28);
                    String check = dataSnapshot.child("IsRead").getValue(String.class);
                    if (keyRoom.equals(FirebaseAuth.getInstance().getUid())) {
                        if ("true".equals(check)) {
                            noti.setVisibility(View.VISIBLE);
                            break;
                        } else {
                            noti.setVisibility(View.INVISIBLE);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}