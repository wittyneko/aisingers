package com.wittyneko.aisingers.ui.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.wittyneko.aisingers.BaseFragment
import com.wittyneko.aisingers.R
import com.wittyneko.aisingers.MainViewModule
import com.wittyneko.aisingers.ui.webview.DownloadFiles
import kotlinx.android.synthetic.main.fragment_list.view.*
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import java.io.File

class ListFragment : BaseFragment() {

    override val kodein = Kodein.lazy {
        extend(parentKodein)
    }

    val viewModule: MainViewModule by instance()
    val adapter by lazy { Adapter(requireContext(), arrayListOf()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        view.list.adapter = adapter
        view.list.addItemDecoration(
            DividerItemDecoration(
                view.context,
                DividerItemDecoration.VERTICAL
            )
        )
        viewModule.list.observe(this) {
            adapter.list.clear()
            it.mapTo(adapter.list) { it.name to it }
            adapter.notifyDataSetChanged()
        }
        view.btn_refresh.setOnClickListener {
            viewModule.getList()
            adapter.notifyDataSetChanged()
        }
        DownloadFiles.onFinish = {
            viewModule.getList()
            adapter.notifyDataSetChanged()
        }
        return view
    }

    inner class Adapter(val context: Context, val list: MutableList<Pair<String, File>>) :
        RecyclerView.Adapter<Adapter.ViewHolder>() {
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
                    viewModule.current = item.second
                    it.findNavController().navigate(R.id.editFragment)
                }
            }
        }
    }
}
