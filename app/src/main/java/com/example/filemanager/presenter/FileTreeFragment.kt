package com.example.filemanager.presenter


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.filemanager.R
import com.example.filemanager.domain.SortDirection
import com.example.filemanager.domain.SortType
import com.example.filemanager.databinding.FragmentFileTreeBinding
import com.example.filemanager.domain.FileEntity
import com.example.filemanager.mapFileIoListToFileEntities
import com.example.filemanager.presenter.adapter.FileAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class FileTreeFragment : Fragment(), FilePresenterActivity.OnSortConfigChangeListener {

    private lateinit var sortedConfig: HashMap<SortType, SortDirection>
    private lateinit var pathString: String

    private lateinit var directoryFiles: List<FileEntity>

    private lateinit var fileAdapter: FileAdapter
    private var _binding: FragmentFileTreeBinding? = null
    private val binding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentFileListBinding is null")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseIntent()
    }

    private fun parseIntent() {
        if (!requireArguments().containsKey(PATH_KEY)) {
            throw RuntimeException("argument pathString is absent")
        }
        if (!requireArguments().containsKey(SORTED_CONFIG_KEY)) {
            throw RuntimeException("argument SORTED_CONFIG_KEY is absent")
        }
        pathString = requireArguments().getString(PATH_KEY)
            ?: throw RuntimeException("argument pathString is null")
        requireArguments().getSerializable(SORTED_CONFIG_KEY).apply {
            if (this == null) throw RuntimeException("argument SORTED_CONFIG_KEY is null")
            sortedConfig = this as HashMap<SortType, SortDirection>
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
            val file = File(pathString)
            binding.filesRv.adapter = FileAdapter().apply {
                fileAdapter = this

                if (file.listFiles() != null) {
                    lifecycleScope.launch {
                        binding.progressBar.visibility = View.VISIBLE
                        Log.d("AAATAG", "start")
                        launch(Dispatchers.Default) {
                            Log.d("AAATAG", "statr2 ${file.listFiles().size}")
                            directoryFiles = mapFileIoListToFileEntities(file.listFiles()!!)
                            Log.d("AAATAG", "finish")
                            withContext(Dispatchers.Main){
                                binding.progressBar.visibility = View.GONE
                                submitList(directoryFiles.sort(sortedConfig))
                            }
                        }

                    }
                }
                onFileClicked = {
                    requireActivity().supportFragmentManager.beginTransaction().replace(
                        R.id.fragment_files, newIntent(it.absolutePath, sortedConfig)
                    ).addToBackStack(null).commit()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun List<FileEntity>.sort(hashMap: HashMap<SortType, SortDirection>): List<FileEntity> {
        hashMap.apply {
            val size: ((FileEntity) -> Comparable<*>) = {
                when (get(SortType.SIZE)) {
                    SortDirection.TO_ASCENDING -> it.size
                    SortDirection.TO_DESCENDING -> Long.MAX_VALUE - it.size
                    SortDirection.NO_SORT -> 0
                    null -> 0
                }
            }
            val name: ((FileEntity) -> Comparable<*>) = {
                when (get(SortType.NAME)) {
                    SortDirection.TO_ASCENDING -> it.name
                    SortDirection.TO_DESCENDING -> it.name
                    SortDirection.NO_SORT -> 0
                    null -> 0
                }
            }
            val expansion: ((FileEntity) -> Comparable<*>) = {
                when (get(SortType.EXPANSION)) {
                    SortDirection.TO_ASCENDING -> it.fileType
                    SortDirection.TO_DESCENDING -> it.fileType
                    SortDirection.NO_SORT -> 0
                    null -> 0
                }
            }
            val date: ((FileEntity) -> Comparable<*>) = {
                when (get(SortType.DATE)) {
                    SortDirection.TO_ASCENDING -> it.timeCreated
                    SortDirection.TO_DESCENDING -> Long.MAX_VALUE - it.timeCreated
                    SortDirection.NO_SORT -> 0
                    null -> 0
                }
            }
            return directoryFiles.sortedWith(compareBy(name, size, expansion, date))
        }
    }

    override fun onCategoryChanged(map: HashMap<SortType, SortDirection>) {
        sortedConfig = map
        fileAdapter.submitList(directoryFiles.sort(map))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private val SORTED_CONFIG_KEY = "sort_conf"
        private val PATH_KEY = "path"
        fun newIntent(
            pathString: String, sortedConfig: HashMap<SortType, SortDirection>
        ): FileTreeFragment {
            return FileTreeFragment().apply {
                arguments = Bundle().apply {
                    putString(PATH_KEY, pathString)
                    putSerializable(SORTED_CONFIG_KEY, sortedConfig)
                }
            }

        }

    }


}