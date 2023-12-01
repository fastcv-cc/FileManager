package com.application.demo.filemanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.application.demo.R;

import java.io.File;
import java.util.Objects;

public class FileManagerActivity extends AppCompatActivity {

    private static final String FILE_PATH = "filePath";
    private TextView tvPath;

    private File root;
    private File currentParentFile;
    private FileAdapter adapter;

    public static void intoActivity(Context context, String filePath) {
        Intent intent = new Intent(context, FileManagerActivity.class);
        intent.putExtra(FILE_PATH, filePath);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);

        RecyclerView rvFile = findViewById(R.id.rv_file);
        tvPath = findViewById(R.id.tv_path);

        String filePath = getIntent().getStringExtra(FILE_PATH);

        if (TextUtils.isEmpty(filePath)) {
            showExceptionDialogAndExits("传入路径为空，将退出此界面！！");
            return;
        }

        //默认文件夹一定存在
        root = new File(filePath);
        if (!root.exists() || !root.isDirectory()) {
            showExceptionDialogAndExits("此路径不存在或者不为文件夹，将退出此界面！！");
            return;
        }

        File[] files = root.listFiles();
        if (files == null) {
            showExceptionDialogAndExits("路径异常，将退出此界面！！");
            return;
        }

        currentParentFile = root;
        tvPath.setText(currentParentFile.getAbsolutePath());
        adapter = new FileAdapter();
        rvFile.setAdapter(adapter);
        adapter.setItemClickListener((file, position) -> goDir(file));

        adapter.setItemLongClickListener((file, position) -> {
            deleteFileOrFolder(file);
            return true;
        });
        adapter.setData(files);
    }

    private void goDir(File file) {
        File[] listFiles = file.listFiles();
        if (listFiles == null) {
            Toast.makeText(this, "访问的目录不存在或无法访问", Toast.LENGTH_SHORT).show();
            return;
        }

        currentParentFile = file;
        tvPath.setText(currentParentFile.getAbsolutePath());
        adapter.setData(listFiles);
    }

    private void deleteFileOrFolder(File file) {
        String msg;
        if (file.isDirectory()) {
            msg = "确认删除此文件夹？";
        } else {
            msg = "确认删除此文件？";
        }

        new AlertDialog.Builder(this)
                .setTitle("警告")
                .setMessage(msg)
                .setPositiveButton("确定", (dialog, which) -> {
                    String fileType;
                    if (file.isDirectory()) {
                        fileType = "文件夹";
                    } else {
                        fileType = "文件";
                    }

                    if (file.delete()) {
                        Toast.makeText(FileManagerActivity.this, fileType + "删除成功", Toast.LENGTH_SHORT).show();
                        goDir(currentParentFile);
                    } else {
                        Toast.makeText(FileManagerActivity.this, fileType + "删除失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .create().show();
    }

    private void showExceptionDialogAndExits(String msg) {
        new AlertDialog.Builder(this)
                .setTitle("警告")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("确定", (dialog, which) -> finish())
                .create().show();
    }

    @Override
    public void onBackPressed() {
        if (Objects.requireNonNull(currentParentFile).getAbsolutePath().equals(root.getAbsolutePath())) {
            getOnBackPressedDispatcher().onBackPressed();
        } else {
            goDir(Objects.requireNonNull(currentParentFile.getParentFile()));
        }
    }

}
