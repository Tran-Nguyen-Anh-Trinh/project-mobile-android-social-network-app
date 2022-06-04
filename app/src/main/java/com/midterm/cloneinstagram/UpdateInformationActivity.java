package com.midterm.cloneinstagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.midterm.cloneinstagram.Model.Users;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateInformationActivity extends AppCompatActivity {
    CircleImageView circleImageView;
    EditText editTextEmail;
    EditText editTextName;
    TextView btnApply;
    TextView btnCC;
    TextView forgot_pass;
    TextView btnSingOut;
    Users users;
    ProgressDialog progressDialog;

    Uri imgUri;
    byte[] bytes;
    String linkDownload;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    String token = "";
    String ID = FirebaseAuth.getInstance().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_information);
        circleImageView = (CircleImageView) findViewById(R.id.ImgAvatar);
//        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/tchat-5e5a4.appspot.com/o/ImageChat%2FHRAqMYguN7W0S6pe9lgyXSgkajE3Mon-21-Feb-2022-17%3A06%3A50.66900?alt=media&token=3f854645-5216-4dac-a53b-034620b936b2").into(circleImageView);
        editTextEmail = findViewById(R.id.editEmail);
        editTextName = findViewById(R.id.editName);
        btnApply = findViewById(R.id.btnApply);
        btnCC = findViewById(R.id.btnCC);
        forgot_pass = findViewById(R.id.forgot_pass);

        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateInformationActivity.this, ForgotPassWordActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btnCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAndRemoveTask();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right_1);            }
        });
        btnSingOut = findViewById(R.id.btnSignOut);
        btnSingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(UpdateInformationActivity.this, R.style.Dialogs);
                dialog.setContentView(R.layout.dialog_layout);
                TextView btnYes;
                TextView btnNo;
                btnYes = dialog.findViewById(R.id.button_Yes);
                btnNo = dialog.findViewById(R.id.button_No);

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(UpdateInformationActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        editTextEmail.setEnabled(false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        DatabaseReference databaseReference = database.getReference().child("User").child(auth.getUid());
        DatabaseReference databaseReference1 = database.getReference().child("User").child(auth.getUid());


        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Updating...");
        progressDialog.setCancelable(false);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextName.getText().toString().isEmpty()) {
                    Toast.makeText(UpdateInformationActivity.this, "Please enter name", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog.show();
                StorageReference storageReference = storage.getReference().child("Upload").child(auth.getUid());
                if (imgUri != null) {
                    storageReference.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        linkDownload = uri.toString();
                                        databaseReference.child("imageUri").setValue(linkDownload).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    databaseReference1.child("name").setValue(editTextName.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(getBaseContext(), "Saved successfully", Toast.LENGTH_SHORT).show();
                                                                finishAndRemoveTask();
                                                                overridePendingTransition(R.anim.slide_out_down, R.anim.slide_up_dialog);
                                                            } else {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(UpdateInformationActivity.this, "Wrong something", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(UpdateInformationActivity.this, "Wrong something", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            }
                                        });
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(UpdateInformationActivity.this, "Wrong something", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else if (bytes!=null){
                    storageReference.putBytes(bytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        linkDownload = uri.toString();
                                        databaseReference.child("imageUri").setValue(linkDownload).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    databaseReference1.child("name").setValue(editTextName.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(getBaseContext(), "Saved successfully", Toast.LENGTH_SHORT).show();
                                                                finishAndRemoveTask();
                                                                overridePendingTransition(R.anim.slide_out_down, R.anim.slide_up_dialog);
                                                            } else {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(UpdateInformationActivity.this, "Wrong something", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(UpdateInformationActivity.this, "Wrong something", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            }
                                        });
                                    }
                                });
                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(UpdateInformationActivity.this, "Wrong something", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    databaseReference1.child("name").setValue(editTextName.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(), "Saved successfully", Toast.LENGTH_SHORT).show();
                                finishAndRemoveTask();
                                overridePendingTransition(R.anim.slide_out_down, R.anim.slide_up_dialog);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(UpdateInformationActivity.this, "Wrong something", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });


        Picasso.get().load(Users.getInstance().getImageUri()).into(circleImageView);
        editTextEmail.setText(Users.getInstance().getEmail());
        editTextName.setText(Users.getInstance().getName());
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(UpdateInformationActivity.this, R.style.Dialogs);
                dialog.setContentView(R.layout.layout_option_image);
                LinearLayout takePhoto;
                LinearLayout chooseFromGallery;
                LinearLayout cancel;
                takePhoto = dialog.findViewById(R.id.takePhoto);
                chooseFromGallery = dialog.findViewById(R.id.chooseFromGallery);
                cancel = dialog.findViewById(R.id.cancel_option);
                takePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_DENIED) {
                            Toast.makeText(UpdateInformationActivity.this, "Please grant camera permission to the app", Toast.LENGTH_SHORT).show();
                        }else {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 100);
                        }
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
                dialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        UpdateInformationActivity.super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            Log.i("rrrrrr", requestCode+"");
            if (data != null) {
                bytes = null;
                imgUri = data.getData();
                Log.i("eeeeeeee", imgUri+"");

                circleImageView.setImageURI(imgUri);
            }
        }
        if (requestCode == 100) {
            if (data != null) {
                if (data.getExtras() == null) {
                    return;
                }
                imgUri = null;
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                circleImageView.setImageBitmap(bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                bytes = baos.toByteArray();
            }
        }
    }

    void updateStatus(String status){
        DatabaseReference databaseReference = database.getReference().child("Users").child(auth.getUid()).child("status");
        databaseReference.setValue(status);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right_1);
    }
}