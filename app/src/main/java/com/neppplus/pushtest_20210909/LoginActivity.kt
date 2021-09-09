package com.neppplus.pushtest_20210909

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.nepplus.colosseum_0903.utils.ContextUtil
import com.nepplus.colosseum_0903.utils.ServerUtil
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : BaseActivity() {

//    여기는 송병섭(sbs0516)이 작업하는 브랜치입니다.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupEvents()
        setVales()
    }

    override fun setupEvents() {

        loginBtn.setOnClickListener {
            val inputId = idEdt.text.toString()
            val inputPw = pwEdt.text.toString()

            ServerUtil.postRequestSignIn(inputId, inputPw, object : ServerUtil.JsonResponseHandler {
                override fun onResponse(jsonObj: JSONObject) {
                    val code = jsonObj.getInt("code")
                    if(code == 200) {
                        val dataObj = jsonObj.getJSONObject("data")
                        val token = dataObj.getString("token")

                        // 로그인 성공 시 토큰 값 저장
                        ContextUtil.setToken(mContext, token)

                        val myIntent= Intent(mContext, MainActivity::class.java)
                        startActivity(myIntent)
                    } else {
                        runOnUiThread {
                            Toast.makeText(mContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            } )
        }

        // ServerUtil 클래스 활용 -> 로그인 시도



    }

    override fun setVales() {

    }
}