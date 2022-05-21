package com.midterm.cloneinstagram.Fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm.cloneinstagram.Adapter.PostAdapter;
import com.midterm.cloneinstagram.Adapter.PostedAdapter;
import com.midterm.cloneinstagram.R;
import com.midterm.cloneinstagram.UpdateInformationActivity;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tvEditProfile;
    private PostedAdapter postedAdapter;
    private List<String> list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvEditProfile = view.findViewById(R.id.tv_edit_profile);
        tvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().startActivity(new Intent(getContext(), UpdateInformationActivity.class));
            }
        });
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
        postedAdapter = new PostedAdapter(getContext(), list);
        recyclerView.setAdapter(postedAdapter);
    }
}