package com.neppplus.pushtest_20210909

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.neppplus.colosseum_20210903.utils.ServerUtil
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : BaseActivity() {

    val mUserList = ArrayList<>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupEvents()
        setVales()
    }

    override fun setupEvents() {

        userListView.setOnItemClickListener { adapterView, view, position, l ->

            val ClickedUser = mUserList[position]
            ServerUtil.postRequestForkUser(mContext, clickedUser.id, object : ServerUtil.JsonResponseHandler {
                val code = jsonObj.getInt("code")

                runOnUiThread {
                    if ( code == 200) {
                        ㅅ
                    }
                }

            })
        }

    }

    override fun setVales() {

        getMainData()
    }

    fun getMainData() {

//        메인화면에서 쓰일 데이터 불러오기 X,
//        기존의 v2/main_info 에 쿼리파라미터 2개 추가.
        ServerUtil.getRequestMainData(mContext, null)

        ServerUtil.getRequestUserList(mContext, object  : ServerUtil.JsonResponseHandler {
            override fun onResponse(jsonObj: JSONObject) {

                val dataObj = jsonObj.getJSONObject("data")
                val usersArr = dataObj.getJSONArray("users")

                for ( i in 0 until usersArr.length()) {
                    mUserList.add( u)
                }

                runOnUiThread {  }

            }


        })

    }

}