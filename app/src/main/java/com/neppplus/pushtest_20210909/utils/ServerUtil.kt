package com.example.colosseum_20210903.utils

import android.content.Context
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject
import java.io.IOException

class ServerUtil {

    // 단순 기능 수행 -> 서버에 요청 -> 응답을 화면에 전달
    // 응답을 화면에 전달 : 나에게 발생한 이벤트를 화면단에게 대신 해달라고 한다. (Interface 활용)
    interface JsonResponseHandler {
        fun onResponse(jsonObj: JSONObject) {

        }
    }

    // static 함수들로 활용 -> ServerUtil.기능() 형태의 코드 작성

    companion object {
        // 이 안에 만드는 변수 / 함수는 전부 static 처럼 동작
        // 호스트 주소를 변수에 저장 ( 가져다 쓰기 편하게 - ServerUtil 안에서만)
        private val HOST_URL = "http://54.180.52.26"

        // 로그인 기능 실행 함수
        // 아아디/패스워드 전달 + 서버에 다녀오면 어떤 일을 할건지? 인터페이스 객체 같이 전달
        fun postRequestSignIn(id: String, pw: String, handler: JsonResponseHandler?) {
            // 1. 어디로(url) 갈것인가? HOST_URL + Endpoint
            val urlString = "${HOST_URL}/user"

            // 2. 어떤 데이터를 들고 갈것인가? -> 파라미터
            val formData = FormBody.Builder()
                .add("email", id)
                .add("password", pw)
                .build()

            // 3. 어떤 방식으로 접근?
            // 모두 모아서 하나의 Request 정보로 만들어주자
            val request = Request.Builder()
                .url(urlString)
                .post(formData)
                .build()

            // 만들어진 request를 실제로 호출해야함
            // 요청을 한다 -> 클라이언트의 역할 -> 앱이 클라이언트로 동작해야함.
            val client = OkHttpClient()

            // 만들어진 요청 호출 -> 응답이 왔을때 분석 / UI 반영
            // 호출을 하면 -> 응답 받아서 처리 (처리할 코드를 등록)
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    // 어떤 내용이든 응답이 돌아온 경우 (로그인 성공, 실패 모두 응답)
                    // 응답에 포함된 데이터들 중 -> 본문(Body)을 보자.

                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    // 본문을 그냥 String 으로 찍으면 한글이 깨짐
                    // JSONObject 형태로 변환
                    Log.d("서버 응답 본문", jsonObj.toString())

                    //받아낸 jsonObj를 통째로 -> 화면의 응답처리 코드에 넣어주자
                    handler?.onResponse(jsonObj)

                }

                override fun onFailure(call: Call, e: IOException) {
                    // 실패 : 서버 연결 자체를 실패. 응답 X
                    // 인터넷 끊김, 서버 접속 불가 등

                }
            })
        }

        // 회원 가입 실행 함수
        // handler -> 서버에 갔다와서 어떤행동을 할건지?
        fun putRequestSingUp(
            email: String,
            password: String,
            nickname: String,
            handler: JsonResponseHandler?
        ) {
            val urlString = "${HOST_URL}/user"
            val formData = FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .add("nick_name", nickname)
                .build()

            val request = Request.Builder()
                .url(urlString)
                .put(formData)
                .build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버응답본문", jsonObj.toString())
                    handler?.onResponse(jsonObj)
                }

                override fun onFailure(call: Call, e: IOException) {

                }
            })
        }

        // 이메일/닉네임 중복 확인 함수
        fun getRequestDuplCheck(type: String, value: String, handler: JsonResponseHandler?) {
            // GET 메소드로 서버에 요청 -> URL을 적을떄, 쿼리 파라미터들도 같이 적어줘야함
            // 어디로 + 무엇을 들고 -> 한번에 작성

            // 호스트주소/엔드포인트 기반으로, 파라미터들을 쉽게 첨부할 수 있도록 도와주는 변수
            val url = "${HOST_URL}/user_check".toHttpUrlOrNull()!!.newBuilder()
            url.addEncodedQueryParameter("type", type)
            url.addEncodedQueryParameter("value", value)

            val urlString = url.toString()

            Log.d("완성된 URL", urlString)

            val request = Request.Builder()
                .url(urlString)
                .get()
                .build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버응답", jsonObj.toString())
                    handler?.onResponse(jsonObj)
                }

                override fun onFailure(call: Call, e: IOException) {
                }
            })
        }

        // 메인화면 데이터 가져오기
        // 저장된 토큰값을 서버에 전송 -> pref를 열기 위한 재료로 context 필요
        fun getRequestMainData(context: Context, handler: JsonResponseHandler?) {
            val url = "${HOST_URL}/v2/main_info".toHttpUrlOrNull()!!.newBuilder()
            url.addEncodedQueryParameter("device_token", FirebaseInstanceId.getInstance().token)
            url.addEncodedQueryParameter("os", "Android")

            val urlString = url.toString()

            Log.d("완성된 URL", urlString)

            val request = Request.Builder()
                .url(urlString)
                .get()
                .header("X-Http-Token", ContextUtil.getToken(context))
                .build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버응답", jsonObj.toString())
                    handler?.onResponse(jsonObj)
                }

                override fun onFailure(call: Call, e: IOException) {
                }
            })
        }

        // 토론 상세 정보 (특정 주제에 대해서만) 가져오기
        fun getRequestTopicDetail(context: Context, topicId: Int, handler: JsonResponseHandler?) {

            val url = "${HOST_URL}/topic".toHttpUrlOrNull()!!.newBuilder()
            // 어떤 데이터를 보고싶은지, /숫자 형태로 이어 붙이는 주소 -> Path 라고 부름
            // 주소?type=EMAIL 등 파라미터 이름 = 값 형태로 이어붙이는 주소 -> Query 라고 부름
            url.addPathSegment(topicId.toString())

            url.addEncodedQueryParameter("order_type", "NEW")

            val urlString = url.toString()

            Log.d("완성된URL", urlString)

            val request = Request.Builder()
                .url(urlString)
                .get()
                .header("X-Http-Token", ContextUtil.getToken(context))
                .build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버응답", jsonObj.toString())
                    handler?.onResponse(jsonObj)
                }

                override fun onFailure(call: Call, e: IOException) {
                }
            })
        }

        // 진영 선택 투표 함수
        fun postRequestTopicVote(context: Context, sideId: Int, handler: JsonResponseHandler?) {
            val urlString = "${HOST_URL}/topic_vote"

            val formData = FormBody.Builder()
                .add("side_id", sideId.toString())
                .build()

            val request = Request.Builder()
                .url(urlString)
                .post(formData)
                .header("X-Http-Token", ContextUtil.getToken(context))
                .build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버 응답 본문", jsonObj.toString())

                    handler?.onResponse(jsonObj)
                }

                override fun onFailure(call: Call, e: IOException) {
                }
            })
        }

        // 토론 주제에 의견 등록하기
        fun postRequestTopicReply(
            context: Context,
            topicId: Int,
            content: String,
            handler: JsonResponseHandler?
        ) {
            val urlString = "${HOST_URL}/topic_reply"

            val formData = FormBody.Builder()
                .add("topic_id", topicId.toString())
                .add("content", content)
                .build()

            val request = Request.Builder()
                .url(urlString)
                .post(formData)
                .header("X-Http-Token", ContextUtil.getToken(context))
                .build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버 응답 본문", jsonObj.toString())

                    handler?.onResponse(jsonObj)
                }

                override fun onFailure(call: Call, e: IOException) {
                }
            })
        }

        // 댓글 좋아요/싫어요 상태 반영
        fun postRequestReplyLikeOrHate(
            context: Context,
            replyId: Int,
            isLike: Boolean,
            handler: JsonResponseHandler?
        ) {
            val urlString = "${HOST_URL}/topic_reply_like"

            val formData = FormBody.Builder()
                .add("reply_id", replyId.toString())
                .add("is_like", isLike.toString())
                .build()

            val request = Request.Builder()
                .url(urlString)
                .post(formData)
                .header("X-Http-Token", ContextUtil.getToken(context))
                .build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버 응답 본문", jsonObj.toString())

                    handler?.onResponse(jsonObj)
                }

                override fun onFailure(call: Call, e: IOException) {
                }
            })
        }

        // 알림 개수 or 목록까지 가져오기
        fun getRequestNotificationCountOrList(
            context: Context,
            needList: Boolean,
            handler: JsonResponseHandler?
        ) {
            val url = "${HOST_URL}/notification".toHttpUrlOrNull()!!.newBuilder()
            url.addEncodedQueryParameter("need_all_notis", needList.toString())

            val urlString = url.toString()

            Log.d("완성된 URL", urlString)

            val request = Request.Builder()
                .url(urlString)
                .get()
                .header("X-Http-Token", ContextUtil.getToken(context))
                .build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버응답", jsonObj.toString())
                    handler?.onResponse(jsonObj)
                }

                override fun onFailure(call: Call, e: IOException) {
                }
            })
        }

        // 알림 읽음 처리
        fun postRequestNotificationRead(
            context: Context,
            notiId: Int,
            handler: JsonResponseHandler?
        ) {
            val urlString = "${HOST_URL}/notification"

            val formData = FormBody.Builder()
                .add("noti_id", notiId.toString())
                .build()

            val request = Request.Builder()
                .url(urlString)
                .post(formData)
                .header("X-Http-Token", ContextUtil.getToken(context))
                .build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버 응답 본문", jsonObj.toString())

                    handler?.onResponse(jsonObj)
                }

                override fun onFailure(call: Call, e: IOException) {
                }
            })
        }

        // 댓글에 답글 달기
        fun postRequestReplyToParentReply(
            context: Context,
            content: String,
            parentReplyId: Int,
            handler: JsonResponseHandler?
        ) {
            val urlString = "${HOST_URL}/topic_reply"

            val formData = FormBody.Builder()
                .add("content", content)
                .add("parent_reply_id", parentReplyId.toString())
                .build()

            val request = Request.Builder()
                .url(urlString)
                .post(formData)
                .header("X-Http-Token", ContextUtil.getToken(context))
                .build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버 응답 본문", jsonObj.toString())

                    handler?.onResponse(jsonObj)
                }

                override fun onFailure(call: Call, e: IOException) {
                }
            })
        }

        // 답글 목록 가져오기
        fun getRequestChildReplyList(
            context: Context,
            parentReplyId: Int,
            handler: JsonResponseHandler?
        ) {
            val url = "${HOST_URL}/topic_reply/${parentReplyId}".toHttpUrlOrNull()!!.newBuilder()

            val urlString = url.toString()

            Log.d("완성된 URL", urlString)

            val request = Request.Builder()
                .url(urlString)
                .get()
                .header("X-Http-Token", ContextUtil.getToken(context))
                .build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버응답", jsonObj.toString())
                    handler?.onResponse(jsonObj)
                }

                override fun onFailure(call: Call, e: IOException) {
                }
            })
        }

        // 서버에서 내 정보 가져오기
        fun getRequestUserData(context: Context, handler: JsonResponseHandler?) {
            val url = "${HOST_URL}/user_info".toHttpUrlOrNull()!!.newBuilder()
            val urlString = url.toString()

            Log.d("완성된 URL", urlString)

            val request = Request.Builder()
                .url(urlString)
                .get()
                .header("X-Http-Token", ContextUtil.getToken(context))
                .build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버응답", jsonObj.toString())
                    handler?.onResponse(jsonObj)
                }

                override fun onFailure(call: Call, e: IOException) {
                }
            })
        }

        // 댓글/답글 삭제
        fun deleteRequestReply(
            context: Context,
            replyId: Int,
            handler: JsonResponseHandler?
        ) {
            val url = "${HOST_URL}/topic_reply".toHttpUrlOrNull()!!.newBuilder()
            url.addEncodedQueryParameter("reply_id", replyId.toString())

            val urlString = url.toString()

            Log.d("완성된 URL", urlString)

            val request = Request.Builder()
                .url(urlString)
                .delete()
                .header("X-Http-Token", ContextUtil.getToken(context))
                .build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버응답", jsonObj.toString())
                    handler?.onResponse(jsonObj)
                }

                override fun onFailure(call: Call, e: IOException) {
                }
            })
        }

        // 유저 리스트 가져오기
        fun getRequestUserList(context: Context, handler: JsonResponseHandler?) {
            val url = "${HOST_URL}/user".toHttpUrlOrNull()!!.newBuilder()

            val urlString = url.toString()

            Log.d("완성된 URL", urlString)

            val request = Request.Builder()
                .url(urlString)
                .get()
                .header("X-Http-Token", ContextUtil.getToken(context))
                .build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버응답", jsonObj.toString())
                    handler?.onResponse(jsonObj)
                }

                override fun onFailure(call: Call, e: IOException) {
                }
            })
        }

        // 콕 찔러보기
        fun postRequestForkUser(context: Context, userId: Int, handler: JsonResponseHandler?) {
            val urlString = "${HOST_URL}/user_check"

            val formData = FormBody.Builder()
                .add("user_id", userId.toString())
                .build()

            val request = Request.Builder()
                .url(urlString)
                .post(formData)
                .header("X-Http-Token", ContextUtil.getToken(context))
                .build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버 응답 본문", jsonObj.toString())

                    handler?.onResponse(jsonObj)
                }

                override fun onFailure(call: Call, e: IOException) {
                }
            })
        }


    }
}