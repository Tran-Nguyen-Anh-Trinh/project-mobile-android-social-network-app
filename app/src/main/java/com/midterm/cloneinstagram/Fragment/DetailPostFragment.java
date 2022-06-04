package com.midterm.cloneinstagram.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.CommentActivity;
import com.midterm.cloneinstagram.DetailPost;
import com.midterm.cloneinstagram.LoginActivity;
import com.midterm.cloneinstagram.Model.Notification;
import com.midterm.cloneinstagram.Model.Post;
import com.midterm.cloneinstagram.Model.Users;
import com.midterm.cloneinstagram.PostActivity;
import com.midterm.cloneinstagram.PushNotify.FCMSend;
import com.midterm.cloneinstagram.R;
import com.midterm.cloneinstagram.UpdateInformationActivity;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DetailPostFragment extends Fragment {
    public TextView username, likes, publisher, description, comments, delete, back , date, btn_edit_post;
    String idPost;
    String type;
    Post post;
    ImageView imageView2;
    public ImageView image_profile, post_image, like, likeed,  comment, save;
    private static long LAST_CLICK_TIME = 0;
    private final int mDoubleClickInterval = 400; // Milliseconds
    public DetailPostFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idPost = getArguments().getString("id");
            type = getArguments().getString("type");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        image_profile = view.findViewById(R.id.image_profile);
        post_image = view.findViewById(R.id.post_image);
        comments = view.findViewById(R.id.comments);
        likes = view.findViewById(R.id.likes);
        comment = view.findViewById(R.id.comment);
        save = view.findViewById(R.id.save);
        username = view.findViewById(R.id.username);
        publisher = view.findViewById(R.id.publisher);
        like  = view.findViewById(R.id.like);
        description = view.findViewById(R.id.description);
        delete = view.findViewById(R.id.btn_delete_post);
        back = view.findViewById(R.id.back);
        date = view.findViewById(R.id.date);
        back.setVisibility(View.VISIBLE);
        likeed  = view.findViewById(R.id.likeed);
        imageView2 = view.findViewById(R.id.imageView2);
        btn_edit_post  = view.findViewById(R.id.btn_edit_post);

        setData();
        addEvent();
    }


    private void setData(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post").child(idPost);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    post = snapshot.getValue(Post.class);
                    if(post.getUsers().getUid().equals(FirebaseAuth.getInstance().getUid())){
                        delete.setVisibility(View.VISIBLE);
                        btn_edit_post.setVisibility(View.VISIBLE);
                    }
                    getData();
                }else{
                    if(delete.getVisibility()==View.GONE) {
                        Toast.makeText(getContext(), "Post does not exist", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStackImmediate();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Post").child(idPost).child("comment").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    comments.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void getData(){
        username.setText(post.getUsers().getName());
        Picasso.get().load(post.getUsers().getImageUri()).into(image_profile);
        Picasso.get().load(post.getPostimage()).into(post_image);

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("type", "home");
                bundle.putString("idUser", post.getUsers().getUid());
                ProfileUserFragment nextFrag = new ProfileUserFragment();
                nextFrag.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                fragmentTransaction.replace(R.id.fragment_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("type", "home");
                bundle.putString("idUser", post.getUsers().getUid());
                ProfileUserFragment nextFrag = new ProfileUserFragment();
                nextFrag.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                fragmentTransaction.replace(R.id.fragment_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long doubleClickCurrentTime = System.currentTimeMillis();
                long currentClickTime = System.currentTimeMillis();
                if (currentClickTime - LAST_CLICK_TIME <= mDoubleClickInterval) {
                    String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
                    String idNotify = new SimpleDateFormat("yyyyMMdd_HHmmssss").format(Calendar.getInstance().getTime());
                    idNotify += System.currentTimeMillis();
                    if (likeed.getVisibility() == View.GONE) {
                        imageView2.setVisibility(View.VISIBLE);
                        imageView2.animate().scaleX(3.0f).scaleY(3.0f)
                                .alpha(1.0f)
                                .setDuration(300)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        imageView2.animate().scaleX(0.5f).scaleY(0.5f)
                                                .alpha(0.0f)
                                                .setDuration(700)
                                                .setListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        super.onAnimationEnd(animation);
                                                        imageView2.setVisibility(View.GONE);
                                                    }
                                                });
                                    }
                                });


                        likeed.setVisibility(View.VISIBLE);
                        FirebaseDatabase.getInstance().getReference()
                                .child("Post").child(post.getPostid()).child("like").child(FirebaseAuth.getInstance().getUid())
                                .setValue(FirebaseAuth.getInstance().getUid());
                        Notification notification = new Notification();
                        notification.setIdNotify(idNotify);
                        notification.setIdUser(FirebaseAuth.getInstance().getUid());
                        notification.setType("like");
                        notification.setDate(timeStamp);
                        notification.setContent("liked your photo");
                        notification.setIdPost(post.getPostid());
                        notification.setIdPostLike(post.getPostid());

                        if (!post.getUsers().getUid().equals(FirebaseAuth.getInstance().getUid())) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("Notify").child(post.getUsers().getUid())
                                    .child("isRead").push()
                                    .setValue(post.getUsers().getUid());
                            FirebaseDatabase.getInstance().getReference()
                                    .child("Notify").child(post.getUsers().getUid())
                                    .push().setValue(notification);
                            FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Users users = snapshot.getValue(Users.class);
                                    String timeStamp = new SimpleDateFormat("HH:mm dd/MM/yyyy")
                                            .format(Calendar.getInstance().getTime());
                                    FirebaseDatabase.getInstance().getReference().child("User").child(post.getUsers().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Users users1 = snapshot.getValue(Users.class);
                                            FCMSend.pushNotification(getContext(), users1.getToken(), "Post", users.getName()+": Liked your post on "+ timeStamp, "", "", "", "", "", "");
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    } else {
                        likeed.setVisibility(View.GONE);
                        FirebaseDatabase.getInstance().getReference()
                                .child("Notify").child(post.getUsers().getUid())
                                .child("isRead").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            dataSnapshot.getRef().removeValue();
                                            break;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                        FirebaseDatabase.getInstance().getReference()
                                .child("Notify").child(post.getUsers().getUid()).orderByChild("idPostLike").equalTo(post.getPostid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            dataSnapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                        FirebaseDatabase.getInstance().getReference()
                                .child("Post").child(post.getPostid()).child("like")
                                .child(FirebaseAuth.getInstance().getUid()).removeValue();
                    }
                }
                else
                {
                    LAST_CLICK_TIME = System.currentTimeMillis();
                }
            }
        });

        date.setText(post.getDate());
        publisher.setText(post.getUsers().getName());
        if(post.getDescription().equals("")) {
            description.setVisibility(View.GONE);
        }else {
            description.setVisibility(View.VISIBLE);
            description.setText(post.getDescription());
        }
        FirebaseDatabase.getInstance().getReference().child("Post").child(idPost).child("like").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("Post").child(idPost).child("like")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            likeed.setVisibility(View.VISIBLE);
                        } else {
                            likeed.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
                String idNotify = new SimpleDateFormat("yyyyMMdd_HHmmssss").format(Calendar.getInstance().getTime());
                idNotify += System.currentTimeMillis();
                if (likeed.getVisibility() == View.GONE) {
                    likeed.setVisibility(View.VISIBLE);
                    FirebaseDatabase.getInstance().getReference()
                            .child("Post").child(post.getPostid()).child("like").child(FirebaseAuth.getInstance().getUid())
                            .setValue(FirebaseAuth.getInstance().getUid());
                    Notification notification = new Notification();
                    notification.setIdNotify(idNotify);
                    notification.setIdUser(FirebaseAuth.getInstance().getUid());
                    notification.setType("like");
                    notification.setDate(timeStamp);
                    notification.setContent("liked your photo");
                    notification.setIdPost(post.getPostid());
                    notification.setIdPostLike(post.getPostid());

                    if (!post.getUsers().getUid().equals(FirebaseAuth.getInstance().getUid())) {
                        FirebaseDatabase.getInstance().getReference()
                                .child("Notify").child(post.getUsers().getUid())
                                .child("isRead").push()
                                .setValue(post.getUsers().getUid());
                        FirebaseDatabase.getInstance().getReference()
                                .child("Notify").child(post.getUsers().getUid())
                                .push().setValue(notification);
                        FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Users users = snapshot.getValue(Users.class);
                                String timeStamp = new SimpleDateFormat("HH:mm dd/MM/yyyy")
                                        .format(Calendar.getInstance().getTime());
                                FirebaseDatabase.getInstance().getReference().child("User").child(post.getUsers().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Users users1 = snapshot.getValue(Users.class);
                                        FCMSend.pushNotification(getContext(), users1.getToken(), "Post", users.getName()+": Liked your post on "+ timeStamp, "", "", "", "", "", "");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                } else {
                    likeed.setVisibility(View.GONE);
                    FirebaseDatabase.getInstance().getReference()
                            .child("Notify").child(post.getUsers().getUid())
                            .child("isRead").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        dataSnapshot.getRef().removeValue();
                                        break;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                    FirebaseDatabase.getInstance().getReference()
                            .child("Notify").child(post.getUsers().getUid()).orderByChild("idPostLike").equalTo(post.getPostid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        dataSnapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                    FirebaseDatabase.getInstance().getReference()
                            .child("Post").child(post.getPostid()).child("like")
                            .child(FirebaseAuth.getInstance().getUid()).removeValue();
                }
            }
        });

        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CommentActivity.class);
                intent.putExtra("id", post.getPostid());
                intent.putExtra("idUser", post.getUsers().getUid());
                getContext().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left_1);

            }
        });
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CommentActivity.class);
                intent.putExtra("id", post.getPostid());
                intent.putExtra("idUser", post.getUsers().getUid());
                getContext().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left_1);

            }
        });
    }

    private void addEvent(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type.equals("profile")){
                    ProfileFragment nextFrag = new ProfileFragment();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                    fragmentTransaction.replace(R.id.fragment_container, nextFrag, "findThisFragment")
                            .addToBackStack(null)
                            .commit();
                }else if(type.equals("home")){
                    getActivity().getSupportFragmentManager().popBackStack();
                } else if(type.equals("search")){
                    getActivity().getSupportFragmentManager().popBackStack();
                } else if(type.equals("activity")){
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                else{
                    getActivity().getSupportFragmentManager().popBackStack();
                }


//                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

        likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowFollowFragment nextFrag= new ShowFollowFragment();
                Bundle bundle = new Bundle();
                bundle.putString("idPost", post.getPostid());
//                bundle.putString("type", "detail");
                bundle.putString("type",  type);
                bundle.putString("typeTemp",  "fromPost");
                String idUser = getArguments().getString("idUser");
                bundle.putString("idUser", idUser);
                nextFrag.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_out_down, R.anim.slide_up_dialog);
                fragmentTransaction.add(R.id.fragment_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getContext(), R.style.Dialogs);
                dialog.setContentView(R.layout.dialog_delete);
                TextView btnYes;
                TextView btnNo;
                btnYes = dialog.findViewById(R.id.button_Yes);
                btnNo = dialog.findViewById(R.id.button_No);
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post").child(idPost);
                        reference.removeValue();
                        Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                        HomeFragment nextFrag = new HomeFragment();
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.slide_out_down, R.anim.slide_up_dialog);
                        fragmentTransaction.replace(R.id.fragment_container, nextFrag, "findThisFragment")
                                .addToBackStack(null)
                                .commit();
                    }
                });
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });


        btn_edit_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PostActivity.class);
                intent.putExtra("idPost", idPost);
                getContext().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_out_down, R.anim.slide_up_dialog);
            }
        });

    }
}