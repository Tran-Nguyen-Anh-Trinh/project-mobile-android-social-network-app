package com.midterm.cloneinstagram.Controller.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.Adapter.ActivityAdapter;
import com.midterm.cloneinstagram.Model.Notification;
import com.midterm.cloneinstagram.R;

import java.util.ArrayList;

public class ActivityFragment extends Fragment {
    RecyclerView rv_notify;
    ActivityAdapter adapter;
    private ArrayList<Notification> listNotify;

    private static ActivityFragment instance;

    public static ActivityFragment getInstance() {
        instance = new ActivityFragment();
        return instance;
    }
    private ActivityFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv_notify = view.findViewById(R.id.rv_notify);
        listNotify = new ArrayList<>();
        adapter = new ActivityAdapter(listNotify, getContext(), getActivity());
        rv_notify.setAdapter(adapter);
        rv_notify.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseDatabase.getInstance().getReference()
                .child("Notify").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            if(dataSnapshot.exists()){
                                Notification notification = dataSnapshot.getValue(Notification.class);
                                listNotify.add(0, notification);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activity, container, false);
    }
}