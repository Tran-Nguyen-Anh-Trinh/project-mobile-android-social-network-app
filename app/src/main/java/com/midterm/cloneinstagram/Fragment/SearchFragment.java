package com.midterm.cloneinstagram.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.R;
import com.midterm.cloneinstagram.SearchAdapter;
import com.midterm.cloneinstagram.UserAdapter;
import com.midterm.cloneinstagram.Users;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    RecyclerView recyclerView, recycleViewSearch;
    UserAdapter userAdapter;
    List<Users> mUsers;
    EditText searchbar;
    private SearchAdapter searchAdapter;
    private List<String> list;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        searchbar = view.findViewById(R.id.search_bar);
        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), mUsers);
        recyclerView.setAdapter(userAdapter);
        readUser();


        recycleViewSearch = view.findViewById((R.id.recycle_view_search));
        recycleViewSearch.setLayoutManager(new GridLayoutManager(getContext(), 3));
        list = new ArrayList<String>();
        searchAdapter = new SearchAdapter(getContext(), list);
        recycleViewSearch.setAdapter(searchAdapter);
        searchbar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUser(charSequence.toString().toLowerCase());
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }
    private void searchUser(String s){
        Query query = FirebaseDatabase.getInstance().getReference("User").orderByChild("name").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    mUsers.add(dataSnapshot.getValue(Users.class));
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void readUser(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (searchbar.getText().toString().equals("")){
                    mUsers.clear();
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        mUsers.add(dataSnapshot.getValue(Users.class));
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}