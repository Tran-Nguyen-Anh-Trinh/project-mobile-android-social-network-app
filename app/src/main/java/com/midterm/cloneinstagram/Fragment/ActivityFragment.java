package com.midterm.cloneinstagram.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm.cloneinstagram.ActivityAdapter;
import com.midterm.cloneinstagram.R;

import java.util.ArrayList;

public class ActivityFragment extends Fragment {
    RecyclerView rv1, rv2;
    ActivityAdapter aadapter;
    private ArrayList<Integer> lint;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv1 = view.findViewById(R.id.rv_1);
//        rv2 = view.findViewById(R.id.rv_2);
        lint = new ArrayList<>();
        lint.add(0);
        lint.add(1);
        lint.add(2);
        lint.add(3);
        lint.add(4);
        lint.add(5);
        aadapter = new ActivityAdapter(lint, getContext());

        rv1.setAdapter(aadapter);
        rv1.setLayoutManager(new LinearLayoutManager(getContext()));
//        rv2.setAdapter(aadapter);
//        rv2.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activity, container, false);
    }
}