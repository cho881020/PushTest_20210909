package com.neppplus.pushtest_20210909

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setupEvents()
        setVales()
    }

    override fun setupEvents() {

    }

    override fun setVales() {

        val myHandler = Handler(Looper.getMainLooper())
        myHandler.postDelayed( {

           val myIntent = Intent(mContext, LoginActivity::class.java)
            startActivity(myIntent)
            finish()

        } , 2500)

    }
}