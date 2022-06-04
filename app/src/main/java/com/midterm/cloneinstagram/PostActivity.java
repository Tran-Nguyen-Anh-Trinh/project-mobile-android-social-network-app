package com.midterm.cloneinstagram;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import com.midterm.cloneinstagram.Model.Users;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PostActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idPost = getIntent().getStringExtra("idPost");
        setContentView(R.layout.activity_post);
        setup();
        addEvent();
    }

    private void setup() {
        btnClose = findViewById(R.id.btn_close_post);
        btnPost = findViewById(R.id.btn_post);
        title = findViewById(R.id.title);
        imageView = findViewById(R.id.image_post);
        editText = findViewById(R.id.text_caption);
        imageChoose = findViewById(R.id.chose_image);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        reference = firebaseDatabase.getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        if (idPost == null) {
            progressDialog.setMessage("Posting...");
            title.setText("New post");
        } else {
            progressDialog.setMessage("Updating...");
            title.setText("Update post");
            getData();
        }

    }

    private void getData() {
        FirebaseDatabase.getInstance().getReference().child("Post").child(idPost).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postPrimary = snapshot.getValue(Post.class);
                Picasso.get().load(postPrimary.getPostimage()).into(imageView);
                editText.setText(postPrimary.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addEvent() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAndRemoveTask();
                overridePendingTransition(R.anim.slide_out_down, R.anim.slide_up_dialog);
            }
        });
        imageChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(PostActivity.this, R.style.Dialogs);
                dialog.setContentView(R.layout.layout_option_image);
                LinearLayout takePhoto;
                LinearLayout chooseFromGallery;
                LinearLayout cancel;
                takePhoto = dialog.findViewById(R.id.takePhoto);
                chooseFromGallery = dialog.findViewById(R.id.chooseFromGallery);
                cancel = dialog.findViewById(R.id.cancel_option);


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

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idPost == null) {
                    if (bytes == null && uri == null) {
                        Toast.makeText(PostActivity.this, "Please choose an image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                progressDialog.show();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-ssss");
                Date date = new Date();
                String datetime = dateFormat.format(date);
                if (idPost == null) {
                    if (uri != null) {
                        storageReference = firebaseStorage.getReference().child("Post").child(FirebaseAuth.getInstance().getUid()).child(datetime);
                        storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
                                            Post post = new Post();
                                            post.setPostid(datetime);
                                            post.setPublisher(datetime);
                                            post.setDescription(editText.getText().toString().trim());
                                            post.setPostimage(uri.toString());
                                            post.setDate(timeStamp);
                                            FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    Users users = snapshot.getValue(Users.class);
                                                    post.setUsers(users);
                                                    reference.child("Post").child(datetime).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(PostActivity.this, "Posted", Toast.LENGTH_SHORT).show();
                                                                progressDialog.dismiss();
                                                                finishAndRemoveTask();
                                                                overridePendingTransition(R.anim.slide_out_down, R.anim.slide_up_dialog);                                                            } else {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(PostActivity.this, "Error", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(PostActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else if (bytes != null) {
                        storageReference = firebaseStorage.getReference().child("Post").child(FirebaseAuth.getInstance().getUid()).child(datetime);
                        storageReference.putBytes(bytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
                                            Post post = new Post();
                                            post.setPostid(datetime);
                                            post.setPublisher(datetime);
                                            post.setDate(timeStamp);
                                            post.setDescription(editText.getText().toString().trim());
                                            post.setPostimage(uri.toString());

                                            FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    Users users = snapshot.getValue(Users.class);
                                                    post.setUsers(users);
                                                    reference.child("Post").child(datetime).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(PostActivity.this, "Posted", Toast.LENGTH_SHORT).show();
                                                                progressDialog.dismiss();
                                                                finishAndRemoveTask();
                                                                overridePendingTransition(R.anim.slide_out_down, R.anim.slide_up_dialog);                                                            } else {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(PostActivity.this, "Error", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(PostActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    if (uri != null) {
                        storageReference = firebaseStorage.getReference().child("Post").child(FirebaseAuth.getInstance().getUid()).child(idPost);
                        storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
                                            reference.child("Post").child(idPost).child("date").setValue(timeStamp);
                                            reference.child("Post").child(idPost).child("description").setValue(editText.getText().toString().trim());
                                            reference.child("Post").child(idPost).child("publisher").setValue(datetime);
                                            reference.child("Post").child(idPost).child("postimage").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(PostActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                        finishAndRemoveTask();
                                                        overridePendingTransition(R.anim.slide_out_down, R.anim.slide_up_dialog);
                                                    } else {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(PostActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }


                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(PostActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else if (bytes != null) {
                        storageReference = firebaseStorage.getReference().child("Post").child(FirebaseAuth.getInstance().getUid()).child(idPost);
                        storageReference.putBytes(bytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
                                            reference.child("Post").child(idPost).child("date").setValue(timeStamp);
                                            reference.child("Post").child(idPost).child("description").setValue(editText.getText().toString().trim());
                                            reference.child("Post").child(idPost).child("publisher").setValue(datetime);
                                            reference.child("Post").child(idPost).child("postimage").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(PostActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                        finishAndRemoveTask();
                                                        overridePendingTransition(R.anim.slide_out_down, R.anim.slide_up_dialog);
                                                    } else {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(PostActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(PostActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
                        reference.child("Post").child(idPost).child("date").setValue(timeStamp);
                        reference.child("Post").child(idPost).child("description").setValue(editText.getText().toString().trim());
                        reference.child("Post").child(idPost).child("publisher").setValue(datetime).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(PostActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    finishAndRemoveTask();
                                    overridePendingTransition(R.anim.slide_out_down, R.anim.slide_up_dialog);
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(PostActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            if (data != null) {
                bytes = null;
                uri = data.getData();
                imageView.setImageURI(uri);
            }
        }
        if (requestCode == 100) {
            if (data != null) {
                if (data.getExtras() == null) return;
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bitmap);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                bytes = byteArrayOutputStream.toByteArray();
                uri = null;
            }
        }
    }

    @Override
    public void onBackPressed() {
        finishAndRemoveTask();
        overridePendingTransition(R.anim.slide_out_down, R.anim.slide_up_dialog);
    }
}