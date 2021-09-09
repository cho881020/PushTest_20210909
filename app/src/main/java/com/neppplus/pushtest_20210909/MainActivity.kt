package com.neppplus.pushtest_20210909

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.neppplus.colosseum_20210903.utils.ServerUtil
import com.neppplus.pushtest_20210909.adapters.UserAdapter
import com.neppplus.pushtest_20210909.datas.UserData
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : BaseActivity() {

    val mUserList = ArrayList<UserData>()

    lateinit var mAdapter : UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupEvents()
        setVales()
    }

    override fun setupEvents() {

    }

    override fun setVales() {

        getMainData()

        mAdapter = UserAdapter(mContext, R.layout.user_list_item, mUserList)
        userListView.adapter = mAdapter

    }

    fun getMainData() {

//        메인화면에서 쓰일 데이터 불러오기 X,
//        기존의 v2/main_info 에 쿼리파라미터 2개 추가.
        ServerUtil.getRequestMainData(mContext, null)


//        사용자 목록
        ServerUtil.getRequestUserList(mContext, object : ServerUtil.JsonResponseHandler{
            override fun onResponse(jsonObj: JSONObject) {

                val dataObj = jsonObj.getJSONObject("data")
                val userArr = dataObj.getJSONArray("users")

                for (i in 0 until userArr.length() ){

                    mUserList.add(UserData.getUserDataFromJson(userArr.getJSONObject(i)))

                }

                runOnUiThread{
                    mAdapter.notifyDataSetChanged()
                }
            }
        })

    }

}