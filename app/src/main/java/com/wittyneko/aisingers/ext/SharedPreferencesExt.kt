package com.wittyneko.aisingers.ext

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.reflect.KProperty

/**
 * SharedPreferences 扩展
 * @author wittyneko
 * @since 2019/4/18
 */
private val GSON = Gson()
/** 3des 加解密的key */
private val SECRET_KEY = ""

abstract class PreferencesWrapper(preferences: SharedPreferences) : SharedPreferences by preferences

open class EditorWrapper(editor: SharedPreferences.Editor) : SharedPreferences.Editor by editor

open class CommonProperty<T>(val default: T, val prefs: SharedPreferences, val key: String? = null) {

    open operator fun getValue(thisRef: Any?, property: KProperty<*>): T = getValue(key
            ?: property.name, default)

    open operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = putValue(key
            ?: property.name, value)

    protected fun getValue(name: String, default: T): T = with(prefs) {
        val res: Any = when (default) {
            is Int -> getInt(name, default)
            is Long -> getLong(name, default)
            is Float -> getFloat(name, default)
            is Boolean -> getBoolean(name, default)
            is String -> getString(name, default)
            else -> throw IllegalArgumentException()
        }!!
        @Suppress("UNCHECKED_CAST")
        return res as T
    }

    protected fun putValue(name: String, value: T) = with(prefs.edit()) {
        when (value) {
            is Int -> putInt(name, value)
            is Long -> putLong(name, value)
            is Float -> putFloat(name, value)
            is Boolean -> putBoolean(name, value)
            is String -> putString(name, value)
            else -> throw IllegalArgumentException()
        }.commit()
    }
}

open class JsonProperty<T>(val typeToken: TypeToken<T>, val prefs: SharedPreferences, val block: ((T) -> Unit)? = null, val key: String? = null) {


    open operator fun getValue(thisRef: Any?, property: KProperty<*>): T = getValue(key
            ?: property.name)

    open operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = putValue(key
            ?: property.name, value)

    protected fun getValue(name: String): T = with(prefs) {
        //val default = if (List::class.java.isAssignableFrom(typeToken.rawType)) defArray else defObject
        val default = if (List::class.java.isAssignableFrom(typeToken.rawType)) "[]" else "{}"
        var json = getString(name, default)
        //json = decode(json)
        return GSON.fromJson(json, typeToken.type)
    }

    protected fun putValue(name: String, value: T) = with(prefs.edit()) {
        block?.also {
            it(value)
        }
        var json = GSON.toJson(value)
        //json = encode(json)
        putString(name, json)
        apply()
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T> SharedPreferences.property(default: T, key: String? = null) = CommonProperty(default, this, key)

@Suppress("NOTHING_TO_INLINE")
inline fun <reified T> SharedPreferences.jsonProperty(noinline block: ((T) -> Unit)? = null) = JsonProperty(object : TypeToken<T>() {}, this, block)

@Suppress("NOTHING_TO_INLINE")
inline fun <reified T> SharedPreferences.jsonProperty(key: String? = null, noinline block: ((T) -> Unit)? = null) = JsonProperty(object : TypeToken<T>() {}, this, block, key)

fun SharedPreferences.onChangeListener(owner: LifecycleOwner, listener: (key: String) -> Unit) {

    owner.lifecycle.addObserver(object : SharedPreferences.OnSharedPreferenceChangeListener, DefaultLifecycleObserver {
        init {
            registerOnSharedPreferenceChangeListener(this)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            unregisterOnSharedPreferenceChangeListener(this)
            owner.lifecycle.removeObserver(this)
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            listener(key)
        }
    })
}