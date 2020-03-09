package com.wittyneko.aisingers.ui.webview

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wittyneko.aisingers.BaseFragment
import com.wittyneko.aisingers.R
import com.wittyneko.aisingers.ext.logcat
import kotlinx.android.synthetic.main.fragment_webview.view.*
import org.kodein.di.Kodein
import java.io.File

class WebViewFragment : BaseFragment() {

    override val kodein = Kodein.lazy {
        extend(parentKodein)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_webview, container, false)
        val webview = view.webview
        val url = "http://aisingers.com"
        //syncCookie(requireContext(), url)
        webview.loadUrl(url)
        val settings = view.webview.settings
        settings.apply {
            @Suppress("ImplicitThis")
            javaScriptEnabled = true
            allowFileAccess = true
            domStorageEnabled = true
            blockNetworkImage = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
        }

        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                webview.loadUrl(url)
                return true
            }

        }
        webview.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                class Adapter(val list: MutableList<Pair<String, File>>) :
                    RecyclerView.Adapter<Adapter.ViewHolder>() {

                    var onClick: (() -> Unit)? = null

                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                        val view = LayoutInflater.from(parent.context)
                            .inflate(android.R.layout.simple_list_item_2, parent, false)
                        return ViewHolder(view)
                    }

                    override fun getItemCount() = list.size

                    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                        val item = list.get(position)
                        holder.text1.text = item.first
                        holder.text2.text = item.second.path
                    }

                    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                        val text1 by lazy { itemView.findViewById<TextView>(android.R.id.text1) }
                        val text2 by lazy { itemView.findViewById<TextView>(android.R.id.text2) }

                        init {
                            itemView.setOnClickListener {
                                val position = adapterPosition
                                val item = list.get(position)
                                val value = arrayOf(Uri.parse("file://${item.second.path}"))
                                filePathCallback.onReceiveValue(value)
                                onClick?.invoke()
                            }
                        }
                    }
                }

                val adapter = Adapter(mutableListOf())

                val recyclerView = RecyclerView(requireContext()).apply {
                    layoutManager = LinearLayoutManager(context)
                    addItemDecoration(
                        DividerItemDecoration(
                            view.context,
                            DividerItemDecoration.VERTICAL
                        )
                    )
                    this.adapter = adapter
                }
                requireContext().getExternalFilesDir("out")?.listFiles { it ->
                    it.isFile && it.name.endsWith(".nn")
                }?.mapTo(adapter.list) { it.name to it }
                val dailog = AlertDialog.Builder(requireContext())
                    .setView(recyclerView)
                    .setNegativeButton("取消") { dialog, which ->
                    }
                    .setOnCancelListener {
                        filePathCallback.onReceiveValue(null)
                    }
                    .create()
                adapter.onClick = { dailog.dismiss() }
                dailog.show()
                return true
            }


//            @Suppress("UseExpressionBody")
//            override fun openFileChooser(
//                uploadFile: ValueCallback<Uri?>,
//                acceptType: String?,
//                capture: String?
//            ) {
//                uploadFile.onReceiveValue(null)
//            }

        }
        webview.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            logcat("download: $url, $userAgent, $contentDisposition, $mimetype, $contentLength")
            DownloadFiles.download(url)
        }
        return view
    }

    /**
     * Sync Cookie
     */
    private fun syncCookie(context: Context, url: String) {
        try {
            Log.d("Nat:  syncCookie url", url)
            CookieSyncManager.createInstance(context)
            val cookieManager: CookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.removeSessionCookie() // 移除
            cookieManager.removeAllCookie()
            val oldCookie = cookieManager.getCookie(url)
            if (oldCookie != null) {
                Log.d("Nat: oldCookie", oldCookie)
            }
            val sbCookie = StringBuilder()
            sbCookie.append(String.format("JSESSIONID=%s", "INPUT YOUR JSESSIONID STRING"))
            sbCookie.append(String.format(";domain=%s", "INPUT YOUR DOMAIN STRING"))
            sbCookie.append(String.format(";path=%s", "INPUT YOUR PATH STRING"))
            val cookieValue = sbCookie.toString()
            cookieManager.setCookie(url, cookieValue)
            CookieSyncManager.getInstance().sync()
            val newCookie = cookieManager.getCookie(url)
            if (newCookie != null) {
                Log.d("Nat: newCookie", newCookie)
            }
        } catch (e: Exception) {
            Log.e("Nat:  failed", e.toString())
        }
    }
}
