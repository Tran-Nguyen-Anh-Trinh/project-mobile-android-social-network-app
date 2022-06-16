package com.midterm.cloneinstagram.Controller.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.midterm.cloneinstagram.Controller.Fragment.ActivityFragment;
import com.midterm.cloneinstagram.Controller.Fragment.HomeFragment;
import com.midterm.cloneinstagram.Controller.Fragment.ProfileFragment;
import com.midterm.cloneinstagram.Controller.Fragment.SearchFragment;
import com.midterm.cloneinstagram.Model.Users;
import com.midterm.cloneinstagram.R;

public class MainActivity extends AppCompatActivity implements LifecycleObserver {

    long pressedTime;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;
    String token;
    String checkFragment;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        if (!isInternetAvailable()) {
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();

                alertDialog.setTitle("Info");
                alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again");
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finishAndRemoveTask();
                        return;
                    }
                });

                alertDialog.show();
            } catch (Exception e) {
            }
        }

        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left_1);
            return;
        }


        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                token = task.getResult();
                FirebaseDatabase.getInstance().getReference("User/" + mAuth.getUid() + "/token").setValue(token);
            }
        });

        FirebaseDatabase.getInstance().getReference().child("User").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                Users.getInstance().setUid(users.getUid());
                Users.getInstance().setEmail(users.getEmail());
                Users.getInstance().setName(users.getName());
                Users.getInstance().setImageUri(users.getImageUri());
                Users.getInstance().setStatus(users.getStatus());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference()
                .child("Notify").child(FirebaseAuth.getInstance().getUid())
                .child("isRead")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            bottomNavigationView.getOrCreateBadge(R.id.nav_heart).setBackgroundColor(getResources().getColor(R.color.red));
                        } else {
                            bottomNavigationView.getOrCreateBadge(R.id.nav_heart).setBackgroundColor(getResources().getColor(R.color.white));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);


        checkFragment = getIntent().getStringExtra("activity");
        if ("true".equals(checkFragment)) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
            fragmentTransaction.addToBackStack(null).replace(R.id.fragment_container, ActivityFragment.getInstance()).commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_heart);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, HomeFragment.getInstance()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = HomeFragment.getInstance();
                            break;
                        case R.id.nav_search:
                            selectedFragment = SearchFragment.getInstance();
                            break;
                        case R.id.nav_heart:
//                            selectedFragment = new NotificationFragment();
                            FirebaseDatabase.getInstance().getReference()
                                    .child("Notify").child(FirebaseAuth.getInstance().getUid())
                                    .child("isRead").removeValue();
                            selectedFragment = ActivityFragment.getInstance();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = ProfileFragment.getInstance();
                            break;
                    }
                    if(selectedFragment!=null){
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
                        fragmentTransaction.addToBackStack(null).replace(R.id.fragment_container, selectedFragment)
                                .commit();
                    }
                    return true;
                }
            };

    @Override
    public void onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    void updateStatus(String status) {
        DatabaseReference databaseReference = database.getReference().child("User").child(mAuth.getUid()).child("status");
        databaseReference.setValue(status);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
        updateStatus("offline");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
        updateStatus("online");
    }

}