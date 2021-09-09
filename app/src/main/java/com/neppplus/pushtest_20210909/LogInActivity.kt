package com.neppplus.pushtest_20210909

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.colossuem_0903.utils.ContextUtil
import com.example.colossuem_0903.utils.ServerUtil
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LogInActivity : BaseActivity() {

//    여기는 김현우 브랜치입니다.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupEvents()
        setVales()
    }

    override fun setupEvents() {

        login_Btn.setOnClickListener {

            val inputId = email_Txt.text.toString()
            val inputPassword = password_Edt.text.toString()

            ServerUtil.postRequestSignIn(
                inputId,
                inputPassword,
                object : ServerUtil.JsonResponseHandler {
                    override fun onResponse(jsonObj: JSONObject) {

                        val code = jsonObj.getInt("code")

                        if (code == 200) {
                            val dataObj = jsonObj.getJSONObject("data")
                            val token = dataObj.getString("token")

                            Log.d("토큰값", token.toString())

                            ContextUtil.setToken(mContext, token)

                            val myIntent = Intent(mContext, MainActivity::class.java)
                            startActivity(myIntent)
                            finish()
                        } else {
                            runOnUiThread {
                                Toast.makeText(mContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                })
        }
    }

    override fun setVales() {

        Log.d("디바이스 토큰", FirebaseInstanceId.getInstance().token!!)
    }
}