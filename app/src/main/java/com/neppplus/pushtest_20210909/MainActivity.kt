package com.neppplus.pushtest_20210909

import android.os.Bundle
import android.util.Log
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

        loginBtn.setOnClickListener {
            // id/pw
            val inputId = emailEdt.text.toString()
            val inputPw = passwordEdt.text.toString()

            // ServerUtil클래스 활용 -> 로그인 시도

        }
    }

    override fun setVales() {
        Log.d("디바이스토큰", FirebaseInstanceId.getInstance().token!!
        )
    }
}