package com.wittyneko.aisingers.di

import android.content.Context
import android.content.SharedPreferences
import com.wittyneko.aisingers.App
import com.wittyneko.aisingers.repository.ConfigRepository
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

private const val GLOBAL_REPOS_MODULE_TAG = "PrefsModule"

private const val DEFAULT_SP_TAG = "PrefsDefault"

val globalRepositoryModule = Kodein.Module(GLOBAL_REPOS_MODULE_TAG) {

    bind<SharedPreferences>(DEFAULT_SP_TAG) with singleton {
        App.INSTANCE.getSharedPreferences(DEFAULT_SP_TAG, Context.MODE_PRIVATE)
    }

    bind<ConfigRepository>() with singleton {
        ConfigRepository.getInstance(instance(DEFAULT_SP_TAG))
    }
}