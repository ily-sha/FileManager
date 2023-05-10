package com.example.filemanager.presenter.adapter

import android.media.MediaMetadataRetriever
import android.text.format.Formatter.formatShortFileSize
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.filemanager.R
import com.example.filemanager.domain.FileEntity
import com.example.filemanager.domain.FileType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class FileAdapter: ListAdapter<FileEntity, FileAdapter.FileViewHolder>(FileDiffCallBack()) {



    var onFileClicked: ((FileEntity) -> Unit) = {}

    class FileViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.file_image)
        val name = view.findViewById<TextView>(R.id.file_name)
        val size = view.findViewById<TextView>(R.id.file_size)
        val created_time = view.findViewById<TextView>(R.id.file_created_time)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return FileViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.file_item, parent, false))
    }


    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val item = getItem(position)
        holder.created_time.text = formatTime(item.timeCreated)
        holder.name.text = item.name
        when {
            item.isDirectory -> {
                holder.image.setImageResource(R.drawable.icon_folder)
                holder.image.scaleType = ImageView.ScaleType.FIT_CENTER
                holder.size.text = ""
                holder.view.setOnClickListener {
                    onFileClicked.invoke(item)
                }
            }
            item.isFile -> {
                val formattedSize = formatShortFileSize(holder.view.context, item.size)
                holder.size.text = formattedSize

                    when (item.fileType) {
                        FileType.JPEG, FileType.JPG, FileType.PNG, FileType.WEBP -> {
                            holder.image.setImageURI(item.uri)
                            holder.image.scaleType = ImageView.ScaleType.CENTER_CROP
                        }
                        FileType.MP4,  FileType.MOV -> {
                            val media = MediaMetadataRetriever()
                            media.setDataSource(item.uri.toString())
                            holder.image.setImageBitmap(media.frameAtTime)
                            holder.image.scaleType = ImageView.ScaleType.CENTER_CROP
                        }
                        FileType.ELSE -> {
                            holder.image.setImageResource(R.drawable.icon_file)
                            holder.image.scaleType = ImageView.ScaleType.FIT_CENTER
                        }
                    }

                    }
//                    if (imgBitmap != null){
//                        holder.image.setImageBitmap(imgBitmap)
//
//                        currentList[position] = item.copy(bitmap = imgBitmap)
//                    }
//                    holder.image.setImageResource(R.drawable.icon_file)

            }
    }



    private fun formatTime(time: Long): String {

        val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return format.format(Date(time))

    }
}