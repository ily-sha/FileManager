package com.example.filemanager.presenter

import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.filemanager.R
import com.example.filemanager.domain.SortDirection
import com.example.filemanager.domain.SortType
import com.example.filemanager.databinding.ActivityFilePresenterBinding
import com.google.android.material.bottomsheet.BottomSheetDialog


class FilePresenterActivity : AppCompatActivity() {


    interface OnSortConfigChangeListener {
        fun onCategoryChanged(map: HashMap<SortType, SortDirection>)
    }

    private val binding by lazy { ActivityFilePresenterBinding.inflate(layoutInflater) }

    private val defaultSortedConfig = hashMapOf(
        SortType.DATE to SortDirection.NO_SORT,
        SortType.SIZE to SortDirection.NO_SORT,
        SortType.EXPANSION to SortDirection.NO_SORT,
        SortType.NAME to SortDirection.TO_ASCENDING,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val rootPath = Environment.getExternalStorageDirectory().absolutePath
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(
                R.id.fragment_files,
                FileTreeFragment.newIntent(rootPath, defaultSortedConfig)
            ).commit()
        }
        binding.dateSortType.setOnClickListener {
            showBottomSheetDialog(SortType.DATE)
        }
        binding.sizeSortType.setOnClickListener {
            showBottomSheetDialog(SortType.SIZE)
        }
        binding.expansionSortType.setOnClickListener {
            showBottomSheetDialog(SortType.EXPANSION)
        }
        binding.nameSortType.setOnClickListener {
            showBottomSheetDialog(SortType.NAME)
        }
    }

    private fun showBottomSheetDialog(sortType: SortType) {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog)
        bottomSheetDialog.findViewById<TextView>(R.id.ascending_order)?.setOnClickListener {
            switchSortDirection(sortType, SortDirection.TO_ASCENDING)
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.findViewById<TextView>(R.id.descending_order)?.setOnClickListener {
            switchSortDirection(sortType, SortDirection.TO_DESCENDING)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.findViewById<TextView>(R.id.no_sort)?.setOnClickListener {
            when (sortType) {
                SortType.SIZE -> {
                    binding.sizeArrowUpward.visibility = View.GONE
                    binding.sizeArrowDownward.visibility = View.GONE
                    binding.sizeSortType.background.setTint(resources.getColor(R.color.white))
                    binding.sizeSortType.strokeColor = resources.getColor(R.color.dark_gray)
                    defaultSortedConfig[SortType.SIZE] = SortDirection.NO_SORT
                }
                SortType.NAME -> {
                    binding.nameArrowUpward.visibility = View.GONE
                    binding.nameArrowDownward.visibility = View.GONE
                    binding.nameSortType.background.setTint(resources.getColor(R.color.white))
                    binding.nameSortType.strokeColor = resources.getColor(R.color.dark_gray)
                    defaultSortedConfig[SortType.NAME] = SortDirection.NO_SORT
                }

                SortType.DATE -> {
                    binding.dateArrowUpward.visibility = View.GONE
                    binding.dateArrowDownward.visibility = View.GONE
                    binding.dateSortType.background.setTint(resources.getColor(R.color.white))
                    binding.dateSortType.strokeColor = resources.getColor(R.color.dark_gray)
                    defaultSortedConfig[SortType.DATE] = SortDirection.NO_SORT
                }

                SortType.EXPANSION -> {
                    binding.expansionArrowUpward.visibility = View.GONE
                    binding.expansionArrowDownward.visibility = View.GONE
                    binding.expansionSortType.background.setTint(resources.getColor(R.color.white))
                    binding.expansionSortType.strokeColor = resources.getColor(R.color.dark_gray)
                    defaultSortedConfig[SortType.EXPANSION] = SortDirection.NO_SORT
                }
            }
            bottomSheetDialog.dismiss()
            callFragmentAboutChange()
        }
        bottomSheetDialog.show()
    }

    private fun switchSortDirection(sortType: SortType, direction: SortDirection) {
        val (visibleView, invisibleView) = when (direction) {
            SortDirection.TO_DESCENDING -> listOf(View.GONE, View.VISIBLE)
            SortDirection.TO_ASCENDING -> listOf(View.VISIBLE, View.GONE)
            else -> throw RuntimeException("switchSortDirection dont receive $sortType SortTypeCause")
        }
        when (sortType) {
            SortType.SIZE -> {
                binding.sizeArrowUpward.visibility = visibleView
                binding.sizeArrowDownward.visibility = invisibleView
                binding.sizeSortType.background.setTint(resources.getColor(R.color.gray))
                binding.sizeSortType.strokeColor = resources.getColor(R.color.gray)
                defaultSortedConfig[SortType.SIZE] = direction
            }
            SortType.NAME -> {
                binding.nameArrowUpward.visibility = visibleView
                binding.nameArrowDownward.visibility = invisibleView
                binding.nameSortType.background.setTint(resources.getColor(R.color.gray))
                binding.nameSortType.strokeColor = resources.getColor(R.color.gray)
                defaultSortedConfig[SortType.NAME] = direction
            }

            SortType.DATE -> {
                binding.dateArrowUpward.visibility = visibleView
                binding.dateArrowDownward.visibility = invisibleView
                binding.dateSortType.background.setTint(resources.getColor(R.color.gray))
                binding.dateSortType.strokeColor = resources.getColor(R.color.gray)
                defaultSortedConfig[SortType.DATE] = direction
            }

            SortType.EXPANSION -> {
                binding.expansionArrowUpward.visibility = visibleView
                binding.expansionArrowDownward.visibility = invisibleView
                binding.expansionSortType.background.setTint(resources.getColor(R.color.gray))
                binding.expansionSortType.strokeColor = resources.getColor(R.color.gray)
                defaultSortedConfig[SortType.EXPANSION] = direction
            }
        }
        callFragmentAboutChange()
    }

    private fun callFragmentAboutChange(){
        supportFragmentManager.fragments.forEach {
            if (it is OnSortConfigChangeListener){
                it.onCategoryChanged(defaultSortedConfig)
            } else throw RuntimeException("fragment inside FilePresenterActivity must implement OnSortConfigChangeListener")
        }
    }






}