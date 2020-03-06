package com.wittyneko.aisingers

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.android.retainedKodein
import org.kodein.di.generic.kcontext

class MainActivity : AppCompatActivity(), KodeinAware {

    val navHostFragment by lazy { findNavController(R.id.nav_host_fragment) }

    protected val parentKodein by closestKodein()

    override val kodeinContext = kcontext(this)

    override val kodein: Kodein by retainedKodein {
        extend(parentKodein, copy = Copy.All)
        import(mainKodeinModule)
    }

    override fun onSupportNavigateUp(): Boolean = navHostFragment.navigateUp()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

