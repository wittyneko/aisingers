package com.wittyneko.aisingers.repository

import android.content.SharedPreferences
import com.qingmei2.rhine.util.SingletonHolderSingleArg
import com.wittyneko.aisingers.ext.boolean

class ConfigRepository(prefs: SharedPreferences) {
    var hasInitModel by prefs.boolean("model", true)

    companion object: SingletonHolderSingleArg<ConfigRepository, SharedPreferences>(::ConfigRepository)
}