package com.wittyneko.aisingers.ui.edit

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.github.promeg.pinyinhelper.Pinyin
import com.wittyneko.aisingers.BaseFragment
import com.wittyneko.aisingers.R
import kotlinx.android.synthetic.main.fragment_edit.*
import kotlinx.android.synthetic.main.fragment_edit.view.*
import kotlinx.android.synthetic.main.item_edit.view.*
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import java.io.File


class EditFragment : BaseFragment() {

    override val kodein = Kodein.lazy {
        extend(parentKodein)
        import(editKodeinModule)
    }

    val editViewModule: EditViewModule by instance()
    val application: Application by instance()
    lateinit var chars: List<List<String>>

    val cacheFile by lazy {
        val dir = application.getExternalFilesDir("cache")
        File(dir, "${editViewModule.current.name}.txt")
    }

    val adapter by lazy {
        Adapter(
            requireContext(),
            readCache()
        )
    }

    var editPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit, container, false)
        view.list.adapter = adapter
//        view.list.addItemDecoration(
//            DividerItemDecoration(
//                view.context,
//                DividerItemDecoration.VERTICAL
//            )
//        )

        editViewModule.chars.observe(this) {
            chars = it.slice(2 until it.size)
            //val newList = adapter.list.joinToString("")
            //val origin = chars.joinToString(" ") { it[1] }
            //Log.e("origin", ": $origin")
            //tv_origin.text = origin
            edt_input.setText("")
        }

        view.la_edt_input.setEndIconOnClickListener {
            val text = edt_input.text.toString()
            if (editPosition != -1) {
                adapter.list[editPosition] = text
                editPosition = -1
                adapter.notifyDataSetChanged()
            } else {
                addLine(text)
            }
            edt_input.setText("")
        }
        view.edt_input.doAfterTextChanged {
            val length = (edt_input.text?.length ?: 0)
            val start =
                if (editPosition != -1) adapter.getStart(editPosition) else adapter.getLength()
            val end = start + length
            la_edt_input.hint = chars.slice(start until end).joinToString(" ") { it[1] }
            tv_origin.text = chars.slice(end until chars.size).joinToString(" ") { it[1] }
        }
        view.btn_save.setOnClickListener {
            val dir = application.getExternalFilesDir("out")!!
            val file = File(dir, editViewModule.current.name)

            //GlobalScope.launch(Dispatchers.IO) {  }
            cacheFile.outputStream().use { output ->
                adapter.list.forEach {
                    output.write("$it\n".toByteArray())
                }
            }

            file.outputStream().use { output ->
                val newList = adapter.list.joinToString("")

                editViewModule.chars.value?.forEachIndexed { index, list ->
                    val line: String
                    if (index < 2) {
                        line = list.joinToString(" ", postfix = "\n")
                    } else {
                        val temp = newList.getOrNull(index - 2)?.let { char ->
                            list.toMutableList().also { temp ->
                                temp[1] = char.toString()
                                temp[2] = Pinyin.toPinyin(char).toLowerCase()
                            }
                        } ?: list
                        line = temp.joinToString(" ", postfix = "\n")
                    }
                    output.write(line.toByteArray())
                }
            }

            Toast.makeText(requireActivity(), "已保存到${file.path}", Toast.LENGTH_LONG).show()
        }
        return view
    }

    fun addLine(msg: String) {

        adapter.list.add(msg)
        adapter.notifyDataSetChanged()
        list.smoothScrollToPosition(adapter.itemCount)
    }

    fun readCache() = run {
        if (cacheFile.exists()) {
            cacheFile.readLines().toMutableList()
        } else mutableListOf()
    }

    inner class Adapter(val context: Context, val list: MutableList<String>) :
        RecyclerView.Adapter<Adapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_edit, parent, false)
            //.inflate(android.R.layout.simple_list_item_2, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list.get(position)
            holder.text1.hint = getOrigin(position)
            holder.text2.setText(item)
        }

        fun getStart(position: Int) = list.let {
            var size = 0
            it.forEachIndexed { index, s ->
                if (index < position)
                    size += s.length
                else
                    return@let size
            }
            size
        }

        fun getLength() = run {
            var size = 0
            list.forEach { size += it.length }
            size
        }

        fun getOrigin(position: Int) = let {
            val start = getStart(position)
            chars.slice(start until start + list.get(position).length).joinToString(" ") { it[1] }
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val text1 by lazy { itemView.item_la_edit }
            val text2 by lazy { itemView.item_edit }

            init {
                val call = {
                    val position = adapterPosition
                    val item = list.get(position)
                    editPosition = position
                    edt_input.setText(item)
                    edt_input.setSelection(item.length)
                }

                text1.setEndIconOnClickListener {
                    adapter.list.removeAt(adapterPosition)
                    adapter.notifyDataSetChanged()
                    edt_input.setText("")
                }

                text2.setOnClickListener {
                    call()
                }
                itemView.setOnClickListener {
                    call()
                }
            }
        }
    }

}
