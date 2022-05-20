package com.midterm.cloneinstagram;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    EditText email, username, password, confirm_password;
    TextView signUp, signIn;
    CircleImageView profile;

    Uri uri;
    byte[] bytes;
    ProgressDialog progressDialog;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        signUp = findViewById(R.id.signUp);
        profile = findViewById(R.id.profile);
        signIn = findViewById(R.id.signIn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(RegisterActivity.this, R.style.Dialogs);
                dialog.setContentView(R.layout.layout_option_image);
                TextView takePhoto;
                TextView chooseFromGallery;
                TextView cancel;
                takePhoto = dialog.findViewById(R.id.takePhoto);
                chooseFromGallery = dialog.findViewById(R.id.chooseFromGallery);
                cancel = dialog.findViewById(R.id.cancel_option);


                dialog.show();
                takePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 100);
                        dialog.dismiss();
                    }
                });
                chooseFromGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, 10);
                        dialog.dismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionSignUp();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==10){
            if(data!=null){
                bytes = null;
                uri = data.getData();
                profile.setImageURI(uri);
            }
        }
        if(requestCode==100){
            if(data!=null){
                if(data.getExtras()==null)  return;
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                profile.setImageBitmap(bitmap);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                bytes = byteArrayOutputStream.toByteArray();
                uri = null;
            }
        }
    }

    void functionSignUp(){
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        String usernameText = username.getText().toString();
        String confirmPasswordText = confirm_password.getText().toString();

        if(emailText.isEmpty()||passwordText.isEmpty()
                ||usernameText.isEmpty()||confirmPasswordText.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Please complete all information!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        if(!emailText.matches(emailPattern)){
            Toast.makeText(RegisterActivity.this, "Incorrect email format!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        if(!passwordText.equals(confirmPasswordText)){
            Toast.makeText(RegisterActivity.this, "Password does not match!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        if(passwordText.length() < 6){
            Toast.makeText(RegisterActivity.this, "Password must be 6 characters", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        progressDialog.show();
        auth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    DatabaseReference databaseReference = firebaseDatabase.getReference().child("User").child(auth.getUid());
                    StorageReference storageReference = firebaseStorage.getReference().child("Upload").child(auth.getUid());
                    if(uri!=null){
                        storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Users users = new Users(auth.getUid(), usernameText, emailText, uri.toString(), "");
                                            databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        progressDialog.dismiss();
                                                        Toast.makeText(RegisterActivity.this, "Created successfully!", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                    }else{
                                                        progressDialog.dismiss();
                                                        Toast.makeText(RegisterActivity.this, "An error occurred while sing up!", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });
                                        }
                                    });
                                }else{
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, "An error occurred while upload image!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else if(bytes!=null){
                        storageReference.putBytes(bytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Users users = new Users(auth.getUid(), usernameText,emailText, uri.toString(), "");
                                            databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        progressDialog.dismiss();
                                                        Toast.makeText(RegisterActivity.this, "Created successfully!", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                    }else{
                                                        progressDialog.dismiss();
                                                        Toast.makeText(RegisterActivity.this, "An error occurred while sing up!", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });
                    }else{
                        String linkImgProfile = "https://firebasestorage.googleapis.com/v0/b/tinstagram-82874.appspot.com/o/Profile-ICon.png?alt=media&token=f42430a4-3a26-477e-b9ee-a56765db1f26";
                        Users users = new Users(auth.getUid(), usernameText,emailText, linkImgProfile, "");
                        databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, "Created successfully!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                }else{
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, "An error occurred while sing up!", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }

                }else{
                    progressDialog.dismiss();

                    Toast.makeText(RegisterActivity.this, "An error occurred while sing up!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}