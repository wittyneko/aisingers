package com.wittyneko.aisingers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.findNavController
import com.wittyneko.aisingers.ext.logcat
import com.wittyneko.aisingers.repository.ConfigRepository
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.android.retainedKodein
import org.kodein.di.generic.instance
import org.kodein.di.generic.kcontext
import java.io.File

class MainActivity : AppCompatActivity(), KodeinAware {

    val navHostFragment by lazy { findNavController(R.id.nav_host_fragment) }

    protected val parentKodein by closestKodein()

    override val kodeinContext = kcontext(this)

    override val kodein: Kodein by retainedKodein {
        extend(parentKodein, copy = Copy.All)
        import(mainKodeinModule)
    }

    val configRepository: ConfigRepository by instance()

    override fun onSupportNavigateUp(): Boolean = navHostFragment.navigateUp()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (configRepository.hasInitModel) {
            copyFile("model")
            copyFile("cache")
            copyFile("out")
            configRepository.hasInitModel = false
        }
    }

    fun copyFile(path: String){
        assets.list(path)?.forEach {
            logcat(it)
            val file = File(getExternalFilesDir("$path"), it)
            file.outputStream().use { output ->
                assets.open("$path/$it").use { input ->
                    var len = 0
                    val buffer = ByteArray(1024 * 4)
                    while (input.read(buffer).also { len = it } > 0) {
                        output.write(buffer, 0, len)
                    }
                    output.flush()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about -> {
                navHostFragment.navigate(R.id.aboutFragment)
            }
            R.id.home ->{
                navHostFragment.navigate(R.id.webViewFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

