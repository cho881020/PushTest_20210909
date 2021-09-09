package com.neppplus.pushtest_20210909

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.neppplus.colosseum_20210903.utils.ContextUtil
import com.neppplus.colosseum_20210903.utils.ServerUtil
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class LoginActivity : BaseActivity() {

//    여기는 김현지가 작업하는 브랜치 입니다

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupEvents()
        setVales()
    }

    override fun setupEvents() {

        loginBtn.setOnClickListener {

            val inputId = emailEdt.text.toString()
            val inputPw = passwordEdt.text.toString()


            ServerUtil.postRequestSignIn(inputId, inputPw, object  : ServerUtil.JsonResponseHandler {

                override fun onResponse(jsonObj: JSONObject) {

                    val code = jsonObj.getInt("code")
                    if(code == 200) {
                        val dataobj = jsonObj.getJSONObject("data")
                        val token = dataobj.getString("token")
                        Log.d("토큰값",token)




                        ContextUtil.setToken(mContext, MainActivity::class.java)
                        startActivity(myIntent)


                    }
                }

            })

        }

    }

    override fun setVales() {

        Log.d(FirebaseInstanceId.getInstance().token!!)



    }
}