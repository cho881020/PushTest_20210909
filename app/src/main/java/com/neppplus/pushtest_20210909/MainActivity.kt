package com.neppplus.pushtest_20210909

import android.os.Bundle
import com.example.colossuem_0903.utils.ServerUtil
import com.neppplus.pushtest_20210909.adapters.UserAdapter

class MainActivity : BaseActivity() {

    lateinit var mAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setVales()
        setupEvents()
    }

    override fun setupEvents() {

    }

    override fun setVales() {

        getMainData()
    }

    fun getMainData() {

//       메인화면에서 쓰일 데이터 불러오기 X,
//        기존의 v2/main_info 에 쿼리파라미터 2개 추가.
        ServerUtil.getRequestMainData(mContext, null)
    }
}