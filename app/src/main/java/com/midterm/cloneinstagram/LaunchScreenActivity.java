package com.midterm.cloneinstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LaunchScreenActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lauch_screen);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(LaunchScreenActivity.this, MainActivity.class);
                LaunchScreenActivity.this.startActivity(mainIntent);
                LaunchScreenActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}