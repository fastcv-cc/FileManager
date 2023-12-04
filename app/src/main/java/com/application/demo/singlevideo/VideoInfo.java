package com.application.demo.singlevideo;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class VideoInfo extends MediaInfo {
    // 视频第一帧图
    Bitmap firstFrame;

    // 视频长度 ms
    long duration = 0L;

    // 视频码率 bps
    long biteRate = 0L;

    /* --------not necessary, maybe not value---- */
    // 视频添加时间
    long addTime = 0L;

    // 视频方向
    int videoRotation = 0;


    @Override
    public String toString() {
        return "VideoInfo{" +
                "firstFrame=" + firstFrame +
                ", duration=" + duration +
                ", biteRate=" + biteRate +
                ", addTime=" + addTime +
                ", videoRotation=" + videoRotation +
                ", size=" + size +
                ", width=" + width +
                ", height=" + height +
                ", localPathUri=" + localPathUri +
                ", localPath='" + localPath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", mediaId='" + mediaId + '\'' +
                ", lastModified=" + lastModified +
                '}';
    }

    public static DiffUtil.ItemCallback<VideoInfo> DiffCallback = new DiffUtil.ItemCallback<VideoInfo>() {
        @Override
        public boolean areItemsTheSame(@NonNull VideoInfo oldItem, @NonNull VideoInfo newItem) {
            return oldItem.mediaId == newItem.mediaId;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull VideoInfo oldItem, @NonNull VideoInfo newItem) {
            return oldItem == newItem;
        }
    };

}
