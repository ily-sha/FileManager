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
import com.example.filemanager.domain.FileEntity
import com.example.filemanager.domain.SortDirection
import com.example.filemanager.domain.SortType
import com.example.filemanager.mapFileIoListToFileEntities

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

    private val defaultSortedConfig = hashMapOf(
        SortType.DATE to SortDirection.NO_SORT,
        SortType.SIZE to SortDirection.NO_SORT,
        SortType.EXPANSION to SortDirection.NO_SORT,
        SortType.NAME to SortDirection.TO_ASCENDING,
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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
                    binding.fileDifferentLayout.visibility = View.GONE
                    binding.loadingLayout.visibility = View.VISIBLE
                }
                is FileUploaded -> {
                    binding.permissionDenial.visibility = View.GONE
                    binding.fileDifferentLayout.visibility = View.VISIBLE
                    binding.loadingLayout.visibility = View.GONE
                    binding.fileChangedTv.text =
                        String.format(getString(R.string.count_changed_files), it.changedFiles.size)
                    binding.fileAddedTv.text =
                        String.format(getString(R.string.count_added_files), it.newFiles.size)
                    supportFragmentManager.beginTransaction().replace(
                        binding.filesChangedFragment.id,
                        FileTreeFragment.newIntent(
                            mapFileIoListToFileEntities(
                                it.changedFiles.toTypedArray()
                            ) as ArrayList<FileEntity>,
                            defaultSortedConfig
                        )
                    ).commit()
                    supportFragmentManager.beginTransaction()
                        .replace(
                            binding.filesAddedFragment.id, FileTreeFragment.newIntent(
                                mapFileIoListToFileEntities(
                                    it.newFiles.toTypedArray()
                                ) as ArrayList<FileEntity>,
                                defaultSortedConfig
                            )
                        )
                        .commit()

                }

                is PermissionDenial -> {
                    binding.permissionDenial.visibility = View.VISIBLE
                    binding.fileDifferentLayout.visibility = View.GONE
                    binding.loadingLayout.visibility = View.GONE
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!permissionAccept()) {
            requestPermission()
        } else viewModel.startCalculateFileDiff()
    }


    override fun onRestart() {
        super.onRestart()
        if (viewModel.state.value is FileUploaded){
            binding.recalculateFileChanged.visibility = View.VISIBLE
            binding.recalculateFileChanged.setOnClickListener {
                viewModel.startCalculateFileDiff()
            }
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