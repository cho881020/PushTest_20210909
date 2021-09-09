package com.neppplus.pushtest_20210909

import android.os.Bundle
import android.widget.Toast
import com.example.colossuem_0903.utils.ServerUtil
import com.neppplus.pushtest_20210909.adapters.UserAdapter
import com.neppplus.pushtest_20210909.datas.UserData
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : BaseActivity() {

    lateinit var mAdapter: UserAdapter
    val mUserList = ArrayList<UserData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setVales()
        setupEvents()
    }

    override fun setupEvents() {

        userListView.setOnItemClickListener { adapterView, view, position, l ->

            val clickedUser = mUserList[position]

            ServerUtil.postRequestForkUser(
                mContext,
                clickedUser.id,
                object : ServerUtil.JsonResponseHandler {
                    override fun onResponse(jsonObj: JSONObject) {

                        val code = jsonObj.getInt("code")

                        runOnUiThread {
                            if (code == 200) {

//                        푸시알림 발송 성공
                                Toast.makeText(mContext, "푸시알림 전송 성공", Toast.LENGTH_SHORT).show()
                            } else {

//                        디바이스 토큰 없는사람.
                                Toast.makeText(
                                    mContext,
                                    "상대방이 푸시알림을 받을 수 없습니다.",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }


                    }
                })
        }
    }

    override fun setVales() {

        getMainData()

        mAdapter = UserAdapter(mContext, R.layout.user_list_item, mUserList)
        userListView.adapter = mAdapter
    }

    fun getMainData() {

//       메인화면에서 쓰일 데이터 불러오기 X,
//        기존의 v2/main_info 에 쿼리파라미터 2개 추가.
        ServerUtil.getRequestMainData(mContext, null)

//        사용자 목록 받아서 파싱
        ServerUtil.getRequestUserList(mContext, object : ServerUtil.JsonResponseHandler {
            override fun onResponse(jsonObj: JSONObject) {

                val dataObj = jsonObj.getJSONObject("data")
                val userArr = dataObj.getJSONArray("users")

                for (i in 0 until userArr.length()) {

                    mUserList.add(UserData.getUserDataFromJson(userArr.getJSONObject(i)))
                }

                runOnUiThread {
                    mAdapter.notifyDataSetChanged()
                }
            }
        })
    }
}