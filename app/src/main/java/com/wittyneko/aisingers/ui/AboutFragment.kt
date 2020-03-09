package com.wittyneko.aisingers.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wittyneko.aisingers.BaseFragment
import com.wittyneko.aisingers.R
import kotlinx.android.synthetic.main.fragment_about.view.*
import org.kodein.di.Kodein

val about = """ 
使用教程
1. http://aisingers.com/ai/nnlist/ 下载模版
2. 将.nn模版文件放到存储卡 
Android/data/com.wittyneko.aisingers/files/model
3. 选择对应模版修改歌词
4. 歌词修改完文件保存到 
Android/data/com.wittyneko.aisingers/files/out

更新内容

0.0.2
1. 内置一个模版歌曲
2. 歌词编辑列表可删除
3. 修复歌词编辑bug

0.0.1
1. 基础歌词编辑功能
2. 编辑歌词缓存


目前仅支持中文，后续可能会加拼音
后续版本应该会内嵌浏览器，方便打开选择上传合成，不定期更新大概获取明年吧

问题反馈群：141921984
    """.trimIndent()
class AboutFragment : BaseFragment() {

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
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        view.tv_about.text = about
        return view
    }

}
