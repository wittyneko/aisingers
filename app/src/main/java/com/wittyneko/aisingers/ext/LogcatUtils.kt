package com.wittyneko.aisingers.ext

import android.util.Log
import com.wittyneko.aisingers.BuildConfig.DEBUG

/**
 * Log封装
 * Created by wittyneko on 2017/10/30.
 */

enum class LogLevel {
    Verbose, Debug, Info, Warn, Error, WTF, Assert
}

var TAG = "AppLog"
var space = "->"
var isDebug = DEBUG
private val defLevel = LogLevel.Error
private val MAXLENGTH = 2000

private val trace get() = trace(3)

@Suppress("NOTHING_TO_INLINE")
inline private fun trace(index: Int): String {
    val caller = Throwable().run {
        //处理 Java @JvmOverloads 调用行号错误
        if (stackTrace[index].fileName != null) stackTrace[index] else stackTrace[index + 1]
    }

    // 1. 类名 + 方法名
    //var callerClazzName = caller.className
    //callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
    //val template = "${callerClazzName}.${caller.methodName}(${caller.fileName}:${caller.lineNumber})"
    //Log.e("throwable", "throwable", Throwable())

    // 2. 方法名
    val template = "${caller.methodName}(${caller.fileName}:${caller.lineNumber})"
    return template
}

@JvmOverloads
fun logcat(msg: String, level: LogLevel = defLevel, tag: String = TAG) = run {
    if (isDebug) {

        val length = msg.length
        var index = 0
        var start = 0
        var end = if (length < MAXLENGTH) length else MAXLENGTH

        do {
            val p = if (index == 0) "" else "$index"

            when (level) {
                LogLevel.Verbose -> {
                    Log.v(tag, "$trace $p$space ${msg.subSequence(start, end)}")
                }
                LogLevel.Debug -> {
                    Log.d(tag, "$trace $p$space ${msg.subSequence(start, end)}")
                }
                LogLevel.Info -> {
                    Log.i(tag, "$trace $p$space ${msg.subSequence(start, end)}")
                }
                LogLevel.Warn -> {
                    Log.w(tag, "$trace $p$space ${msg.subSequence(start, end)}")
                }
                LogLevel.Error -> {
                    Log.e(tag, "$trace $p$space ${msg.subSequence(start, end)}")
                }
                LogLevel.WTF -> {
                    Log.wtf(tag, "$trace $p$space ${msg.subSequence(start, end)}")
                }
            }

            index += 1
            start = end
            end += MAXLENGTH
            if (end > length){
                end = length
            }
        } while (start < end)
    }
}

@JvmOverloads
fun logcat(msg: String, tr: Throwable?, level: LogLevel = defLevel, tag: String = TAG) = run {
    if (isDebug) {

        val length = msg.length
        var index = 0
        var start = 0
        var end = if (length < MAXLENGTH) length else MAXLENGTH

        do {
            val p = if (index == 0) "" else "$index"

            when (level) {
                LogLevel.Verbose -> {
                    Log.v(tag, "$trace $p$space ${msg.subSequence(start, end)}", tr)
                }
                LogLevel.Debug -> {
                    Log.d(tag, "$trace $p$space ${msg.subSequence(start, end)}", tr)
                }
                LogLevel.Info -> {
                    Log.i(tag, "$trace $p$space ${msg.subSequence(start, end)}", tr)
                }
                LogLevel.Warn -> {
                    Log.w(tag, "$trace $p$space ${msg.subSequence(start, end)}", tr)
                }
                LogLevel.Error -> {
                    Log.e(tag, "$trace $p$space ${msg.subSequence(start, end)}", tr)
                }
                LogLevel.WTF -> {
                    Log.wtf(tag, "$trace $p$space ${msg.subSequence(start, end)}", tr)
                }
            }

            start = end
            end += MAXLENGTH
            index += 1
            if (end > length){
                end = length
            }
        } while (start < end)
    }
}