package com.example.filemanager.presenter

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class MainViewModel(application: Application): AndroidViewModel(application) {


    val state = MutableLiveData<State>()
    private val fileDiff = FileDifferent(application)


    @OptIn(ExperimentalTime::class)
    fun startCalculateFileDiff(){
        state.value = Loading
        viewModelScope.launch {
            val (result: List<File>, duration: Duration) = measureTimedValue {
                fileDiff.getCountChangedFile()
            }
            state.value = FileChanged(result)

            Log.d("TAGGGGGGG","Got $result after ${duration.toDouble(DurationUnit.MILLISECONDS)} ms.")
        }
    }


}

