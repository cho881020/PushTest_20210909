package com.neppplus.pushtest_20210909

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.colossuem_0903.utils.ContextUtil

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        setVales()
        setupEvents()
    }

    override fun setupEvents() {

    }

    override fun setVales() {

        val myHandler = Handler(Looper.getMainLooper())
        myHandler.postDelayed({
            val myIntent: Intent

            if (ContextUtil.getAutoLogIn(mContext) && ContextUtil.getToken(mContext) != "") {

                myIntent = Intent(mContext, MainActivity::class.java)
            } else {

                myIntent = Intent(mContext, LogInActivity::class.java)
            }

            startActivity(myIntent)
            finish()
        }, 2000)
    }
}