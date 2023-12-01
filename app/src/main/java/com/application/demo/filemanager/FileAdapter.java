package com.application.demo.filemanager;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.demo.R;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.VH> {

    private OnItemClickListener<File> itemClickListener;
    private OnItemLongClickListener<File> itemLongClickListener;

    private ArrayList<File> list = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setData(File[] files) {
        list.clear();
        list.addAll(Arrays.asList(files));
        notifyDataSetChanged();
    }

    public void setItemClickListener(OnItemClickListener<File> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(OnItemLongClickListener<File> itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (list.get(position).isDirectory()) {
            holder.ivIcon.setImageResource(R.drawable.ic_folder);
            holder.itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClickListener(list.get(position), position);
                }
            });
        } else {
            holder.ivIcon.setImageResource(R.drawable.ic_file);
            holder.itemView.setOnClickListener(null);
        }
        holder.tvTitle.setText(list.get(position).getName());
        holder.ivState.setImageResource(getStateResource(position));
        holder.itemView.setOnLongClickListener(v -> {
            if (itemLongClickListener != null) {
                return itemLongClickListener.onItemLongClickListener(list.get(position), position);
            } else {
                return false;
            }
        });
    }

    private int getStateResource(int position) {
        boolean read = list.get(position).canRead();
        boolean write = list.get(position).canRead();
        if (read && !write) {
            return R.drawable.ic_only_read;
        } else if (!read && write) {
            return R.drawable.ic_only_write;
        } else {
            return R.drawable.ic_read_and_write;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        public ImageView ivIcon;
        public ImageView ivState;
        public TextView tvTitle;

        public VH(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            ivState = itemView.findViewById(R.id.iv_state);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }

    public interface OnItemClickListener<T> {
        void onItemClickListener(T t, int position);
    }

    interface OnItemLongClickListener<T> {
        boolean onItemLongClickListener(T t, int position);
    }
}
