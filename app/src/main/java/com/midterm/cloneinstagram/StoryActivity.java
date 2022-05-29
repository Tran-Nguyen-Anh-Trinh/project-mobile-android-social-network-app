package com.midterm.cloneinstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.cloneinstagram.Model.OnSwipeTouchListener;
import com.midterm.cloneinstagram.Model.Story;
import com.midterm.cloneinstagram.Model.Users;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        View hold = findViewById(R.id.hold);
        hold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        hold.setOnTouchListener(new OnSwipeTouchListener(this){
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

        View reserve = findViewById(R.id.reserve);
        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storiesProgressView.reverse();
            }
        });
        reserve.setOnTouchListener(new OnSwipeTouchListener(this){
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

        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storiesProgressView.skip();
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
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(userid);
                reference.removeValue();
                storiesProgressView.skip();
                Toast.makeText(StoryActivity.this, "Deleted", Toast.LENGTH_SHORT).show();

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
}