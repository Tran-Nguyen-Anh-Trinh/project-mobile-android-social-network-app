package com.midterm.cloneinstagram.Controller.Activity;

import static com.midterm.cloneinstagram.Adapter.ActivityAdapter.context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.midterm.cloneinstagram.Model.Post;
import com.midterm.cloneinstagram.Model.Storys;
import com.midterm.cloneinstagram.Model.Users;
import com.midterm.cloneinstagram.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewStory extends AppCompatActivity {
    private TextView btnClose, btnPost, title;
    private ImageView imageView, imageChoose;
    private TextInputEditText editText;
    private String idPost;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference reference;
    private StorageReference storageReference;
    ProgressDialog progressDialog;
    Post postPrimary;

    Uri uri;
    byte[] bytes;
    String currentPhotoPath;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_story);
        setup();
        addEvent();
    }

    private void setup() {
        btnClose = findViewById(R.id.btn_close_post);
        btnPost = findViewById(R.id.btn_post);
        title = findViewById(R.id.title);
        imageView = findViewById(R.id.image_post);
        imageChoose = findViewById(R.id.chose_image);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        reference = firebaseDatabase.getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Posting...");

    }

    private void addEvent() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAndRemoveTask();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right_1);
            }
        });
        imageChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(NewStory.this, R.style.Dialogs);
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
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idPost == null) {
                    if (bytes == null && uri == null) {
                        Toast.makeText(NewStory.this, "Please choose an image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                progressDialog.show();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-ssss");
                Date date = new Date();
                String datetime = dateFormat.format(date);
                if (uri != null) {
                    storageReference = firebaseStorage.getReference().child("Story").child(FirebaseAuth.getInstance().getUid()).child(datetime);
                    storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
                                        Storys storys = new Storys();
                                        storys.setPostid(datetime);
                                        storys.setPublisher(datetime);
                                        storys.setPostimage(uri.toString());
                                        storys.setDate(timeStamp);
                                        storys.setUserID(FirebaseAuth.getInstance().getUid());
                                        FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Users users = snapshot.getValue(Users.class);
                                                storys.setUsers(users);
                                                reference.child("Story").child(users.getUid()).setValue(storys).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(NewStory.this, "Posted", Toast.LENGTH_SHORT).show();
                                                            progressDialog.dismiss();
                                                            finishAndRemoveTask();
                                                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right_1);
                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(NewStory.this, "Error", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(NewStory.this, "Lỗi", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else if (bytes != null) {
                    storageReference = firebaseStorage.getReference().child("Story").child(FirebaseAuth.getInstance().getUid()).child(datetime);
                    storageReference.putBytes(bytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
                                        Storys storys = new Storys();
                                        storys.setPostid(datetime);
                                        storys.setPublisher(datetime);
                                        storys.setDate(timeStamp);
                                        storys.setPostimage(uri.toString());

                                        FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Users users = snapshot.getValue(Users.class);
                                                storys.setUsers(users);
                                                reference.child("Story").child(users.getUid()).setValue(storys).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(NewStory.this, "Posted", Toast.LENGTH_SHORT).show();
                                                            progressDialog.dismiss();
                                                            finishAndRemoveTask();
                                                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right_1);
                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(NewStory.this, "Error", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(NewStory.this, "Lỗi", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            if (data != null) {
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                bytes = null;
                uri = data.getData();
                imageView.setImageURI(uri);
            }
        }
        if (requestCode == 100) {
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            if (bitmap==null) {
                return;
            }
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageBitmap(bitmap);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            bytes = byteArrayOutputStream.toByteArray();
            uri = null;
        }
    }

    @Override
    public void onBackPressed() {
        finishAndRemoveTask();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right_1);
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