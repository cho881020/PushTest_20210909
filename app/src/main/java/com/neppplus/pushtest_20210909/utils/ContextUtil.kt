package com.example.colosseum_20210903.utils

import android.content.Context

class ContextUtil {

    companion object {

        private val prefName = "ColosseumPref"

        private val AUTO_LOGIN = "AUTO_LOGIN"
        private val TOKEN = "TOKEN"

        // 자동 저장 여부를 설정하는 함수
        fun setAutoLogin(context: Context, isAutoLogin: Boolean) {
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            pref.edit().putBoolean(AUTO_LOGIN, isAutoLogin).apply()
        }

        // 자동 저장 여부를 읽어오는 함수
        fun getAutoLogin(context: Context): Boolean {
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            //두번째 파라미터 -> SharedPreferences 가 없는경우 반환할 값
            return pref.getBoolean(AUTO_LOGIN, false)
        }

        // 토큰 저장 함수
        fun setToken(context: Context, token: String) {
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            pref.edit().putString(TOKEN, token).apply()
        }

        // 저장된 토큰 읽어오는 함수
        fun getToken(context: Context): String {
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            return pref.getString(TOKEN, "")!!
        }
    }
}