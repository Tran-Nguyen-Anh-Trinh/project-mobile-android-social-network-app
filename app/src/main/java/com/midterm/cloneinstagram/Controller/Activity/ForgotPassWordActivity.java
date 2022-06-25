package com.midterm.cloneinstagram.Controller.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.midterm.cloneinstagram.R;

public class ForgotPassWordActivity extends AppCompatActivity {

    private TextView btn_reset, btn_close;
    private EditText email_reset;
    private final static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass_word);
        btn_reset = findViewById(R.id.btn_reset);
        btn_close = findViewById(R.id.btn_close);
        email_reset = findViewById(R.id.email_reset);
        relativeLayout = findViewById(R.id.hide_kb);

        addEvent();

        String email = getIntent().getStringExtra("email");
        if(email!=null){
            email_reset.setEnabled(false);
            email_reset.setText(email);
        }
    }

    private void addEvent() {
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAndRemoveTask();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_reset.getText().toString().trim();
                if (!email.matches(emailPattern)) {
                    email_reset.setError("Invalid Email");
                    return;
                }
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPassWordActivity.this, "Reset email instructions sent to " + email, Toast.LENGTH_LONG).show();
                            finishAndRemoveTask();
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        } else {
                            Toast.makeText(ForgotPassWordActivity.this, email + " does not exist", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                input.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAndRemoveTask();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}