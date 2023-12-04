package com.application.demo.singlevideo;

import static android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.application.demo.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class CustomSingleVideoSelectActivity extends AppCompatActivity {

    public static final String TAG = "CustomSingleVideoSelectActivity";
    private GalleryAdapter galleryAdapter;
    private ContentObserver contentObserver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_single_video_select);
        galleryAdapter = new GalleryAdapter(image -> {
            Intent intent = new Intent();
            intent.setData(image.localPathUri);
            setResult(200, intent);
            finish();
        });

        contentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                loadImages();
            }
        };

        getContentResolver().registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true, contentObserver);


        RecyclerView rv = findViewById(R.id.gallery);
        rv.setAdapter(galleryAdapter);
        loadImages();
    }

    private void loadImages() {
        new Thread() {
            @Override
            public void run() {
                ArrayList<VideoInfo> videoList = queryVideos();
                runOnUiThread(() -> galleryAdapter.submitList(videoList));
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(contentObserver);
    }

    @SuppressLint({"Recycle", "Range"})
    private ArrayList<VideoInfo> queryVideos() {
        ArrayList<VideoInfo> videos = new ArrayList<>();

        String[] projection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            projection = new String[]{MediaStore.Video.Media._ID, MediaStore.Video.Media.SIZE,
                    MediaStore.Video.Media.WIDTH,
                    MediaStore.Video.Media.HEIGHT,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.MIME_TYPE,
                    MediaStore.Video.Media.DURATION,
                    MediaStore.Video.Media.DATE_ADDED,
                    MediaStore.Video.Media.DATE_MODIFIED,
                    MediaStore.Video.Media.BITRATE
            };
        } else {
            projection = new String[]{MediaStore.Video.Media._ID, MediaStore.Video.Media.SIZE,
                    MediaStore.Video.Media.WIDTH,
                    MediaStore.Video.Media.HEIGHT,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.MIME_TYPE,
                    MediaStore.Video.Media.DURATION,
                    MediaStore.Video.Media.DATE_ADDED,
                    MediaStore.Video.Media.DATE_MODIFIED
            };
        }

        String selection = MediaStore.Video.Media.DATE_ADDED + " >= ?";

        String[] selectionArgs = new String[]{dateToTimestamp(22, 10, 2008) + ""};

        String sortOrder = MediaStore.Video.Media.DATE_ADDED + " DESC";

        Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder);

        if (cursor == null) {
            return videos;
        }
        while (cursor.moveToNext()) {
            VideoInfo videoInfo = new VideoInfo();
            videoInfo.mediaId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
            videoInfo.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
            videoInfo.width = cursor.getFloat(cursor.getColumnIndex(MediaStore.Video.Media.WIDTH));
            videoInfo.height = cursor.getFloat(cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT));
            videoInfo.localPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            videoInfo.localPathUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, Long.parseLong(videoInfo.mediaId));
            videoInfo.fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
            videoInfo.mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));
            long startTime = System.currentTimeMillis();
            videoInfo.firstFrame = getVideoThumbnail(videoInfo.localPathUri);
            Log.d(TAG, "queryVideos:duration =  " + (System.currentTimeMillis() - startTime));
            videoInfo.duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                videoInfo.biteRate = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.BITRATE));
            } else {
                videoInfo.biteRate = (long) ((8 * videoInfo.size * 1024) / (videoInfo.duration / 1000f));
            }
            videoInfo.addTime = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
            videoInfo.lastModified = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));

            videos.add(videoInfo);
            Log.v(TAG, "Added video:" + videoInfo);
        }

        Log.v(TAG, "Found " + videos.size() + "videos");
        cursor.close();
        return videos;
    }


    private final long VIDEO_FIRST_FRAME_TIME_US = 1000L;
    /**
     * 视频缩略图默认压缩尺寸
     */
    private final float THUMBNAIL_DEFAULT_COMPRESS_VALUE = 512f;

    /**
     * 获取视频缩略图：通过绝对路径抓取第一帧
     */
    private Bitmap getVideoThumbnail(Uri uri) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(this, uri);
            // OPTION_CLOSEST_SYNC：在给定的时间，检索最近一个同步与数据源相关联的的帧（关键帧）
            // OPTION_CLOSEST：表示获取离该时间戳最近帧（I帧或P帧）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                bitmap = retriever.getScaledFrameAtTime(
                        VIDEO_FIRST_FRAME_TIME_US, OPTION_CLOSEST_SYNC,
                        (int) THUMBNAIL_DEFAULT_COMPRESS_VALUE,
                        (int) THUMBNAIL_DEFAULT_COMPRESS_VALUE
                );
            } else {
                bitmap = compressVideoThumbnail(retriever.getFrameAtTime(VIDEO_FIRST_FRAME_TIME_US));
            }
        } catch (Exception e) {
        } finally {
            try {
                retriever.release();
            } catch (Exception e) {
            }
            return bitmap;
        }
    }

    /**
     * 压缩视频缩略图
     */
    Bitmap compressVideoThumbnail(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int max = Math.max(width, height);
        if (max > THUMBNAIL_DEFAULT_COMPRESS_VALUE) {
            float scale = THUMBNAIL_DEFAULT_COMPRESS_VALUE / max;
            int w = (int) (scale * width);
            int h = (int) (scale * height);
            return compressVideoThumbnail(bitmap, w, h);
        }
        return bitmap;
    }

    /**
     * 压缩视频缩略图：宽高压缩
     * 注：如果用户期望的长度和宽度和原图长度宽度相差太多的话，图片会很不清晰。
     *
     * @param bitmap 视频缩略图
     */
    private Bitmap compressVideoThumbnail(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
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
