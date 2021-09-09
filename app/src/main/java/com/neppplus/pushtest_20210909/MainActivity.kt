package com.neppplus.pushtest_20210909

import android.os.Bundle
import android.util.Log
import com.example.colosseum_20210903.utils.ServerUtil
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

//    여기는 장혜린 작업 브랜치입니다.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupEvents()
        setVales()
    }

    override fun setupEvents() {
    }

    override fun setVales() {
        getMainData()
    }

    fun getMainData(){
        // 메인화면에서 쓰일 데이터 불러오기 X,
        // 기존의 v2/main_info 에 쿼리파라미터 2개 추가
        ServerUtil.getRequestMainData(mContext, null)
    }
}