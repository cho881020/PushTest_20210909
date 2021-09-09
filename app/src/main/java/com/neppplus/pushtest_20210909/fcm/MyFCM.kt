package com.neppplus.pushtest_20210909.fcm

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFCM : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        val myHandler = Handler(Looper.getMainLooper())

        myHandler.post {

            Toast.makeText(applicationContext, p0.notification!!.body, Toast.LENGTH_SHORT).show()

        }

    }

}