package com.wittyneko.aisingers

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File
import java.net.InetSocketAddress

class MainViewModule(
    val application: Application
) : ViewModel() {

    var list = MutableLiveData<Array<File>>(arrayOf())
    lateinit var current: File

    init {
        getList()
    }

    override fun onCleared() {

        super.onCleared()
    }

    fun getList() {
        viewModelScope.launch {
            application.getExternalFilesDir("model")?.listFiles { it ->
                it.isFile && it.name.endsWith(".nn")
            }?.apply {
                list.value = this
            }
        }
    }
}