package com.neppplus.pushtest_20210909.datas

import org.json.JSONObject
import java.io.Serializable

class UserData(
    var id: Int,
    var email: String,
    var nickname: String
) : Serializable {

    constructor() : this(0, "", "")

    companion object {
        // json -> UserData 형태로 돌려주는 함수
        fun getUserDataFromJson(json: JSONObject): UserData {
            val userData = UserData()

            userData.id = json.getInt("id")
            userData.email = json.getString("email")
            userData.nickname = json.getString("nick_name")

            return userData
        }
    }
}