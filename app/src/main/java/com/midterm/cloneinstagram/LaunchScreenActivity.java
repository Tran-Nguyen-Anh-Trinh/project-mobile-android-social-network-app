package com.midterm.cloneinstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LauchScreenActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lauch_screen);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(LauchScreenActivity.this, MainActivity.class);
                LauchScreenActivity.this.startActivity(mainIntent);
                LauchScreenActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}