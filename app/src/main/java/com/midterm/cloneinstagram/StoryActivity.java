package com.midterm.cloneinstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.Model.Messages;
import com.midterm.cloneinstagram.Model.OnSwipeTouchListener;
import com.midterm.cloneinstagram.Model.Story;
import com.midterm.cloneinstagram.Model.Users;
import com.midterm.cloneinstagram.PushNotify.FCMSend;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    int counter = 0;
    long pressTime = 0L;
    long limit = 300L;

    StoriesProgressView storiesProgressView;
    ImageView image, story_photo;
    TextView story_username;

    List<String> images;
    List<String> storyids;
    String userid;
    String storyid;
    String name;
    String imageAvatar;
    String imageStory;
    ImageView delete;
    ImageView sent;
    EditText content;
    String senderRoom;
    String receiverRoom;
    RelativeLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        linearLayout = findViewById(R.id.sum);
        content = findViewById(R.id.content);
        content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    storiesProgressView.pause();
                    System.out.println("qqqqqqqqqqq");
                }else {
                    storiesProgressView.resume();
                    System.out.println("llllllllllllll");
                }
            }
        });


        sent = findViewById(R.id.send);

        View hold = findViewById(R.id.hold);
        hold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        hold.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                }
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
        });

        View reserve = findViewById(R.id.reserve);
        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                storiesProgressView.reverse();
            }
        });
        reserve.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                }
                long now = System.currentTimeMillis();
                storiesProgressView.resume();
                return limit < now - pressTime;
            }
        });

        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                storiesProgressView.skip();
                finishAndRemoveTask();
                overridePendingTransition(R.anim.slide_out_down, R.anim.slide_up_dialog);
            }
        });
        skip.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                }
                long now = System.currentTimeMillis();
                storiesProgressView.resume();
                return limit < now - pressTime;
            }
        });



        storiesProgressView = findViewById(R.id.stories);
        image = findViewById(R.id.image);
        delete = findViewById(R.id.delete);
        story_photo = findViewById(R.id.story_photo);
        story_username = findViewById(R.id.story_username);

        userid = getIntent().getStringExtra("userid");
        storyid = getIntent().getStringExtra("storyid");
        name = getIntent().getStringExtra("name");
        imageAvatar = getIntent().getStringExtra("image");
        imageStory = getIntent().getStringExtra("imageStory");


        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messages = "Replied to your story: " + content.getText().toString().trim();
                if (messages.isEmpty()) {
                    Toast.makeText(StoryActivity.this, "Please enter a messages!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Date now = new Date();
                String format = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(now);
                content.setText("");
                saveMessagesChat(messages, "", "");
                FirebaseDatabase.getInstance().getReference().child("User").child(userid)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Users usersReceive = snapshot.getValue(Users.class);
                                if(!usersReceive.getUid().equals(FirebaseAuth.getInstance().getUid())){
                                    FCMSend.pushNotification(StoryActivity.this, usersReceive.getToken(), "Messages", Users.getInstance().getName()+": "+messages + " on " + format,
                                            Users.getInstance().getUid(),
                                            Users.getInstance().getName(), Users.getInstance().getImageUri(), userid,
                                            name, imageAvatar);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Story").child(userid).child("isSeen")
                .child(FirebaseAuth.getInstance().getUid())
                .setValue(FirebaseAuth.getInstance().getUid());
        story_username.setText(name);
        Picasso.get().load(imageAvatar).into(story_photo);
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                image.setImageBitmap(bitmap);
                storiesProgressView.setStoriesCount(1);
                storiesProgressView.setStoryDuration(5000L);
                storiesProgressView.setStoriesListener(StoryActivity.this);
                storiesProgressView.startStories(counter);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.get().load(imageStory).into(target);


        addEvent();
    }

    private void addEvent(){
        if(userid.equals(FirebaseAuth.getInstance().getUid())){
            delete.setVisibility(View.VISIBLE);
        }
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storiesProgressView.pause();
                Dialog dialog = new Dialog(StoryActivity.this, R.style.Dialogs);
                dialog.setContentView(R.layout.dialog_delete);
                TextView btnYes;
                TextView btnNo;
                btnYes = dialog.findViewById(R.id.button_Yes);
                btnNo = dialog.findViewById(R.id.button_No);
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(userid);
                        reference.removeValue();
                        storiesProgressView.skip();
                        Toast.makeText(StoryActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        storiesProgressView.resume();
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
    }

    @Override
    public void onNext() {

    }

    @Override
    public void onPrev() {
        if((counter - 1) < 0) return;
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_down, R.anim.slide_up_dialog);

    }

    private void saveMessagesChat(String messages, String img, String vid) {
        Date now = new Date();
        String format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.ENGLISH).format(now);
        Messages messages1;
        messages1 = new Messages(messages, FirebaseAuth.getInstance().getUid(), img, vid, format);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        senderRoom = FirebaseAuth.getInstance().getUid() + userid;
        receiverRoom = userid + FirebaseAuth.getInstance().getUid();
        if (FirebaseAuth.getInstance().getUid().equals(userid)) {
            database.getReference().child("Chats")
                    .child(senderRoom)
                    .child("Messages")
                    .push()
                    .setValue(messages1);
        } else {
            database.getReference().child("Chats")
                    .child(senderRoom)
                    .child("Messages")
                    .push()
                    .setValue(messages1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            database.getReference().child("Chats")
                                    .child(receiverRoom)
                                    .child("Messages")
                                    .push().setValue(messages1);

                        }
                    });
            database.getReference().child("Chats")
                    .child(receiverRoom)
                    .child("IsRead").setValue("true");
        }
    }
}