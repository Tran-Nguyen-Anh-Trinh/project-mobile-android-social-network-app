package com.midterm.cloneinstagram.Fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.Adapter.PostAdapter;
import com.midterm.cloneinstagram.Adapter.PostedAdapter;
import com.midterm.cloneinstagram.Adapter.StoryAdapter;
import com.midterm.cloneinstagram.Adapter.StroriedAdapter;
import com.midterm.cloneinstagram.Model.Post;
import com.midterm.cloneinstagram.Model.Storys;
import com.midterm.cloneinstagram.Model.Users;
import com.midterm.cloneinstagram.R;
import com.midterm.cloneinstagram.UpdateInformationActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tvEditProfile;
    private PostedAdapter postedAdapter;
    private List<Post> list;
    private StroriedAdapter storyAdapter;
    private List<Storys> storysList;
    private LinearLayout imageView, imageView1;
    private LinearLayout select, select1;
    private CircleImageView profile;
    private TextView name;
    private TextView tv_posts;
    private TextView tv_followers;
    private TextView tv_following;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        tvEditProfile = view.findViewById(R.id.tv_edit_profile);

        imageView = view.findViewById(R.id.imageView);
        imageView1 = view.findViewById(R.id.imageView1);

        select = view.findViewById(R.id.select);
        select1 = view.findViewById(R.id.select1);

        recyclerView = view.findViewById(R.id.rv_posted);
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
        storysList = new ArrayList<>();
        postedAdapter = new PostedAdapter(getContext(), list, getActivity(), "profile");
        storyAdapter = new StroriedAdapter(getContext(), storysList, getActivity());
        recyclerView.setAdapter(postedAdapter);

        profile = view.findViewById(R.id.profile);
        name = view.findViewById(R.id.tv_name);
        tv_posts = view.findViewById(R.id.tv_posts);
        tv_followers = view.findViewById(R.id.tv_followers);
        tv_following = view.findViewById(R.id.tv_following);
        readPost();
        updateDataUser();
        addEvent();
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void addEvent() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select.setVisibility(View.VISIBLE);
                select1.setVisibility(View.INVISIBLE);
                readPost();
            }
        });
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select.setVisibility(View.INVISIBLE);
                select1.setVisibility(View.VISIBLE);
                readStory();
            }
        });
        tvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().startActivity(new Intent(getContext(), UpdateInformationActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_out_down, R.anim.slide_up_dialog);

            }
        });
        tv_followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowFollowFragment nextFrag = new ShowFollowFragment();
                Bundle bundle = new Bundle();
                bundle.putString("follow", "followers");
                nextFrag.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_out_down, R.anim.slide_up_dialog);
                fragmentTransaction.replace(R.id.fragment_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        tv_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowFollowFragment nextFrag = new ShowFollowFragment();
                Bundle bundle = new Bundle();
                bundle.putString("follow", "following");
                nextFrag.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_out_down, R.anim.slide_up_dialog);
                fragmentTransaction.replace(R.id.fragment_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
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
                    if (post.getUsers().getUid().equals(Users.getInstance().getUid())) {
                        list.add(0, post);
                    }
                }
                if (tv_posts.getText().toString().isEmpty()) {
                    tv_posts.setText(list.size() + "");
                }
                postedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                    if (storys.getUsers().getUid().equals(Users.getInstance().getUid())) {
                        storysList.add(0, storys);
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateDataUser() {
        FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                Users.getInstance().setUid(users.getUid());
                Users.getInstance().setEmail(users.getEmail());
                Users.getInstance().setName(users.getName());
                Users.getInstance().setImageUri(users.getImageUri());
                Users.getInstance().setStatus(users.getStatus());
                Picasso.get().load(Users.getInstance().getImageUri()).into(profile);
                name.setText(Users.getInstance().getName());

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Post post = snapshot.getValue(Post.class);
                            if (post.getUsers().getUid().equals(Users.getInstance().getUid())) {
                                FirebaseDatabase.getInstance().getReference("Post").child(snapshot.getKey()).child("users").setValue(Users.getInstance());
                                System.out.println(snapshot.getKey().toString());
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
        FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getUid()).child("follower").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tv_followers.setText(snapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tv_following.setText(snapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}