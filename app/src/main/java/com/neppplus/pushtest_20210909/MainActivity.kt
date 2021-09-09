package com.neppplus.pushtest_20210909

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.neppplus.colosseum_20210903.utils.ServerUtil

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
    }

    override fun setupEvents() {
        TODO("Not yet implemented")
    }

    override fun setVales() {
        getMainData()

    }

    fun getMainData() {


        ServerUtil.getRequestMainData(mContext, null)


    }
}