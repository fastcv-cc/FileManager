package com.application.demo;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;

import com.application.demo.filemanager.FileManagerActivity;
import com.application.demo.singleimage.SelectSingleImageActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    ActivityResultCallback<Map<String, Boolean>> resultCallback1 = result -> {
        if (!result.values().contains(false)) {
            FileManagerActivity.intoActivity(MainActivity.this, getFilesDir().getParent());
        }
    };

    ActivityResultCallback<Map<String, Boolean>> resultCallback2 = result -> {
        if (!result.values().contains(false)) {
            FileManagerActivity.intoActivity(MainActivity.this, getExternalFilesDir("").getParent());
        }
    };

    ActivityResultCallback<Map<String, Boolean>> resultCallback3 = result -> {
        if (!result.values().contains(false)) {
            FileManagerActivity.intoActivity(MainActivity.this, Environment.getExternalStorageDirectory().getAbsolutePath());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ActivityResultLauncher<String[]> intentLauncher1 = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), resultCallback1);
        ActivityResultLauncher<String[]> intentLauncher2 = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), resultCallback2);
        ActivityResultLauncher<String[]> intentLauncher3 = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), resultCallback3);



        findViewById(R.id.bt_internal_storage).setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    AlertDialog builder = new AlertDialog.Builder(this).setMessage("本程序需要您同意允许访问所有文件权限").setPositiveButton("确定", (dialog, which) -> startActivity(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))).create();
                    builder.show();
                } else {
                    FileManagerActivity.intoActivity(MainActivity.this, getFilesDir().getParent());
                }
            } else {
                AlertDialog builder = new AlertDialog.Builder(this).setMessage("本程序需要您同意允许读写文件权限").setPositiveButton("确定", (dialog, which) -> intentLauncher1.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})).create();
                builder.show();
            }
        });

        findViewById(R.id.bt_external_private_storage).setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    AlertDialog builder = new AlertDialog.Builder(this).setMessage("本程序需要您同意允许访问所有文件权限").setPositiveButton("确定", (dialog, which) -> startActivity(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))).create();
                    builder.show();
                } else {
                    FileManagerActivity.intoActivity(MainActivity.this, getExternalFilesDir("").getParent());
                }
            } else {
                AlertDialog builder = new AlertDialog.Builder(this).setMessage("本程序需要您同意允许读写文件权限").setPositiveButton("确定", (dialog, which) -> intentLauncher2.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})).create();
                builder.show();
            }
        });

        findViewById(R.id.bt_external_public_storage).setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    AlertDialog builder = new AlertDialog.Builder(this).setMessage("本程序需要您同意允许访问所有文件权限").setPositiveButton("确定", (dialog, which) -> startActivity(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))).create();
                    builder.show();
                } else {
                    FileManagerActivity.intoActivity(MainActivity.this, Environment.getExternalStorageDirectory().getAbsolutePath());
                }
            } else {
                AlertDialog builder = new AlertDialog.Builder(this).setMessage("本程序需要您同意允许读写文件权限").setPositiveButton("确定", (dialog, which) -> intentLauncher3.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})).create();
                builder.show();
            }
        });

        findViewById(R.id.bt_select_single_image).setOnClickListener(v -> {
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            intent.setType("video/*");
//            startActivity(Intent.createChooser(intent, "Select a video"));
            startActivity(new Intent(MainActivity.this, SelectSingleImageActivity.class));
        });

        findViewById(R.id.bt_image).setOnClickListener(v -> {
            MediaScannerConnection.scanFile(this, new String[]{"*/*.mp4", "*/*.avi"}, null, new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {

                }

                @Override
                public void onScanCompleted(String path, Uri uri) {
                    if (path != null) {
                        Log.d("xcl_debug", "onScanCompleted: path = " + path);
                    }
                    if (uri != null) {
                        Log.d("xcl_debug", "onScanCompleted: uri = " + uri.toString());
                    }

                }
            });
        });

        findViewById(R.id.bt_video).setOnClickListener(v -> {
            List<String> videoPaths = new ArrayList<>();
            String[] projection = {MediaStore.Video.Media.DATA};
            Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);

            if (cursor != null) {
                int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                while (cursor.moveToNext()) {
                    String filePath = cursor.getString(dataIndex);
                    videoPaths.add(filePath);
                }
                cursor.close();
            }
            for (String videoPath : videoPaths) {
                Log.d("xcl_debug", "onCreate: videoPath = " + videoPath);
            }

        });


        findViewById(R.id.bt_media_type).setOnClickListener(v -> {
            getAllVideoInfos();
            getLoadMedia();
        });
    }

    public void getLoadMedia() {
        Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)); // id
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM)); // 专辑
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST)); // 艺术家
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)); // 显示名称
                String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)); // 路径
                long duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)); // 时长
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)); // 大小
                String resolution = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION));
                Log.d("xcl_debug", "getLoadMedia: ------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    /**
     * 获取手机中所有视频的信息
     */
    @SuppressLint("Range")
    private void getAllVideoInfos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                HashMap<String,List<MediaBean>> allPhotosTemp = new HashMap<>();//所有照片
                Uri mImageUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] proj = {MediaStore.Video.Thumbnails._ID, MediaStore.Video.Thumbnails.DATA, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DATE_MODIFIED};
                Cursor mCursor = getContentResolver().query(mImageUri, proj, MediaStore.Video.Media.MIME_TYPE + "=?", new String[]{"video/mp4"}, MediaStore.Video.Media.DATE_MODIFIED + " desc");
                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        // 获取视频的路径
                        int videoId = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media._ID));
                        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        int duration = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        long size = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.SIZE)) / 1024; //单位kb
                        if (size < 0) {
                            //某些设备获取size<0，直接计算
                            Log.e("dml", "this video size < 0 " + path);
                            size = new File(path).length() / 1024;
                        }
                        String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        long modifyTime = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));//暂未用到

                        //提前生成缩略图，再获取：http://stackoverflow.com/questions/27903264/how-to-get-the-video-thumbnail-path-and-not-the-bitmap
                        MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(), videoId, MediaStore.Video.Thumbnails.MICRO_KIND, null);
                        String[] projection = {MediaStore.Video.Thumbnails._ID, MediaStore.Video.Thumbnails.DATA};
                        Cursor cursor = getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, projection, MediaStore.Video.Thumbnails.VIDEO_ID + "=?", new String[]{videoId + ""}, null);
                        String thumbPath = "";
                        while (cursor.moveToNext()) {
                            thumbPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                        }
                        cursor.close();
                        // 获取该视频的父路径名
                        String dirPath = new File(path).getParentFile().getAbsolutePath();

                        Log.d("xcl_debug", "run: path = " + path + "  thumbPath = " + thumbPath + "  duration = " + duration + " size = " + size + "  displayName = " + displayName);
                        //存储对应关系
//                        if (allPhotosTemp.containsKey(dirPath)) {
//                            List<MediaBean> data = allPhotosTemp.get(dirPath);
//                            data.add(new MediaBean(MediaBean.Type.Video,path,thumbPath,duration,size,displayName));
//                            continue;
//                        } else {
//                            List<MediaBean> data = new ArrayList<>();
//                            data.add(new MediaBean(MediaBean.Type.Video,path,thumbPath,duration,size,displayName));
//                            allPhotosTemp.put(dirPath,data);
//                        }
                    }
                    mCursor.close();
                }
                //更新界面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //...
                    }
                });
            }
        }).start();
    }

}