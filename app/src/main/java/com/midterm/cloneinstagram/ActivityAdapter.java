package com.midterm.cloneinstagram;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {
    ArrayList<Integer> list;
    public static Context context;

    public ActivityAdapter(ArrayList<Integer> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.activity_1_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityAdapter.ViewHolder holder, int position) {
        Log.d("DEBUG1", "" + list.get(position));
//        final Contact currContact = Contact.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DEBUG1", "Đến trang " + list.get(position));
            }
        });

        if(list.get(position) % 3 == 0){
            holder.lo1.setVisibility(View.VISIBLE);
            holder.lo2.setVisibility(View.GONE);
            holder.lo3.setVisibility(View.GONE);
//            holder.tv1.setText("123");
        }

        if(list.get(position) % 3 == 1){
            holder.lo1.setVisibility(View.GONE);
            holder.lo2.setVisibility(View.VISIBLE);
            holder.lo3.setVisibility(View.GONE);
//            holder.tv2.setText("123");
        }

        if(list.get(position) % 3 == 2){
            holder.lo1.setVisibility(View.GONE);
            holder.lo2.setVisibility(View.GONE);
            holder.lo3.setVisibility(View.VISIBLE);
//            holder.tv3.setText("123");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout lo1, lo2, lo3;
        TextView tv1, tv2, tv3;

        public ViewHolder(View view) {
            super(view);
            lo1 = view.findViewById(R.id.lo1);
            lo2 = view.findViewById(R.id.lo2);
            lo3 = view.findViewById(R.id.lo3);

            tv1 = view.findViewById(R.id.tv1);
            tv2 = view.findViewById(R.id.tv2);
            tv3 = view.findViewById(R.id.tv3);
        }
    }
}
