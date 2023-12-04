package com.application.demo.singlevideo;

import android.annotation.SuppressLint;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

class MediaInfo {

    // 大小 单位B
    public long size;

    // 宽
    public float width;

    // 高
    public float height;

    // 媒体文件Uri
    public Uri localPathUri;

    // 本地文件Path
    public String localPath;

    // 文件名
    public String fileName;

    // 媒体类型
    public String mimeType;

    // 媒体ID
    public String mediaId;

    // 最后更改时间
    public Long lastModified;

}
