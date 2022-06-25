package com.midterm.cloneinstagram.Controller.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.midterm.cloneinstagram.CustomImageView.ZoomableImageView;
import com.midterm.cloneinstagram.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

public class seeImage extends AppCompatActivity {

    ImageView btnDownload;
    ZoomableImageView image;
    Target target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_image);
        btnDownload = findViewById(R.id.btnDownload);
        image = findViewById(R.id.seeImage);
        String url = getIntent().getStringExtra("linkURL");

        target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                image.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        if (!(url.isEmpty())) {
            Picasso.get().load(url).into(target);
        } else {
            Toast.makeText(this, "Error when load image", Toast.LENGTH_SHORT).show();
        }

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadImage(url);
            }
        });

    }

    void DownloadImage(String imageURL) {
        try {
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(imageURL);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle("Naone Team")
                    .setMimeType("image/jpeg")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator +"NaoneTeam.jpg");
            downloadManager.enqueue(request);
            Toast.makeText(this, "Download started", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Download failed", Toast.LENGTH_SHORT).show();
        }
    }
}