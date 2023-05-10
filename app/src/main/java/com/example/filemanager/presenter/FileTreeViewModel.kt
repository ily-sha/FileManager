package com.example.filemanager.presenter

import androidx.lifecycle.ViewModel
import com.example.filemanager.domain.FileEntity
import com.example.filemanager.domain.SortDirection
import com.example.filemanager.domain.SortType

class FileTreeViewModel:ViewModel() {



    fun sort(fileList: List<FileEntity>, hashMap: HashMap<SortType, SortDirection>): List<FileEntity> {
        val dateSortedType = { file: FileEntity -> file.timeCreated }
        val sizeSortedType = { file: FileEntity -> Long.MAX_VALUE - file.size }
        val nameSortedFun = { file: FileEntity -> file.name }
        val typeSortedFun = { file: FileEntity -> file.fileType.name }
        val comparator: ((Comparator<FileEntity>, ((FileEntity) -> Comparable<*>), SortType) -> Comparator<FileEntity>) =
            { comparator, sortedFun, sortType ->
                when (hashMap[sortType]) {
                    SortDirection.TO_ASCENDING -> comparator.thenBy(sortedFun)
                    SortDirection.TO_DESCENDING -> comparator.thenByDescending(sortedFun)
                    SortDirection.NO_SORT -> comparator
                    null -> comparator
                }
            }
        return fileList.sortedWith(
            comparator(
                comparator(
                    comparator(
                        comparator(
                            Comparator { it1, it2 -> 0 },
                            nameSortedFun, SortType.NAME
                        ), typeSortedFun, SortType.EXPANSION
                    ), sizeSortedType, SortType.SIZE
                ), dateSortedType, SortType.DATE
            )
        )
    }
}