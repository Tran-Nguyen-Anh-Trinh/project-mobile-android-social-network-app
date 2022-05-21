package com.midterm.cloneinstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.midterm.cloneinstagram.Fragment.ActivityFragment;
import com.midterm.cloneinstagram.Fragment.HomeFragment;
import com.midterm.cloneinstagram.Fragment.NotificationFragment;
import com.midterm.cloneinstagram.Fragment.ProfileFragment;
import com.midterm.cloneinstagram.Fragment.SearchFragment;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        if(mAuth.getCurrentUser() == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_search:
                            selectedFragment = new SearchFragment();
                            break;
                        case R.id.nav_add:
                            selectedFragment = null;
                            startActivity(new Intent(MainActivity.this, PostActivity.class));
                            break;
                        case R.id.nav_heart:
                            selectedFragment = new ActivityFragment();
                            break;
                        case R.id.nav_profile:
                            SharedPreferences.Editor editor = getSharedPreferences("PRESS", MODE_PRIVATE).edit();
                            editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();
                            selectedFragment = new ProfileFragment();
                            break;
                    }
                    if(selectedFragment!=null){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    }
                    return true;
                }
            };
}