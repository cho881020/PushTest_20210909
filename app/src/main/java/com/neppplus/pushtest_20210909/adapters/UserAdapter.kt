package com.neppplus.pushtest_20210909.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.neppplus.colosseum_20210903.R
import com.neppplus.colosseum_20210903.ViewTopicDetailActivity
import com.neppplus.colosseum_20210903.datas.NotiData
import com.neppplus.colosseum_20210903.datas.ReplyData
import com.neppplus.colosseum_20210903.datas.TopicData
import com.neppplus.colosseum_20210903.utils.ServerUtil
import com.neppplus.pushtest_20210909.datas.UserData
import org.json.JSONObject
import java.text.SimpleDateFormat

class UserAdapter(
    val mContext:Context,
    resId: Int,
    val mList: List<UserData>) : ArrayAdapter<UserData>(mContext, resId, mList) {

    val mInflater = LayoutInflater.from(mContext)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row = convertView
        if (row == null) {
            row = mInflater.inflate(R.layout.user_Lis, null)
        }
        row!!

        val data = mList[position]




        return row
    }

}











