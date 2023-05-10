package com.example.filemanager.presenter

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import com.example.filemanager.R
import com.example.filemanager.databinding.ActivityVideoPlayerBinding
import com.example.filemanager.domain.FileEntity

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var fileEntity: FileEntity

    private val binding by lazy { ActivityVideoPlayerBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        parseIntent()
        findViewById<TextView>(R.id.video_name).text = fileEntity.name
        findViewById<ImageView>(R.id.button_back).setOnClickListener {
            finish()
        }
        findViewById<VideoView>(R.id.video_view).apply {
            setVideoURI(fileEntity.uri);
            setMediaController(MediaController(this@VideoPlayerActivity));
            requestFocus(0);
            start()
        }

    }

    private fun parseIntent(){
        if (intent.extras?.containsKey(FILE_KEY) == true) {
            fileEntity = intent.extras!!.getParcelable(FILE_KEY)
                ?: throw RuntimeException("fileEntity in extras is null")

        } else throw RuntimeException("extras is absent")
    }

    companion object {
        private const val FILE_KEY = "file_key"
        fun newIntent(context: Context, fileEntity: FileEntity): Intent {
            return Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra(FILE_KEY, fileEntity)
            }

        }
    }
}