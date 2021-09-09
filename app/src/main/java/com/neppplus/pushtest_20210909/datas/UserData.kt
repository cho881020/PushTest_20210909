package com.neppplus.pushtest_20210909.datas

import org.json.JSONObject
import java.io.Serializable

class UserData(
    var id: Int,
    var email: String,
    var nickname: String) : Serializable {

    constructor() : this(0, "", "")


    companion object{

//        json 넣으면 -> UserData 형태로 돌려주는 함수
//        ReplyData의 writer에 활용


        fun getUserDataFromJson( json : JSONObject) : UserData {//UserData로 돌려받겠다, 저기로 넣는건가봄

//            결과로 사용될 TopicData 객체 하나 생성
            val userData = UserData()//이거 오류나면 constructor() : this() 맨 위 괄호에 생성

//            json 내부에서 값을 파싱 -> TopicData의 변수들에 대입
            userData.id = json.getInt("id")
            userData.email = json.getString("email")
            userData.nickname = json.getString("nick_name")

//            최종 결과 선정
            return userData
        }

    }

}