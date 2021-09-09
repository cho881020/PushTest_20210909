package com.neppplus.pushtest_20210909

import android.os.Bundle
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

//    여기는 조경진이 작업하는 브랜치입니다.
    
//    푸시알림 설정 파일도 추가

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupEvents()
        setVales()
    }

    override fun setupEvents() {

        loginBtn.setOnClickListener {

//            id / pw
            val inputId = emailEdt.text.toString()
            val inputPw = passwordEdt.text.toString()

//            ServerUtil클래스 활용 -> 로그인 시도.



//            성공시 다른 화면 진입. => 회원 명부 화면.

        }

    }

    override fun setVales() {

        Log.d("디바이스토큰",  FirebaseInstanceId.getInstance().token!!)

    }
}