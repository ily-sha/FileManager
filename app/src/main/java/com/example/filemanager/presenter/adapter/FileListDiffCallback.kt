package com.example.filemanager.presenter.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.filemanager.domain.FileEntity

class FileListDiffCallback(
    private val newList: List<FileEntity>,
    private val oldList: List<FileEntity>
) : DiffUtil.Callback() {


    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition].absolutePath == oldList[oldItemPosition].absolutePath
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList == oldList
    }
}