package com.application.demo.singleimage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.application.demo.R;

import java.io.File;

public class SelectSingleImageActivity extends AppCompatActivity {

    private ImageView ivShow;

    private final ActivityResultContracts.TakePicture takePicture = new ActivityResultContracts.TakePicture();

    private final ActivityResultContracts.StartActivityForResult openGalleryContact = new ActivityResultContracts.StartActivityForResult();
    private Uri uri;

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_single_image);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(
                    SelectSingleImageActivity.this,
                    getPackageName() + ".fileprovider",
                    new File(getExternalCacheDir(), "avatar.png")
            );
        } else {
            uri = Uri.fromFile(new File(getExternalCacheDir(), "avatar.png"));
        }

        ivShow = findViewById(R.id.iv_show);

        ActivityResultLauncher<Uri> cameraContact = registerForActivityResult(takePicture, result -> {
            if (result) {
                ivShow.setImageURI(null);
                ivShow.setImageURI(uri);
            }
        });

        ActivityResultLauncher<Intent> openGalleryResult = registerForActivityResult(openGalleryContact, result -> {
            Uri uri = null;
            if (result.getData() != null) {
                uri = result.getData().getData();
            }
            if (uri != null) {
                ivShow.setImageURI(null);
                ivShow.setImageURI(uri);
            }
        });

        findViewById(R.id.btPhotograph).setOnClickListener(v -> cameraContact.launch(uri));

        findViewById(R.id.btSystemGallery).setOnClickListener(v -> openGalleryResult.launch(
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        ));

        findViewById(R.id.btCustomGallery).setOnClickListener(v ->
                openGalleryResult.launch(
                        new Intent(SelectSingleImageActivity.this, CustomSingleImageSelectActivity.class)
                )
        );
    }
}
