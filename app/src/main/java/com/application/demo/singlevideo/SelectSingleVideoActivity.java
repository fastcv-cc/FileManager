package com.application.demo.singlevideo;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.application.demo.R;

import java.io.File;

public class SelectSingleVideoActivity extends AppCompatActivity {

    private Uri uri;
    private final ActivityResultContracts.CaptureVideo captureVideo = new ActivityResultContracts.CaptureVideo();

    private final ActivityResultContracts.StartActivityForResult openGalleryContact = new ActivityResultContracts.StartActivityForResult();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_single_video);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(
                    SelectSingleVideoActivity.this,
                    getPackageName() + ".fileprovider",
                    new File(getExternalCacheDir(), "video.mp4")
            );
        } else {
            uri = Uri.fromFile(new File(getExternalCacheDir(), "avatar.png"));
        }

        VideoView videoView = findViewById(R.id.vv_show);

        ActivityResultLauncher<Uri> videoContact = registerForActivityResult(captureVideo, result -> {
            if (result) {
                videoView.setVideoURI(uri);
                videoView.start();
            }
        });

        ActivityResultLauncher<Intent> openGalleryResult = registerForActivityResult(openGalleryContact, result -> {
            Uri uri = null;
            if (result.getData() != null) {
                uri = result.getData().getData();
            }
            if (uri != null) {
                videoView.setVideoURI(uri);
                videoView.start();
            }
        });


        findViewById(R.id.btCaptureVideo).setOnClickListener(v -> videoContact.launch(uri));

        findViewById(R.id.btSystemGallery).setOnClickListener(v -> openGalleryResult.launch(
                new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        ));

        findViewById(R.id.btCustomGallery).setOnClickListener(v -> openGalleryResult.launch(
                new Intent(SelectSingleVideoActivity.this, CustomSingleVideoSelectActivity.class)
        ));

    }
}
