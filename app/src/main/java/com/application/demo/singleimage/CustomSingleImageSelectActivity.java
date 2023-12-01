package com.application.demo.singleimage;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.application.demo.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CustomSingleImageSelectActivity extends AppCompatActivity {

    public static final String TAG = "CustomSingleImageSelectActivity";
    private GalleryAdapter galleryAdapter;
    private ContentObserver contentObserver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_single_image_select);
        galleryAdapter = new GalleryAdapter(image -> {
            Intent intent = new Intent();
            intent.setData(image.contentUri);
            setResult(200,intent);
            finish();
        });

        contentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                loadImages();
            }
        };

        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, contentObserver);


        RecyclerView rv = findViewById(R.id.gallery);
        rv.setAdapter(galleryAdapter);
        loadImages();
    }

    private void loadImages() {
        new Thread() {
            @Override
            public void run() {
                ArrayList<MediaStoreImage> imageList = queryImages();
                runOnUiThread(() -> galleryAdapter.submitList(imageList));
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(contentObserver);
    }

    @SuppressLint("Recycle")
    private ArrayList<MediaStoreImage> queryImages() {
        ArrayList<MediaStoreImage> images = new ArrayList<>();

        String[] projection = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATE_ADDED};

        String selection = MediaStore.Images.Media.DATE_ADDED + " >= ?";

        String[] selectionArgs = new String[]{dateToTimestamp(22, 10, 2008) + ""};

        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder);

        int idColumn = 0;
        if (cursor != null) {
            idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        }
        int dateModifiedColumn = 0;
        if (cursor != null) {
            dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
        }
        int displayNameColumn = 0;
        if (cursor != null) {
            displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
        }

        if (cursor == null) {
            return images;
        }
        Log.i(TAG, "Found " + cursor.getCount() + "images");
        while (cursor.moveToNext()) {
            // Here we'll use the column indexs that we found above.
            long id = cursor.getLong(idColumn);
            Date dateModified = new Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateModifiedColumn)));
            String displayName = cursor.getString(displayNameColumn);

            Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            double lat = 0.0;
            double lon = 0.0;

            MediaStoreImage image = new MediaStoreImage();
            image.id = id;
            image.displayName = displayName;
            image.dateAdded = dateModified;
            image.contentUri = contentUri;
            image.lat = lat;
            image.lon = lon;
            images.add(image);
            Log.v(TAG, "Added image:" + image);
        }

        Log.v(TAG, "Found " + images.size() + "images");
        cursor.close();
        return images;
    }

    @SuppressLint("SimpleDateFormat")
    private long dateToTimestamp(int day, int month, int year) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        long time;
        try {
            time = simpleDateFormat.parse(day + "." + month + "." + year).getTime();
        } catch (ParseException e) {
            time = 0L;
        }
        return TimeUnit.MICROSECONDS.toSeconds(time);
    }

}
