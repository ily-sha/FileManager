package com.example.filemanager.presenter.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.filemanager.domain.FileEntity

class FileDiffCallBack : DiffUtil.ItemCallback<FileEntity>() {
    override fun areItemsTheSame(oldItem: FileEntity, newItem: FileEntity): Boolean {
        return oldItem.absolutePath == newItem.absolutePath
    }

    override fun areContentsTheSame(oldItem: FileEntity, newItem: FileEntity): Boolean {
        return oldItem == newItem
    }
}