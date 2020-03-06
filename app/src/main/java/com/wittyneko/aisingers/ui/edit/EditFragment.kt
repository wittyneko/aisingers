package com.wittyneko.aisingers.ui.edit

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.wittyneko.aisingers.BaseFragment
import com.wittyneko.aisingers.R
import kotlinx.android.synthetic.main.fragment_edit.*
import kotlinx.android.synthetic.main.fragment_edit.view.*
import org.kodein.di.Kodein
import org.kodein.di.generic.instance


class EditFragment : BaseFragment() {

    override val kodein = Kodein.lazy {
        extend(parentKodein)
        import(editKodeinModule)
    }

    val editViewModule: EditViewModule by instance()
    lateinit var chars: List<List<String>>

    val adapter by lazy {
        Adapter(
            requireContext(),
            arrayListOf()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit, container, false)
        view.list.adapter = adapter
        view.list.addItemDecoration(
            DividerItemDecoration(
                view.context,
                DividerItemDecoration.VERTICAL
            )
        )

        editViewModule.chars.observe(this) {
            chars = it.slice(2 until it.size)
            val origin = chars.joinToString(" ") { it[1] }
            Log.e("origin", ": $origin")
            tv_origin.text = origin
        }

        view.la_edt_input.setEndIconOnClickListener {
            val text = edt_input.text.toString()
            addLine(text)
            edt_input.setText("")
        }
        view.edt_input.doAfterTextChanged {
            la_edt_input.hint =
                chars.slice(0 until (edt_input.text?.length ?: 0)).joinToString(" ") { it[1] }
            tv_origin.text = chars.slice((edt_input.text?.length ?: 0) until chars.size)
                .joinToString(" ") { it[1] }
        }
        return view
    }

    fun addLine(msg: String) {

        adapter.list.add(msg)
        adapter.notifyDataSetChanged()
    }

    class Adapter(val context: Context, val list: MutableList<String>) :
        RecyclerView.Adapter<Adapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_2, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list.get(position)
            holder.text1.text = item
            holder.text2.text = getOrigin(position)
        }

        fun getOrigin(position: Int) = list.let {
            it.forEachIndexed { index, pair ->

            }
            ""
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val text1 by lazy { itemView.findViewById<TextView>(android.R.id.text1) }
            val text2 by lazy { itemView.findViewById<TextView>(android.R.id.text2) }

            init {
                itemView.setOnClickListener {
                    val position = adapterPosition
                    val item = list.get(position)
                }
            }
        }
    }

}
