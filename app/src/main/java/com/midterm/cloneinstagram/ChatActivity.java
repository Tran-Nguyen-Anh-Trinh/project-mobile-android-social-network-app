package com.midterm.cloneinstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.midterm.cloneinstagram.Adapter.MessagesAdapter;
import com.midterm.cloneinstagram.Model.Messages;
import com.midterm.cloneinstagram.Model.Users;
import com.midterm.cloneinstagram.PushNotify.FCMSend;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String Uid;
    String Name;
    String ReceiverImg;
    CircleImageView profileImg;
    CircleImageView status;
    TextView receiverName;
    CardView sentBtn;
    CardView Img_select;
    CardView camera;
    EditText editMess;
    RecyclerView messAdapter;
    ImageView btnCall;
    ImageView btnVideoCall;

    String senderRoom;
    String receiverRoom;

    ArrayList<Messages> messagesArrayList;
    MessagesAdapter adapter;

    FirebaseDatabase database;
    FirebaseAuth mAuth;
    FirebaseStorage storage;

    public static String sImage;
    public static String rImage;
    String uriSelectImg = "";
    String uriSelectVid = "";
    Uri uriImg;


    Users usersReceive = null;

    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

    String idSend;
    String nameSend;
    String avaSend;
    DatabaseReference userRef;
    DatabaseReference isReadRef;

    ValueEventListener valueEventListener;
    ValueEventListener valueEventListenerMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();


        Uid = getIntent().getStringExtra("Uid");
        Name = getIntent().getStringExtra("Name");
        ReceiverImg = getIntent().getStringExtra("ReceiverImg");

        idSend = getIntent().getStringExtra("idSend");
        nameSend = getIntent().getStringExtra("nameSend");
        avaSend = getIntent().getStringExtra("avaSend");


        btnCall = findViewById(R.id.btnCall);
        btnVideoCall = findViewById(R.id.btnVideoCall);

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "Chưa đủ trình để làm", Toast.LENGTH_SHORT).show();
            }
        });

        btnVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "Chưa đủ trình để làm", Toast.LENGTH_SHORT).show();
            }
        });





        camera = findViewById(R.id.Camera);
        messagesArrayList = new ArrayList<>();
        receiverName = findViewById(R.id.receiver_Name);
        profileImg = findViewById(R.id.profileImg_1);
        status = findViewById(R.id.status);
        sentBtn = findViewById(R.id.sentBtn);
        editMess = findViewById(R.id.editMess);
        Img_select = findViewById(R.id.Img_select);
        messAdapter = findViewById(R.id.messageAdapter);


        Picasso.get().load(ReceiverImg).into(profileImg);
        receiverName.setText(Name);

        senderRoom = mAuth.getUid() + Uid;
        receiverRoom = Uid + mAuth.getUid();

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                database.getReference().child("Chats")
                        .child(senderRoom)
                        .child("IsRead").setValue("false");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };


        isReadRef = FirebaseDatabase.getInstance().getReference()
                .child("Chats").child(senderRoom)
                .child("IsRead");
        isReadRef.addValueEventListener(valueEventListener);

        userRef = FirebaseDatabase.getInstance().getReference().child("User").child(Uid);
        DatabaseReference databaseRf = database.getReference().child("User").child(Uid);
        databaseRf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                if("online".equals(users.getStatus())){
                    status.setImageResource(R.drawable.color_online);
                }else{
                    status.setImageResource(R.drawable.color_offline);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        valueEventListenerMessages = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DatabaseReference chatReference = database.getReference().child("Chats").child(senderRoom).child("Messages");
                chatReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messagesArrayList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Messages messages = dataSnapshot.getValue(Messages.class);
                            messagesArrayList.add(messages);
                        }
                        adapter.notifyDataSetChanged();
                        linearLayoutManager.smoothScrollToPosition(messAdapter, null, adapter.getItemCount());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        isReadRef.addValueEventListener(valueEventListenerMessages);


        messAdapter.setLayoutManager(linearLayoutManager);
        adapter = new MessagesAdapter(ChatActivity.this, messagesArrayList, Uid, ReceiverImg);
        messAdapter.setAdapter(adapter);
        messAdapter.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom)
                    linearLayoutManager.smoothScrollToPosition(messAdapter, null, adapter.getItemCount());
            }
        });

        Img_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 10);


            }
        });
        sentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messages = editMess.getText().toString().trim();
                if (messages.isEmpty()) {
                    Toast.makeText(ChatActivity.this, "Please enter a messages!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Date now = new Date();
                String format = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(now);
                editMess.setText("");
                saveMessagesChat(messages, "", "");
                linearLayoutManager.smoothScrollToPosition(messAdapter, null, adapter.getItemCount());

                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        usersReceive = snapshot.getValue(Users.class);
                        if(!usersReceive.getUid().equals(FirebaseAuth.getInstance().getUid())){
                            FCMSend.pushNotification(ChatActivity.this, usersReceive.getToken(), "Messages", nameSend+": "+messages + " on " + format, idSend, nameSend, avaSend, Uid, Name, ReceiverImg);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        DatabaseReference databaseReference = database.getReference().child("User").child(mAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sImage = snapshot.child("imageUri").getValue().toString();
                rImage = ReceiverImg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(ChatActivity.this, "Please grant camera permission to the app", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 100);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        ChatActivity.super.onActivityResult(requestCode, resultCode, data);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading...");
        if (requestCode == 10) {
            if (data != null) {
                progressDialog.show();
                uriImg = data.getData();
                System.out.println("uuuuuuuuuuu: " + uriImg);
                storage = FirebaseStorage.getInstance();
                Date now = new Date();
                String format = new SimpleDateFormat("EEE-d-MMM-yyyy-HH:mm:ss.SSSSS", Locale.ENGLISH).format(now);
                StorageReference storageReference;
                if (isImageFile(uriImg)) {
                    storageReference = storage.getReference().child("ImageChat").child(mAuth.getUid() + format);
                    storageReference.putFile(uriImg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        if (uri != null) {
                                            uriSelectImg = uri.toString();
                                            saveMessagesChat("", uriSelectImg, "");
                                            linearLayoutManager.smoothScrollToPosition(messAdapter, null, adapter.getItemCount());
                                            String format = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(now);

                                            userRef.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    usersReceive = snapshot.getValue(Users.class);
                                                    if(!usersReceive.getUid().equals(FirebaseAuth.getInstance().getUid())){
                                                        FCMSend.pushNotification(ChatActivity.this, usersReceive.getToken(), "Messages", nameSend+": Send image messages on "+ format, idSend, nameSend, avaSend, Uid, Name, ReceiverImg);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                            progressDialog.dismiss();
                                        }
                                    }
                                });

                            }
                        }
                    });
                } else {
                    storageReference = storage.getReference().child("VideoChat").child(mAuth.getUid() + format);
                    storageReference.putFile(uriImg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        if (uri != null) {
                                            uriSelectVid = uri.toString();
                                            saveMessagesChat("", "", uriSelectVid);
                                            linearLayoutManager.smoothScrollToPosition(messAdapter, null, adapter.getItemCount());
                                            String format = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(now);
                                            userRef.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    usersReceive = snapshot.getValue(Users.class);
                                                    if(!usersReceive.getUid().equals(FirebaseAuth.getInstance().getUid())){
                                                        FCMSend.pushNotification(ChatActivity.this, usersReceive.getToken(), "Messages", nameSend+": Send video messages on "+ format, idSend, nameSend, avaSend, Uid, Name, ReceiverImg);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                            progressDialog.dismiss();
                                        }
                                    }
                                });

                            }
                        }
                    });
                }
            }
        }
        if (requestCode == 100) {
            if (data != null) {
                if(data.getExtras()==  null){
                    return;
                }
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] dt = baos.toByteArray();
                storage = FirebaseStorage.getInstance();
                Date now = new Date();
                String format = new SimpleDateFormat("EEE-d-MMM-yyyy-HH:mm:ss.SSSSS", Locale.ENGLISH).format(now);
                uriImg = data.getData();
                progressDialog.show();
                StorageReference storageReference;
                storageReference = storage.getReference().child("ImageChat").child(mAuth.getUid() + format);
                storageReference.putBytes(dt).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if (uri != null) {
                                        uriSelectImg = uri.toString();
                                        saveMessagesChat("", uriSelectImg, "");
                                        linearLayoutManager.smoothScrollToPosition(messAdapter, null, adapter.getItemCount());
                                        String format = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(now);

                                        userRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                usersReceive = snapshot.getValue(Users.class);
                                                if(!usersReceive.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                                                    FCMSend.pushNotification(ChatActivity.this, usersReceive.getToken(), "Messages", nameSend+": Send image messages on "+ format, idSend, nameSend, avaSend, Uid, Name, ReceiverImg);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        progressDialog.dismiss();
                                    }
                                }
                            });

                        }
                    }
                });
            }
        }
    }

    private void saveMessagesChat(String messages, String img, String vid) {
        Date now = new Date();
        String format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.ENGLISH).format(now);
        Messages messages1;
        messages1 = new Messages(messages, mAuth.getUid(), img, vid, format);
        database = FirebaseDatabase.getInstance();
        if (mAuth.getUid().equals(Uid)) {
            database.getReference().child("Chats")
                    .child(senderRoom)
                    .child("Messages")
                    .push()
                    .setValue(messages1);
        } else {
            database.getReference().child("Chats")
                    .child(senderRoom)
                    .child("Messages")
                    .push()
                    .setValue(messages1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            database.getReference().child("Chats")
                                    .child(receiverRoom)
                                    .child("Messages")
                                    .push().setValue(messages1);

                        }
                    });
            database.getReference().child("Chats")
                    .child(receiverRoom)
                    .child("IsRead").setValue("true");
        }
    }

    @Override
    public void onBackPressed() {
        isReadRef.removeEventListener(valueEventListener);
        isReadRef.removeEventListener(valueEventListenerMessages);
        finishAndRemoveTask();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right_1);
    }

    public boolean isImageFile(Uri path) {
        ContentResolver cR = ChatActivity.this.getContentResolver();
        String type = cR.getType(path);
        System.out.println("00000000000000: "+type.toString());
        if(type.contains("image")){
            return true;
        }
        return false;
    }

}