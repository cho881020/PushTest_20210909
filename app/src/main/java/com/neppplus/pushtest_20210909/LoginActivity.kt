package com.neppplus.pushtest_20210909

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.colosseum_20210903.utils.ContextUtil
import com.example.colosseum_20210903.utils.ServerUtil
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupEvents()
        setVales()
    }

    override fun setupEvents() {
        loginBtn.setOnClickListener {
            // id/pw
            val inputId = emailEdt.text.toString()
            val inputPw = passwordEdt.text.toString()

            // ServerUtil클래스 활용 -> 로그인 시도
            ServerUtil.postRequestSignIn(inputId, inputPw, object : ServerUtil.JsonResponseHandler{
                override fun onResponse(jsonObj: JSONObject) {
                    val code = jsonObj.getInt("code")
                    if(code == 200) {
                        val dataObj = jsonObj.getJSONObject("data")
                        val token = dataObj.getString("token")

                        Log.d("유저토큰", token)

                        ContextUtil.setToken(mContext, token)
                        startActivity(Intent(mContext, MainActivity::class.java))

                    }else{
                        runOnUiThread {
                            Toast.makeText(mContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }
    }

    override fun setVales() {
    }
}