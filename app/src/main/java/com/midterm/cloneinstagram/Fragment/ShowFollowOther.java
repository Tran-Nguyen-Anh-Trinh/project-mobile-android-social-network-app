package com.midterm.cloneinstagram.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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

public class ShowFollowOther extends Fragment {

    private RecyclerView rv_user_follow;
    private UserAdapter adapter;
    private List<Users> usersList;
    private String checkFollow;
    private TextView title;
    private TextView btn_close;
    private String idPost;
    private String idUser;

    public ShowFollowOther() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            checkFollow = getArguments().getString("follow");
            idPost = getArguments().getString("idPost");
            idUser = getArguments().getString("idUser");
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
        adapter = new UserAdapter(getContext(), usersList, getActivity());
        rv_user_follow.setAdapter(adapter);
        rv_user_follow.setHasFixedSize(true);
        rv_user_follow.setLayoutManager(new LinearLayoutManager(getContext()));
        title = view.findViewById(R.id.title);
        if ("followers".equals(checkFollow)) {
            FirebaseDatabase.getInstance().getReference().child("User").child(idUser)
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
            title.setText("List following");
            FirebaseDatabase.getInstance().getReference().child("User").child(idUser)
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
            title.setText("Like");
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

                ProfileUserFragment nextFrag = new ProfileUserFragment();
                Bundle bundle = new Bundle();
                bundle.putString("idUser", idUser);
                nextFrag.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_out_down, R.anim.slide_up_dialog);
                fragmentTransaction.replace(R.id.fragment_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();

//                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
    }
}