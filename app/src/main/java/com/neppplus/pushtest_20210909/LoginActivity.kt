package com.neppplus.pushtest_20210909

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import com.neppplus.colosseum_20210903.utils.ContextUtil
import com.neppplus.colosseum_20210903.utils.ServerUtil
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : BaseActivity() {

//    여기는 안수지가 작업하는 브랜치입니다.

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

            ServerUtil.postRequestSignIn(inputId, inputPw, object : ServerUtil.JsonResponseHandler {
                override fun onResponse(jsonObj: JSONObject) {

                    val code = jsonObj.getInt("code")
                    if (code == 200) {
                        val dataObj = jsonObj.getJSONObject("data")
                        val token = dataObj.getString("token")
                        Log.d("토큰값", token)

                        ContextUtil.setToken(mContext, token)

                        val myIntent = Intent(mContext, MainActivity::class.java)
                        startActivity(myIntent)

                    }
                    else {
                        runOnUiThread {
                            Toast.makeText(mContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                        }
                    }

                }

            })


//            성공시 다른 화면 진입. => 회원 명부 화면.

        }

    }

    override fun setVales() {

        Log.d("디바이스토큰", FirebaseInstanceId.getInstance().token!!)


    }
}