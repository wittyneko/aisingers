package com.wittyneko.aisingers.ui.edit

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.wittyneko.aisingers.MainViewModule
import com.wittyneko.aisingers.ViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.android.x.AndroidLifecycleScope
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.scoped
import org.kodein.di.generic.singleton

const val MAIN_MOUDLUE_TAG = "EDIT_MOUDLE_TAG"

val editKodeinModule = Kodein.Module(MAIN_MOUDLUE_TAG) {

    bind() from scoped<Fragment>(AndroidLifecycleScope).singleton {
        ViewModelProviders.of(context,
            ViewModelFactory {
                EditViewModule(instance(), instance())
            }).get<EditViewModule>()
    }
}