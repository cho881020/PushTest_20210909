package com.neppplus.pushtest_20210909

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : BaseActivity() {

//    여기는 유병재가 작업하는 브랜치입니다.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupEvents()
        setVales()
    }

    override fun setupEvents() {

    }

    override fun setVales() {

        Log.d(FirebaseInstanceId.getInstance().token!!)

    }
}