package com.example.filemanager.presenter.adapter

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.text.format.Formatter.formatShortFileSize
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.filemanager.R
import com.example.filemanager.domain.FileEntity
import com.example.filemanager.domain.FileType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class FileAdapter :RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    var fileList = mutableListOf<FileEntity>()
        set(value) {
            val callback = FileListDiffCallback(fileList, value)
            val diffResult = DiffUtil.calculateDiff(callback)
            diffResult.dispatchUpdatesTo(this)
            field = value
        }

    var onFolderClicked: ((FileEntity) -> Unit) = {}
    var onImageClicked: ((FileEntity) -> Unit) = {}
    var onVideoClicked: ((FileEntity) -> Unit) = {}

    var onImageSendClicked: ((FileEntity) -> Unit) = {}
    var onVideoSendClicked: ((FileEntity) -> Unit) = {}

    class FileViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.file_image)
        val name = view.findViewById<TextView>(R.id.file_name)
        val size = view.findViewById<TextView>(R.id.file_size)
        val created_time = view.findViewById<TextView>(R.id.file_created_time)
        val send = view.findViewById<ImageView>(R.id.send_button)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return FileViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.file_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return fileList.size
    }


    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val item = fileList[position]
        holder.created_time.text = formatTime(item.timeCreated)
        holder.name.text = item.name
        when {
            item.isDirectory -> {
                holder.send.visibility = View.GONE
                holder.image.setImageResource(R.drawable.icon_folder)
                holder.image.scaleType = ImageView.ScaleType.FIT_CENTER
                holder.size.text = ""
                holder.view.setOnClickListener {
                    onFolderClicked.invoke(item)
                }
            }

            item.isFile -> {
                val formattedSize = formatShortFileSize(holder.view.context, item.size)
                holder.size.text = formattedSize

                when (item.fileType) {
                    FileType.JPEG, FileType.JPG, FileType.PNG, FileType.WEBP -> {
                        if (item.bitmap == null){
                            val bmOptions = BitmapFactory.Options()
                            val bitmap = BitmapFactory.decodeFile(item.absolutePath, bmOptions)
                            holder.image.setImageBitmap(bitmap)
                            fileList[position] = item.copy(bitmap = bitmap)
                        } else {
                            holder.image.setImageBitmap(item.bitmap)
                        }
                        holder.image.scaleType = ImageView.ScaleType.CENTER_CROP
                        holder.view.setOnClickListener {
                            onImageClicked.invoke(item)
                        }
                        holder.send.visibility = View.VISIBLE
                        holder.send.setOnClickListener {
                            onImageSendClicked.invoke(item)
                        }
                    }

                    FileType.MP4, FileType.MOV -> {
                        holder.send.visibility = View.VISIBLE

                        if (item.bitmap == null){
                            val media = MediaMetadataRetriever()
                            media.setDataSource(item.uri.toString())
                            val bitmap = media.frameAtTime
                            holder.image.setImageBitmap(bitmap)
                            fileList[position] = item.copy(bitmap = bitmap)
                        } else {
                            holder.image.setImageBitmap(item.bitmap)
                        }

                        holder.image.scaleType = ImageView.ScaleType.CENTER_CROP
                        holder.view.setOnClickListener {
                            onVideoClicked.invoke(item)
                        }
                        holder.send.setOnClickListener {
                            onVideoSendClicked.invoke(item)
                        }
                    }

                    FileType.ELSE -> {
                        holder.send.visibility = View.GONE
                        holder.image.setImageResource(R.drawable.icon_file)
                        holder.image.scaleType = ImageView.ScaleType.FIT_CENTER
                    }
                }

            }
        }
    }


    private fun formatTime(time: Long): String {

        val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return format.format(Date(time))

    }
}