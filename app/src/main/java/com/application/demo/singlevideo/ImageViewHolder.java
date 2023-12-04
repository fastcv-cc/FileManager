package com.application.demo.singlevideo;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.demo.R;

class ImageViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;

    public ImageViewHolder(@NonNull View itemView, OnGalleryClickListener listener) {
        super(itemView);
        imageView = itemView.findViewById(R.id.image);
        imageView.setOnClickListener(v -> {
            Object tag = imageView.getTag();
            Log.d("ImageViewHolder", "ImageViewHolder: tag = " + tag + "   listener = " + listener);
            if (tag != null && listener != null) {
                listener.onClick((VideoInfo) tag);
            }
        });
    }
}
