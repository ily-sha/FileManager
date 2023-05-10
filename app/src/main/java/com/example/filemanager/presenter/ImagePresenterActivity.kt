package com.example.filemanager.presenter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.filemanager.databinding.ActivityImagePresenterBinding
import com.example.filemanager.domain.FileEntity

class ImagePresenterActivity : AppCompatActivity() {

    private val binding: ActivityImagePresenterBinding by lazy {
        ActivityImagePresenterBinding.inflate(layoutInflater)
    }

    private lateinit var fileEntity: FileEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        parseIntent()
        binding.imageView.setImageURI(fileEntity.uri)
        binding.imageName.text = fileEntity.name
        binding.buttonBack.setOnClickListener {
            finish()
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
            return Intent(context, ImagePresenterActivity::class.java).apply {
                putExtra(FILE_KEY, fileEntity)
            }

        }
    }
}