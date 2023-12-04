package com.application.demo;


import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import com.application.demo.filemanager.FileManagerActivity;
import com.application.demo.singleimage.SelectSingleImageActivity;
import com.application.demo.singlevideo.SelectSingleVideoActivity;

import java.util.Map;


public class MainActivity extends AppCompatActivity {

    ActivityResultCallback<Map<String, Boolean>> resultCallback1 = result -> {
        if (!result.containsValue(false)) {
            FileManagerActivity.intoActivity(MainActivity.this, getFilesDir().getParent());
        }
    };

    ActivityResultCallback<Map<String, Boolean>> resultCallback2 = result -> {
        if (!result.containsValue(false)) {
            FileManagerActivity.intoActivity(MainActivity.this, getExternalFilesDir("").getParent());
        }
    };

    ActivityResultCallback<Map<String, Boolean>> resultCallback3 = result -> {
        if (!result.containsValue(false)) {
            FileManagerActivity.intoActivity(MainActivity.this, Environment.getExternalStorageDirectory().getAbsolutePath());
        }
    };

    ActivityResultCallback<Uri> resultCallback4 = result -> {
        Log.d("MainActivity", ":result = " + result);
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityResultLauncher<String[]> intentLauncher1 = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), resultCallback1);
        ActivityResultLauncher<String[]> intentLauncher2 = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), resultCallback2);
        ActivityResultLauncher<String[]> intentLauncher3 = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), resultCallback3);
        ActivityResultLauncher<String[]> intentLauncher4 = registerForActivityResult(new ActivityResultContracts.OpenDocument(), resultCallback4);

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

        findViewById(R.id.bt_saf).setOnClickListener(v -> {
            intentLauncher4.launch(new String[]{"*"});
        });

        findViewById(R.id.bt_select_single_image).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SelectSingleImageActivity.class)));

        findViewById(R.id.bt_select_single_video).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SelectSingleVideoActivity.class)));
    }

}