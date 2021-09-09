package com.neppplus.colosseum_20210903.utils

import android.content.Context

class ContextUtil {

    companion object {//여따 만들면 바로 활용 가능, 뭔소린지 모르니 검색ㄱ

        private val prefName = "ColosseumPref"//파일 이름(메모장)

        private val AUTO_LOGIN = "AUTO_LOGIN"
        private val TOKEN = "TOKEN"

        fun setAutoLogin(context: Context, isAutoLogin: Boolean) {//자동로그인을 set하는 과정
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            pref.edit().putBoolean(AUTO_LOGIN,  isAutoLogin ).apply()//apply==save임
        }

        fun getAutoLogin(context: Context) : Boolean {//얘가 자동로그인이 맞는지 검사,getter
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            return pref.getBoolean(AUTO_LOGIN, false)//false는 기본값, 앞에꺼가 널일 때
        }

        fun setToken(context: Context, token : String) {//메모장을 열어줘야하나context, token을 string으로 set
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            pref.edit().putString(TOKEN,  token).apply()
        }

        fun getToken(context: Context) : String {//메모장을 열어줘야하나context, token을 string으로 set
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            return pref.getString(TOKEN, "")!!//스트링으로 내놔, TOKEN항목에 저장된걸, : String해놔서 널은 안되니 ""로 하자
        }


    }

}