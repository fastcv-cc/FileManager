package com.application.demo.singleimage;

import android.annotation.SuppressLint;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Date;

public class MediaStoreImage {

    public long id;
    public String displayName;
    public Date dateAdded;
    public Uri contentUri;
    public double lat;
    public double lon;

    public static DiffUtil.ItemCallback<MediaStoreImage> DiffCallback = new DiffUtil.ItemCallback<MediaStoreImage>() {
        @Override
        public boolean areItemsTheSame(@NonNull MediaStoreImage oldItem, @NonNull MediaStoreImage newItem) {
            return oldItem.id == newItem.id;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull MediaStoreImage oldItem, @NonNull MediaStoreImage newItem) {
            return oldItem == newItem;
        }
    };

}
