package com.wittyneko.aisingers.ui.edit

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wittyneko.aisingers.MainViewModule
import kotlinx.coroutines.launch
import java.io.File
import java.net.InetSocketAddress

class EditViewModule(
    val application: Application,
    val mainViewModule: MainViewModule
) : ViewModel() {

    val current: File = mainViewModule.current

    private var _lines = listOf<String>()
        set(value) {
            field = value
            lines.value = value
        }
    var lines = MutableLiveData(_lines)
    private var _chars = listOf(listOf<String>())
        set(value) {
            field = value
            chars.value = value
        }
    var chars = MutableLiveData(_chars)

    init {
        read()
    }

    override fun onCleared() {

        super.onCleared()
    }

    fun read() {
        val _lines = current.readLines()
        _chars = _lines.map { it.split(' ') }
//        _chars.forEach {
//            Log.e("chars", "${it[0]}, ${it.getOrNull(1)}")
//        }
    }

    fun save() {
        viewModelScope.launch {

        }
    }
}