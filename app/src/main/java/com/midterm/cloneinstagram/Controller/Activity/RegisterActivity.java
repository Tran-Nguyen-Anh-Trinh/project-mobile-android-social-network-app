package com.midterm.cloneinstagram.Controller.Activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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
import com.midterm.cloneinstagram.Model.Users;
import com.midterm.cloneinstagram.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    long pressedTime;
    EditText email, username, password, confirm_password;
    TextView signUp, signIn;
    CircleImageView profile;
    LinearLayout linearLayout;
    CardView cardView;

    Uri uri;
    byte[] bytes;
    ProgressDialog progressDialog;
    String currentPhotoPath;

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
        linearLayout = findViewById(R.id.lnnnnnn);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        cardView = findViewById(R.id.cv_image);
        progressDialog.setMessage("Please wait...");

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });


        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                input.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return false;
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
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
                finishAndRemoveTask();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    private void selectImage(){
        Dialog dialog = new Dialog(RegisterActivity.this, R.style.Dialogs);
        dialog.setContentView(R.layout.layout_option_image);
        LinearLayout takePhoto;
        LinearLayout chooseFromGallery;
        LinearLayout cancel;
        takePhoto = dialog.findViewById(R.id.takePhoto);
        chooseFromGallery = dialog.findViewById(R.id.chooseFromGallery);
        cancel = dialog.findViewById(R.id.cancel_option);
        RelativeLayout relativeLayout;
        relativeLayout = dialog.findViewById(R.id.bg_select);
        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dialog.dismiss();
                return false;
            }
        });



        dialog.show();
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getApplicationContext(), "Please grant camera permission to the app", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                dispatchTakePictureIntent();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            if (data != null) {
                profile.setScaleType(ImageView.ScaleType.CENTER_CROP);
                bytes = null;
                uri = data.getData();
                profile.setImageURI(uri);
            }
        }
        if (requestCode == 100) {
            profile.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            if (bitmap==null) {
                return;
            }
            profile.setImageBitmap(bitmap);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            bytes = byteArrayOutputStream.toByteArray();
            uri = null;
        }
    }

    void functionSignUp(){
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString();
        String usernameText = username.getText().toString().trim();
        String confirmPasswordText = confirm_password.getText().toString();

        if(emailText.isEmpty()||passwordText.isEmpty()
                ||usernameText.isEmpty()||confirmPasswordText.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Please complete all information!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        if(!emailText.matches(emailPattern)){
            email.setError("Invalid Email");
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
                                                        Toast.makeText(RegisterActivity.this, "Created account successfully", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left_1);

                                                    }else{
                                                        progressDialog.dismiss();
                                                        Toast.makeText(RegisterActivity.this, "An error has occurred", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });
                                        }
                                    });
                                }else{
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, "An error occurred while upload image", Toast.LENGTH_SHORT).show();
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
                                                        Toast.makeText(RegisterActivity.this, "Created account successfully", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left_1);

                                                    }else{
                                                        progressDialog.dismiss();
                                                        Toast.makeText(RegisterActivity.this, "An error has occurred", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });
                                        }
                                    });
                                }else{
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, "An error occurred while upload image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        String linkImgProfile = "https://firebasestorage.googleapis.com/v0/b/tinstagram-82874.appspot.com/o/profile_icon.png?alt=media&token=2effc8ce-8378-4e16-9eef-a807951a8fcd";
                        Users users = new Users(auth.getUid(), usernameText,emailText, linkImgProfile, "");
                        databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, "Created account successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left_1);

                                }else{
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, "An error has occurred", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }

                }else{
                    progressDialog.dismiss();

                    Toast.makeText(RegisterActivity.this, "Email was registered", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
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

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.midterm.cloneinstagram.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 100);
            }
        }
    }
}