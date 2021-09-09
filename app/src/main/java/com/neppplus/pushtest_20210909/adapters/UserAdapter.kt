package com.neppplus.pushtest_20210909.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.neppplus.pushtest_20210909.R
import com.neppplus.pushtest_20210909.datas.UserData

//import 정리 ctrl + alt + o

class UserAdapter(val mContext: Context, resId: Int, val mList: List<UserData>) :
    ArrayAdapter<UserData>(mContext, resId, mList) {

    val mInflater = LayoutInflater.from(mContext)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row = convertView
        if (row == null) {
            row = mInflater.inflate(R.layout.user_list_item, null)
        }

        row!!

        val data = mList[position]
        val nicknameTxt = row.findViewById<TextView>(R.id.nicknameTxt)

        nicknameTxt.text = data.nickname

        return row
    }
}