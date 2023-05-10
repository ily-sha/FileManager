package com.example.filemanager.presenter

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.filemanager.R
import com.example.filemanager.databinding.ActivityMainBinding
import com.example.filemanager.mapFileIoListToFileEntities
import com.example.filemanager.presenter.adapter.FileAdapter

class MainActivity : AppCompatActivity() {
    private val READ_STORAGE_PERMISSION_REQUEST_CODE = 41


    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val viewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    private val permissionsResultCallback = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) viewModel.startCalculateFileDiff()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        lifecycleScope.launch {
//            FileDifferent(application).apply {
//                val h = File(Environment.getExternalStorageDirectory().absolutePath + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images/Копия IMG_20230509_195337.jpg").computeMd5()
//                val j2 = File(Environment.getExternalStorageDirectory().absolutePath + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images/IMG_20230509_195337.jpg").computeMd5()
//                Log.d("EEEEEEEE", h)
//                Log.d("EEEEEEEE", j2)
//                Log.d("EEEEEEEE", (j2 == h).toString())
//
//            }
//        }
        if (!permissionAccept()) {
            requestPermission()
        } else viewModel.startCalculateFileDiff()
        binding.fileTreeCardview.setOnClickListener {
            if (!permissionAccept()) {
                requestPermission()
            } else {
                val intent = Intent(this, FilePresenterActivity::class.java)
                startActivity(intent)
            }
        }
        observeLiveDate()

    }

    private fun observeLiveDate() {

        viewModel.state.observe(this) {
            when (it) {
                is Loading -> {
                    binding.permissionDenial.visibility = View.GONE
                    binding.fileChangedLayout.visibility = View.GONE
                    binding.loadingLayout.visibility = View.VISIBLE
                }

                is FileChanged -> {
                    binding.permissionDenial.visibility = View.GONE
                    binding.fileChangedLayout.visibility = View.VISIBLE
                    binding.loadingLayout.visibility = View.GONE
                    binding.fileChangedTv.text =
                        String.format(getString(R.string.count_changed_files), it.filesList.size)
                    binding.filesRv.adapter = FileAdapter().apply {
                        submitList(mapFileIoListToFileEntities(it.filesList.toTypedArray()))
                    }
                }

                is PermissionDenial -> {
                    binding.permissionDenial.visibility = View.VISIBLE
                    binding.fileChangedLayout.visibility = View.GONE
                    binding.loadingLayout.visibility = View.GONE
                }
            }
        }
    }





    override fun onRestart() {
        super.onRestart()
        binding.recalculateFileChanged.visibility = View.VISIBLE
        binding.recalculateFileChanged.setOnClickListener {
            viewModel.startCalculateFileDiff()
        }
    }






    private fun permissionAccept(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (result == PackageManager.PERMISSION_DENIED) {
            viewModel.state.value = PermissionDenial
            permissionsResultCallback.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            READ_STORAGE_PERMISSION_REQUEST_CODE
        )

    }

}