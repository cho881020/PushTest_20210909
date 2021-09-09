package com.neppplus.pushtest_20210909

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

//    여기는 김현우 브랜치입니다.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupEvents()
        setVales()
    }

    override fun setupEvents() {

        login_Btn.setOnClickListener {

            val inputId = email_Txt.text.toString()
            val inputPassword = password_Edt.text.toString()
        }
    }

    override fun setVales() {

        Log.d("디바이스 토큰", FirebaseInstanceId.getInstance().token!!)
    }
}