package com.application.demo.singleimage;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

import com.application.demo.R;

import java.io.IOException;

public class GalleryAdapter extends ListAdapter<MediaStoreImage, ImageViewHolder> {

    private final OnGalleryClickListener listener;

    protected GalleryAdapter(OnGalleryClickListener listener) {
        super(MediaStoreImage.DiffCallback);
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
        MediaStoreImage mediaStoreImage = getItem(position);
        holder.imageView.setTag(mediaStoreImage);

        Bitmap thumbnail;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                thumbnail = holder.itemView.getContext().getContentResolver().loadThumbnail(
                        mediaStoreImage.contentUri, new Size(640, 480), null);
            } catch (IOException e) {
                e.printStackTrace();
                thumbnail = null;
            }
        } else {
            thumbnail = null;
        }
        if (thumbnail == null) {
            holder.imageView.setImageURI(null);
            holder.imageView.setImageURI(mediaStoreImage.contentUri);
        } else {
            holder.imageView.setImageBitmap(thumbnail);
        }
    }
}
