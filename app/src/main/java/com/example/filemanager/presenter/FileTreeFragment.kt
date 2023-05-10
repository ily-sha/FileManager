package com.example.filemanager.presenter


import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.filemanager.R
import com.example.filemanager.databinding.FragmentFileTreeBinding
import com.example.filemanager.domain.FileEntity
import com.example.filemanager.domain.SharingContentType
import com.example.filemanager.domain.SortDirection
import com.example.filemanager.domain.SortType
import com.example.filemanager.mapFileIoListToFileEntities
import com.example.filemanager.presenter.adapter.FileAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class FileTreeFragment : Fragment(), FilePresenterActivity.OnSortConfigChangeListener {

    private lateinit var sortedConfigHashMap: HashMap<SortType, SortDirection>
    private lateinit var directoryFiles: List<FileEntity>
    private lateinit var fileAdapter: FileAdapter
    private var _binding: FragmentFileTreeBinding? = null
    private val binding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentFileListBinding is null")


    private val viewModel by lazy { ViewModelProvider(this)[FileTreeViewModel::class.java] }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseIntent()
    }

    private fun parseIntent() {
        if (!(requireArguments().containsKey(FILE_LIST_KEY) && requireArguments().containsKey(
                SORTED_CONFIG_KEY))) {
            throw RuntimeException("lack argument")
        }
        requireArguments().getSerializable(SORTED_CONFIG_KEY).apply {
            if (this == null) throw RuntimeException("argument SORTED_CONFIG_KEY is null")
            sortedConfigHashMap = this as HashMap<SortType, SortDirection>
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelableArrayList(FILE_LIST_KEY, FileEntity::class.java).apply {
                if (this == null) throw RuntimeException("argument FILE_LIST_KEY is null")
                directoryFiles = this
            }
        } else requireArguments().getParcelableArrayList<FileEntity>(FILE_LIST_KEY).apply {
            if (this == null) throw RuntimeException("argument FILE_LIST_KEY is null")
            directoryFiles = this
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFileTreeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            binding.filesRv.adapter = FileAdapter().apply {
                fileAdapter = this
                fileList = viewModel.sort(directoryFiles, sortedConfigHashMap).toMutableList()
                onFolderClicked = {
                    lifecycleScope.launch(Dispatchers.IO) {
                        requireActivity().supportFragmentManager.beginTransaction().replace(
                            R.id.fragment_files, newIntent(getFolderFiles(it), sortedConfigHashMap)
                        ).addToBackStack(null).commit()
                    }
                }
                onImageClicked = {
                    imageClick(it)
                }
                onVideoClicked = {
                    videoClick(it)
                }
                onImageSendClicked = {
                    shareFile(it, SharingContentType.IMAGE)
                }
                onVideoSendClicked = {
                    shareFile(it, SharingContentType.VIDEO)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun getFolderFiles(file: FileEntity): ArrayList<FileEntity> {
        withContext(Dispatchers.Main) {
            binding.progressBar.visibility = View.VISIBLE
        }
        val mapList = mapFileIoListToFileEntities(
            File(file.absolutePath).listFiles() ?: emptyArray()
        ) as ArrayList<FileEntity>

        withContext(Dispatchers.Main) {
            binding.progressBar.visibility = View.GONE
        }
        return mapList
    }

    private fun videoClick(file: FileEntity) {
        val intent = VideoPlayerActivity.newIntent(requireContext(), file)
        startActivity(intent)
    }

    private fun imageClick(file: FileEntity) {
        val intent = ImagePresenterActivity.newIntent(requireContext(), file)
        startActivity(intent)
    }

    private fun shareFile(fileEntity: FileEntity, sharingContentType: SharingContentType) {
        val share = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, getUriByFile(fileEntity))
            type = sharingContentType.type
        }, null)
        startActivity(share)
    }


    private fun getUriByFile(file: FileEntity): Uri {
        return FileProvider.getUriForFile(
            requireContext(), "com.example.filemanager.example.provider", File(file.absolutePath)
        )
    }

    override fun onCategoryChanged(map: HashMap<SortType, SortDirection>) {
        sortedConfigHashMap = map
        fileAdapter.fileList = viewModel.sort(directoryFiles, map).toMutableList()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private val SORTED_CONFIG_KEY = "sort_conf"
        private val FILE_LIST_KEY = "file_list"
        fun newIntent(
            fileList: ArrayList<FileEntity>, sortedConfigMap: HashMap<SortType, SortDirection>
        ): FileTreeFragment {
            return FileTreeFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(FILE_LIST_KEY, fileList)
                    putSerializable(SORTED_CONFIG_KEY, sortedConfigMap)
                }
            }


        }

    }


}