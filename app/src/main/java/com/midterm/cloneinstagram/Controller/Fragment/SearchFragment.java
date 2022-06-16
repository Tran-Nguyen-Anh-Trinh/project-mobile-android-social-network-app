package com.midterm.cloneinstagram.Controller.Fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.Adapter.UserAdapter;
import com.midterm.cloneinstagram.Model.Post;
import com.midterm.cloneinstagram.Model.Users;
import com.midterm.cloneinstagram.R;
import com.midterm.cloneinstagram.Adapter.SearchAdapter;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView recycleViewSearch;
    private SearchAdapter searchAdapter;
    private List<Post> list;
    private EditText searchbar;
    private ImageView delete;
    private List<Users> mUsers;
    private UserAdapter userAdapter;
    private LinearLayout linearLayout;
    private long ms_press;

    private static SearchFragment instance;

    public static SearchFragment getInstance() {
        instance = new SearchFragment();
        return instance;
    }
    private SearchFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        delete = view.findViewById(R.id.btn_delete);
        searchbar = view.findViewById(R.id.search_bar);
        linearLayout = view.findViewById(R.id.hide);
        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), mUsers, getActivity(), "0");
        recycleViewSearch = view.findViewById(R.id.recycle_view_search);
        recycleViewSearch.setAdapter(userAdapter);
        recycleViewSearch.setHasFixedSize(true);
        recycleViewSearch.setLayoutManager(new LinearLayoutManager(getContext()));

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchbar.setText("");
                searchbar.clearFocus();
            }
        });

        searchbar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        searchbar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == keyEvent.KEYCODE_DEL && searchbar.getText().toString().isEmpty()){
                    mUsers.clear();
                    userAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        searchbar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().isEmpty()){
                    mUsers.clear();
                    userAdapter.notifyDataSetChanged();
                    delete.setVisibility(View.INVISIBLE);

                } else {
                    delete.setVisibility(View.VISIBLE);
                    searchUser(charSequence.toString().toLowerCase());
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        return view;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void searchUser(String key) {
        FirebaseDatabase.getInstance().getReference().child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);
                    if(users.getName().toLowerCase().contains(key)){
                        mUsers.add(users);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycle_view);
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
        searchAdapter = new SearchAdapter(getContext(), list, getActivity());
        recyclerView.setAdapter(searchAdapter);
        readPost();
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager input = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                input.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });

        recycleViewSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager input = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                input.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });
    }

    private void readPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    list.add(0, post);
                }
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}