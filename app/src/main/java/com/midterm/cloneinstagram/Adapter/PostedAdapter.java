package com.midterm.cloneinstagram.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.midterm.cloneinstagram.Fragment.DetailPostFragment;
import com.midterm.cloneinstagram.Model.Post;
import com.midterm.cloneinstagram.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostedAdapter  extends RecyclerView.Adapter<PostedAdapter.ViewHolder> {
    private Context mContext;
    private List<Post> listImage;
    private FragmentActivity fragmentActivity;
    private String type;

    public PostedAdapter(Context mContext, List<Post> listImage, FragmentActivity fragmentActivity, String type) {
        this.mContext = mContext;
        this.listImage = listImage;
        this.fragmentActivity = fragmentActivity;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_cell_posted, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = listImage.get(position);
        Picasso.get().load(post.getPostimage()).into(holder.image_post_personal);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailPostFragment nextFrag= new DetailPostFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id",  post.getPostid());
                bundle.putString("idUser", post.getUsers().getUid());
                bundle.putString("type",  type);
                nextFrag.setArguments(bundle);

                FragmentTransaction fragmentTransaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                fragmentTransaction.replace(R.id.fragment_container, nextFrag, "findThisFragment")
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
        private ImageView image_post_personal;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_post_personal = itemView.findViewById(R.id.image_post_personal);

        }
    }
}
