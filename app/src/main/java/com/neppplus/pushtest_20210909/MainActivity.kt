package com.neppplus.pushtest_20210909

import android.os.Bundle
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId

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
        Log.d("디바이스토큰", FirebaseInstanceId.getInstance().token!!
        )
    }
}