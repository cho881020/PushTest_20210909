package com.neppplus.pushtest_20210909

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun setupEvents() {
    }

    override fun setVales() {

        val myHandler = Handler(Looper.getMainLooper())
        myHandler.postDelayed({

            val myIntent = Intent(mContext, MainActivity::class.java)
            startActivity(myIntent)

        })
    }
}