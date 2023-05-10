package com.example.filemanager.presenter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

class MainViewModel(application: Application) : AndroidViewModel(application) {


    val state = MutableLiveData<State>()
    private val fileDiff = FileDifferent(application)


    fun startCalculateFileDiff() {
        state.value = Loading
        viewModelScope.launch {
            fileDiff.getCountChangedFile()
            state.value = FileUploaded(fileDiff.newFiles, fileDiff.changedFiles)
        }
    }

    override fun onCleared() {
        super.onCleared()
        fileDiff.coroutineScope.cancel()
    }


}

