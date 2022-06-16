package com.midterm.cloneinstagram.Adapter;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm.cloneinstagram.Controller.Fragment.DetailPostFragment;
import com.midterm.cloneinstagram.Model.Post;
import com.midterm.cloneinstagram.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{
    private Context mContext;
    private List<Post> listImage;
    public FragmentActivity fragmentActivity;

    public SearchAdapter(Context mContext, List<Post> listImage, FragmentActivity fragmentActivity) {
        this.mContext = mContext;
        this.listImage = listImage;
        this.fragmentActivity = fragmentActivity;
    }
    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_search_item, parent, false);
        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        Post post = listImage.get(position);
        Picasso.get().load(post.getPostimage()).into(holder.all_image_personal);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = fragmentActivity.getCurrentFocus();
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager) fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                DetailPostFragment nextFrag= DetailPostFragment.getInstance();
                Bundle bundle = new Bundle();
                bundle.putString("id",  post.getPostid());
                bundle.putString("type",  "search");
                nextFrag.setArguments(bundle);

                FragmentTransaction fragmentTransaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                fragmentTransaction.add(R.id.fragment_container, nextFrag)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listImage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView all_image_personal;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            all_image_personal = itemView.findViewById(R.id.image_seach);

        }
    }
}
