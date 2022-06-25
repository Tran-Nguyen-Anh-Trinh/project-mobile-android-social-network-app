package com.midterm.cloneinstagram.Controller.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.midterm.cloneinstagram.Model.Users;
import com.midterm.cloneinstagram.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    RelativeLayout relativeLayout;
    CardView cardView;
    Uri imgUri;
    byte[] bytes;
    String linkDownload;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    String token = "";
    String ID = FirebaseAuth.getInstance().getUid();
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_information);
        circleImageView = (CircleImageView) findViewById(R.id.ImgAvatar);
        editTextEmail = findViewById(R.id.editEmail);
        editTextName = findViewById(R.id.editName);
        btnApply = findViewById(R.id.btnApply);
        btnCC = findViewById(R.id.btnCC);
        cardView = findViewById(R.id.cv_image);
        forgot_pass = findViewById(R.id.forgot_pass);
        relativeLayout = findViewById(R.id.hide_relative);


        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                input.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return false;
            }
        });

        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateInformationActivity.this, ForgotPassWordActivity.class);
                intent.putExtra("email", Users.getInstance().getEmail());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left_1);
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
                LinearLayout linearLayout;
                btnYes = dialog.findViewById(R.id.button_Yes);
                btnNo = dialog.findViewById(R.id.button_No);
                linearLayout = dialog.findViewById(R.id.bg_signout);

                linearLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        dialog.dismiss();
                        return false;
                    }
                });

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        FirebaseDatabase.getInstance().getReference("User/" + FirebaseAuth.getInstance().getUid() + "/token").removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        updateStatus("offline");
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                });
                        Intent intent = new Intent(UpdateInformationActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left_1);
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
                                                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right_1);
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
                                                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right_1);
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
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right_1);
                            }else {
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
                selectImage();
            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }

    private void selectImage(){
        Dialog dialog = new Dialog(UpdateInformationActivity.this, R.style.Dialogs);
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

        UpdateInformationActivity.super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            if (data != null) {
                circleImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                bytes = null;
                imgUri = data.getData();
                circleImageView.setImageURI(imgUri);
            }
        }
        if (requestCode == 100) {
            circleImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            if (bitmap==null) {
                return;
            }
            circleImageView.setImageBitmap(bitmap);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            bytes = byteArrayOutputStream.toByteArray();
            imgUri = null;
        }
    }

    void updateStatus(String status){
        DatabaseReference databaseReference = database.getReference().child("User").child(auth.getUid()).child("status");
        databaseReference.setValue(status);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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