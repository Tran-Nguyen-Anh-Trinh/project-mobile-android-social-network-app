package com.midterm.cloneinstagram.Controller.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.Adapter.UserAdapter;
import com.midterm.cloneinstagram.Model.Users;
import com.midterm.cloneinstagram.R;

import java.util.ArrayList;
import java.util.List;

public class ShowFollowFragment extends Fragment {
    private RecyclerView rv_user_follow;
    private UserAdapter adapter;
    private List<Users> usersList;
    private String checkFollow;
    private TextView title;
    private TextView btn_close;
    private String idPost;

    private static ShowFollowFragment instance;

    public static ShowFollowFragment getInstance() {
        instance = new ShowFollowFragment();
        return instance;
    }
    private ShowFollowFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            checkFollow = getArguments().getString("follow");
            idPost = getArguments().getString("idPost");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_follow, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv_user_follow = view.findViewById(R.id.rv_user_follow);
        btn_close = view.findViewById(R.id.btn_close);
        usersList = new ArrayList<>();
        adapter = new UserAdapter(getContext(), usersList, getActivity(), "1");
        rv_user_follow.setAdapter(adapter);
        rv_user_follow.setHasFixedSize(true);
        rv_user_follow.setLayoutManager(new LinearLayoutManager(getContext()));
        title = view.findViewById(R.id.title);
        if ("followers".equals(checkFollow)) {
            FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getUid())
                    .child("follower").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            usersList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String id = dataSnapshot.getValue(String.class);
                                FirebaseDatabase.getInstance().getReference().child("User").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Users users = snapshot.getValue(Users.class);
                                        usersList.add(users);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
        if ("following".equals(checkFollow)){
            title.setText("Followings");
            FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getUid())
                    .child("following").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            usersList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String id = dataSnapshot.getValue(String.class);
                                FirebaseDatabase.getInstance().getReference().child("User").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Users users = snapshot.getValue(Users.class);
                                        usersList.add(users);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
        if (idPost!=null){
            title.setText("Likes");
            FirebaseDatabase.getInstance().getReference().child("Post").child(idPost).child("like").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    usersList.clear();
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String id = dataSnapshot.getValue(String.class);
                        FirebaseDatabase.getInstance().getReference().child("User").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Users users = snapshot.getValue(Users.class);
                                usersList.add(users);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();

//                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
    }
}