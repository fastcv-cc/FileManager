package com.application.demo.singlevideo;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

import com.application.demo.R;

import java.io.IOException;

class GalleryAdapter extends ListAdapter<VideoInfo, ImageViewHolder> {

    private final OnGalleryClickListener listener;

    protected GalleryAdapter(OnGalleryClickListener listener) {
        super(VideoInfo.DiffCallback);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery_layout, parent, false);
        return new ImageViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        VideoInfo videoInfo = getItem(position);
        holder.imageView.setTag(videoInfo);

        Bitmap thumbnail;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                thumbnail = holder.itemView.getContext().getContentResolver().loadThumbnail(
                        videoInfo.localPathUri, new Size(640, 480), null);
            } catch (IOException e) {
                e.printStackTrace();
                thumbnail = null;
            }
        } else {
            thumbnail = null;
        }

        Log.d("xcl_debug", "onBindViewHolder: 再次获取");

        if (thumbnail == null) {
            holder.imageView.setImageBitmap(videoInfo.firstFrame);
        } else {
            holder.imageView.setImageBitmap(thumbnail);
        }
    }
}
