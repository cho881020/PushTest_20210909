package com.nepplus.colosseum_0903.utils

import android.content.Context
import android.util.Log
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject
import java.io.IOException

class ServerUtil {
    // 단순 기능 수행 -> 서버에 요청을 날리고 -> 응답을 화면에 전달

    // 응답을 화면에 전달 : 나(ServerUtil)에게 일어난 이벤트를 -> 화면(Activity)에게 대신 해달라고 요청(Interface 활용)

    interface JsonResponseHandler {
        fun onResponse(jsonObj: JSONObject)
    }

    // 어떤 객체가 하던, 요청 / 응답 처리만 잘 되면 그만
    // 이런 함수를 만든다? -> static 함수들로 활용하면 ServerUtil.기능() 코드 작성이 가능

    companion object {

        // 이 안에 만드는 변수나 함수는 전부 static 처럼 동작함

        // 호스트 주소를 애초에 변수로 저장해두자(쓰기 편하게 - ServerUtil 안에서만 사용)
        private val HOST_URL = "http://54.180.52.26"

        // 로그인 기능 실행 함수
        // 아이디 / 비번 전달 + 서버를 다녀오면 어떤 일을 할 건지? 인터페이스 객체 같이 전달
        fun postRequestSignIn(id: String, pw: String, handler: JsonResponseHandler?) {

            // 1. 어디로(URL) 갈 것인가? HOST_URL + EndPoint
            val urlString = "${HOST_URL}/user"

            // 어떤 데이터를 들고 갈 것인가
            val formData = FormBody.Builder()
                .add("email", id).add("password", pw).build()

            // 어떤 방식으로 접근할 것인가? Request 에 같이 적어주자
            // 세 가지를 모두 모아서 하나의 Request 정보로 만들어주자

            val request = Request.Builder()
                .url(urlString).post(formData).build()

            // 만들어진 request 를 실제로 호출해야함
            // 요청을 한다 -> 클라이언트의 역할 -> 앱이 클라이언트로 동작해야함
            val client = OkHttpClient()

            // 만들어진 요청을 호출 -> 응답이 왔을 때 분석 / UI 반영
            // 호출을 하면 -> 서버가 데이터 처리... -> 응답 받아서 처리(처리할 코드를 등록)
            client.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    // 실패? 서버 연결 자체를 실패했을 때. 응답이 돌아오지 않은 경우
                    // 비번이 틀려서 로그인 실패 -> 응답이 왔지만 내용이 실패(응답은 온 것)
                    // 인터넷 끊김. 서버 다운 등등.
                }

                override fun onResponse(call: Call, response: Response) {
                    // 어떤 내용이든, 응답이 돌아온 경우(로그인 성공 / 실패 모두 응답임)
                    // 응답에 포함된 데이터들 중 -> 본문에만 관심을 두자
                    val bodyString = response.body!!.string() // toString() 안 됨

                    // 본문을 그냥 String 으로 찍어보면 -> 한글이 깨져보임
                    // JSONObject 형태로 변환해서 -> 다시 String 으로 바꿔보면 한글이 보임
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버응답본문", jsonObj.toString())

                    // code 값 추출 연습 -> 화면에서 분석해서 토스트를 띄우는 등의 UI 처리
//                    val code = jsonObj.getInt("code")
//
//                    Log.d("코드값)",code.toString())


                    // 받아낸 jsonObj 를 통째로 -> 화면의 응답 처리 코드에 넘겨주자
                    handler?.onResponse(jsonObj)

                }
            })

        }

        // 회원 가입 함수
        fun putRequestSignUp(email: String, password: String, nickname: String, handler: JsonResponseHandler?) {

            val urlString = "${HOST_URL}/user"

            val formData = FormBody.Builder().add("email", email).add("password", password)
                .add("nick_name", nickname).build()

            val request = Request.Builder().url(urlString).put(formData).build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버가 응답한 본문",jsonObj.toString())
                    handler?.onResponse(jsonObj)
                }


            })

        }

        // 이메일 / 닉네임 중복 확인 함수
        fun getRequestDuplCheck(type: String, value: String, handler: JsonResponseHandler?) {

            // GET 메소드로 서버에 요청 -> URL 을 적을 때, (query) 파라미터들도 같이 적어줘야 한다.
            // 어디로 + 무엇을 들고 -> 한번에 작성된다.

            // 호스트 주소 / 엔드포인트 기반으로, 파라미터들을 쉽게 첨부할 수 있도록 도와주는 변수
            val url = "${ HOST_URL}/user_check".toHttpUrlOrNull()!!.newBuilder()
            // 기본 url 뒤에, 파라미터들 첨부
            url.addEncodedQueryParameter("type",type)
            url.addEncodedQueryParameter("value",value)

            val urlString = url.toString()
            Log.d("완성된 주소", urlString)

            val request = Request.Builder().url(urlString).get().build()

            val client = OkHttpClient() // client 부분은 모든 곳에서 다 동일하게 사용함.

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버응답", jsonObj.toString())
                    handler?.onResponse(jsonObj)
                }
            })

        }

        // 메인화면 데이터 가져오기
        // 저장된 토큰값을 서버에 전송 -> 메모장을 열기 위한 재료로 -> Context 가 필요함
        fun getRequestMainData(context: Context, handler: JsonResponseHandler?) {

            val url = "${ HOST_URL}/v2/main_info".toHttpUrlOrNull()!!.newBuilder()
//            url.addEncodedQueryParameter("type",type)
//            url.addEncodedQueryParameter("value",value)

            val urlString = url.toString()
            Log.d("완성된 주소", urlString)

            val request = Request.Builder().url(urlString).get().header("X-Http-Token", ContextUtil.getToken(context)).build()

            val client = OkHttpClient() // client 부분은 모든 곳에서 다 동일하게 사용함.

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버응답", jsonObj.toString())
                    handler?.onResponse(jsonObj)
                }
            })

        }

        // 내 사용자 정보 가져오기
        fun getRequestUserData(context: Context, handler: JsonResponseHandler?) {

            val url = "${ HOST_URL}/user_info".toHttpUrlOrNull()!!.newBuilder()
//            url.addEncodedQueryParameter("type",type)
//            url.addEncodedQueryParameter("value",value)

            val urlString = url.toString()
            Log.d("완성된 주소", urlString)

            val request = Request.Builder().url(urlString).get().header("X-Http-Token", ContextUtil.getToken(context)).build()

            val client = OkHttpClient() // client 부분은 모든 곳에서 다 동일하게 사용함.

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버응답", jsonObj.toString())
                    handler?.onResponse(jsonObj)
                }
            })

        }

        // 토론 상세 정보(특정 주제에 대해서만) 가져오기
        fun getRequestTopicDetail(context: Context, topicId: Int, handler: JsonResponseHandler?) {

            val url = "${ HOST_URL}/topic".toHttpUrlOrNull()!!.newBuilder()
            // 주소 뒤에 /3 등 어떤 데이터를 보고 싶은지 / 형태로 이어붙이는 주소는 -> Path 라고 부름
            // 주소?type=EMAIL 등 파라미터이름=값 형태로 이어붙이는 주소 -> Query 라고 부름
            url.addPathSegment(topicId.toString())

            url.addEncodedQueryParameter("order_type","NEW")
//            url.addEncodedQueryParameter("value",value)

            val urlString = url.toString()
            Log.d("완성된 주소", urlString)

            val request = Request.Builder().url(urlString).get().header("X-Http-Token", ContextUtil.getToken(context)).build()

            val client = OkHttpClient() // client 부분은 모든 곳에서 다 동일하게 사용함.

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버응답", jsonObj.toString())
                    handler?.onResponse(jsonObj)
                }
            })

        }

        // 댓글 상세 정보(답글 목록) 가져오기
        fun getRequestReplyDetail(context: Context, replyId: Int, handler: JsonResponseHandler?) {

            val url = "${ HOST_URL}/topic_reply".toHttpUrlOrNull()!!.newBuilder()
            // 주소 뒤에 /3 등 어떤 데이터를 보고 싶은지 / 형태로 이어붙이는 주소는 -> Path 라고 부름
            // 주소?type=EMAIL 등 파라미터이름=값 형태로 이어붙이는 주소 -> Query 라고 부름
            url.addPathSegment(replyId.toString())

//            url.addEncodedQueryParameter("order_type","NEW")
//            url.addEncodedQueryParameter("value",value)

            val urlString = url.toString()
            Log.d("완성된 주소", urlString)

            val request = Request.Builder().url(urlString).get().header("X-Http-Token", ContextUtil.getToken(context)).build()

            val client = OkHttpClient() // client 부분은 모든 곳에서 다 동일하게 사용함.

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버응답", jsonObj.toString())
                    handler?.onResponse(jsonObj)
                }
            })

        }

        // 진영 선택하여 투표하기
        fun postRequestTopicVote(context: Context, sideId: Int, handler: JsonResponseHandler?) {

            // 1. 어디로(URL) 갈 것인가? HOST_URL + EndPoint
            val urlString = "${HOST_URL}/topic_vote"

            // 어떤 데이터를 들고 갈 것인가
            val formData = FormBody.Builder()
                .add("side_id", sideId.toString()).build()

            // 어떤 방식으로 접근할 것인가? Request 에 같이 적어주자
            // 세 가지를 모두 모아서 하나의 Request 정보로 만들어주자

            val request = Request.Builder()
                .url(urlString).post(formData)
                .header("X-Http-Token", ContextUtil.getToken(context)).build()

            // 만들어진 request 를 실제로 호출해야함
            // 요청을 한다 -> 클라이언트의 역할 -> 앱이 클라이언트로 동작해야함
            val client = OkHttpClient()

            // 만들어진 요청을 호출 -> 응답이 왔을 때 분석 / UI 반영
            // 호출을 하면 -> 서버가 데이터 처리... -> 응답 받아서 처리(처리할 코드를 등록)
            client.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    // 실패? 서버 연결 자체를 실패했을 때. 응답이 돌아오지 않은 경우
                    // 비번이 틀려서 로그인 실패 -> 응답이 왔지만 내용이 실패(응답은 온 것)
                    // 인터넷 끊김. 서버 다운 등등.
                }

                override fun onResponse(call: Call, response: Response) {
                    // 어떤 내용이든, 응답이 돌아온 경우(로그인 성공 / 실패 모두 응답임)
                    // 응답에 포함된 데이터들 중 -> 본문에만 관심을 두자
                    val bodyString = response.body!!.string() // toString() 안 됨

                    // 본문을 그냥 String 으로 찍어보면 -> 한글이 깨져보임
                    // JSONObject 형태로 변환해서 -> 다시 String 으로 바꿔보면 한글이 보임
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버응답본문", jsonObj.toString())

                    // code 값 추출 연습 -> 화면에서 분석해서 토스트를 띄우는 등의 UI 처리
//                    val code = jsonObj.getInt("code")
//
//                    Log.d("코드값)",code.toString())

                    // 받아낸 jsonObj 를 통째로 -> 화면의 응답 처리 코드에 넘겨주자
                    handler?.onResponse(jsonObj)

                }
            })

        }

        // 좋아요 / 싫어요 반영하기
        fun postRequestReplyLikeOrHate(context: Context, replyId: Int, isLike: Boolean, handler: JsonResponseHandler?) {

            // 1. 어디로(URL) 갈 것인가? HOST_URL + EndPoint
            val urlString = "${HOST_URL}/topic_reply_like"

            // 어떤 데이터를 들고 갈 것인가
            val formData = FormBody.Builder()
                .add("reply_id", replyId.toString())
                .add("is_like", isLike.toString())
                .build()

            // 어떤 방식으로 접근할 것인가? Request 에 같이 적어주자
            // 세 가지를 모두 모아서 하나의 Request 정보로 만들어주자

            val request = Request.Builder()
                .url(urlString).post(formData)
                .header("X-Http-Token", ContextUtil.getToken(context)).build()

            // 만들어진 request 를 실제로 호출해야함
            // 요청을 한다 -> 클라이언트의 역할 -> 앱이 클라이언트로 동작해야함
            val client = OkHttpClient()

            // 만들어진 요청을 호출 -> 응답이 왔을 때 분석 / UI 반영
            // 호출을 하면 -> 서버가 데이터 처리... -> 응답 받아서 처리(처리할 코드를 등록)
            client.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    // 실패? 서버 연결 자체를 실패했을 때. 응답이 돌아오지 않은 경우
                    // 비번이 틀려서 로그인 실패 -> 응답이 왔지만 내용이 실패(응답은 온 것)
                    // 인터넷 끊김. 서버 다운 등등.
                }

                override fun onResponse(call: Call, response: Response) {
                    // 어떤 내용이든, 응답이 돌아온 경우(로그인 성공 / 실패 모두 응답임)
                    // 응답에 포함된 데이터들 중 -> 본문에만 관심을 두자
                    val bodyString = response.body!!.string() // toString() 안 됨

                    // 본문을 그냥 String 으로 찍어보면 -> 한글이 깨져보임
                    // JSONObject 형태로 변환해서 -> 다시 String 으로 바꿔보면 한글이 보임
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버응답본문", jsonObj.toString())

                    // code 값 추출 연습 -> 화면에서 분석해서 토스트를 띄우는 등의 UI 처리
//                    val code = jsonObj.getInt("code")
//
//                    Log.d("코드값)",code.toString())

                    // 받아낸 jsonObj 를 통째로 -> 화면의 응답 처리 코드에 넘겨주자
                    handler?.onResponse(jsonObj)

                }
            })

        }

        // 토론 주제에 의견 등록하기
        fun postRequestTopicReply(context: Context, topicId: Int, content: String, handler: JsonResponseHandler?) {

            // 1. 어디로(URL) 갈 것인가? HOST_URL + EndPoint
            val urlString = "${HOST_URL}/topic_reply"

            // 어떤 데이터를 들고 갈 것인가
            val formData = FormBody.Builder()
                .add("topic_id", topicId.toString())
                .add("content", content)
                .build()

            // 어떤 방식으로 접근할 것인가? Request 에 같이 적어주자
            // 세 가지를 모두 모아서 하나의 Request 정보로 만들어주자

            val request = Request.Builder()
                .url(urlString).post(formData)
                .header("X-Http-Token", ContextUtil.getToken(context)).build()

            // 만들어진 request 를 실제로 호출해야함
            // 요청을 한다 -> 클라이언트의 역할 -> 앱이 클라이언트로 동작해야함
            val client = OkHttpClient()

            // 만들어진 요청을 호출 -> 응답이 왔을 때 분석 / UI 반영
            // 호출을 하면 -> 서버가 데이터 처리... -> 응답 받아서 처리(처리할 코드를 등록)
            client.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    // 실패? 서버 연결 자체를 실패했을 때. 응답이 돌아오지 않은 경우
                    // 비번이 틀려서 로그인 실패 -> 응답이 왔지만 내용이 실패(응답은 온 것)
                    // 인터넷 끊김. 서버 다운 등등.
                }

                override fun onResponse(call: Call, response: Response) {
                    // 어떤 내용이든, 응답이 돌아온 경우(로그인 성공 / 실패 모두 응답임)
                    // 응답에 포함된 데이터들 중 -> 본문에만 관심을 두자
                    val bodyString = response.body!!.string() // toString() 안 됨

                    // 본문을 그냥 String 으로 찍어보면 -> 한글이 깨져보임
                    // JSONObject 형태로 변환해서 -> 다시 String 으로 바꿔보면 한글이 보임
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버응답본문", jsonObj.toString())

                    // code 값 추출 연습 -> 화면에서 분석해서 토스트를 띄우는 등의 UI 처리
//                    val code = jsonObj.getInt("code")
//
//                    Log.d("코드값)",code.toString())

                    // 받아낸 jsonObj 를 통째로 -> 화면의 응답 처리 코드에 넘겨주자
                    handler?.onResponse(jsonObj)

                }
            })

        }

        // 알림 갯수, 목록 가져오기
        fun getRequestNotificationCountOrList(context: Context, needList: Boolean, handler: JsonResponseHandler?) {

            val url = "${ HOST_URL}/notification".toHttpUrlOrNull()!!.newBuilder()
            url.addEncodedQueryParameter("need_all_notis",needList.toString())
//            url.addEncodedQueryParameter("value",value)

            val urlString = url.toString()
            Log.d("완성된 주소", urlString)

            val request = Request.Builder().url(urlString).get().header("X-Http-Token", ContextUtil.getToken(context)).build()

            val client = OkHttpClient() // client 부분은 모든 곳에서 다 동일하게 사용함.

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }
                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버응답", jsonObj.toString())
                    handler?.onResponse(jsonObj)
                }
            })
        }

        // 댓글 삭제하기
        fun deleteRequestReply(context: Context, replyId: Int, handler: JsonResponseHandler?) {

            val url = "${ HOST_URL}/topic_reply".toHttpUrlOrNull()!!.newBuilder()
            url.addEncodedQueryParameter("reply_id",replyId.toString())
//            url.addEncodedQueryParameter("value",value)

            val urlString = url.toString()
            Log.d("완성된 주소", urlString)

            // get 이 아니고 delete 이므로 get() -> delete()
            val request = Request.Builder()
                .url(urlString).delete().header("X-Http-Token", ContextUtil.getToken(context)).build()

            val client = OkHttpClient() // client 부분은 모든 곳에서 다 동일하게 사용함.

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }
                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버응답", jsonObj.toString())
                    handler?.onResponse(jsonObj)
                }
            })
        }

        // 알림을 어디까지 읽었는지 서버에 알려주기
        fun postRequestNotificationRead(context: Context, notiId: Int, handler: JsonResponseHandler?) {

            // 1. 어디로(URL) 갈 것인가? HOST_URL + EndPoint
            val urlString = "${HOST_URL}/notification"

            // 어떤 데이터를 들고 갈 것인가
            val formData = FormBody.Builder()
                .add("noti_id", notiId.toString())
                .build()

            // 어떤 방식으로 접근할 것인가? Request 에 같이 적어주자
            // 세 가지를 모두 모아서 하나의 Request 정보로 만들어주자

            val request = Request.Builder()
                .url(urlString).post(formData)
                .header("X-Http-Token", ContextUtil.getToken(context)).build()

            // 만들어진 request 를 실제로 호출해야함
            // 요청을 한다 -> 클라이언트의 역할 -> 앱이 클라이언트로 동작해야함
            val client = OkHttpClient()

            // 만들어진 요청을 호출 -> 응답이 왔을 때 분석 / UI 반영
            // 호출을 하면 -> 서버가 데이터 처리... -> 응답 받아서 처리(처리할 코드를 등록)
            client.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    // 실패? 서버 연결 자체를 실패했을 때. 응답이 돌아오지 않은 경우
                    // 비번이 틀려서 로그인 실패 -> 응답이 왔지만 내용이 실패(응답은 온 것)
                    // 인터넷 끊김. 서버 다운 등등.
                }

                override fun onResponse(call: Call, response: Response) {
                    // 어떤 내용이든, 응답이 돌아온 경우(로그인 성공 / 실패 모두 응답임)
                    // 응답에 포함된 데이터들 중 -> 본문에만 관심을 두자
                    val bodyString = response.body!!.string() // toString() 안 됨

                    // 본문을 그냥 String 으로 찍어보면 -> 한글이 깨져보임
                    // JSONObject 형태로 변환해서 -> 다시 String 으로 바꿔보면 한글이 보임
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버응답본문", jsonObj.toString())

                    // code 값 추출 연습 -> 화면에서 분석해서 토스트를 띄우는 등의 UI 처리
//                    val code = jsonObj.getInt("code")
//
//                    Log.d("코드값)",code.toString())

                    // 받아낸 jsonObj 를 통째로 -> 화면의 응답 처리 코드에 넘겨주자
                    handler?.onResponse(jsonObj)

                }
            })

        }

        // 대댓글 서버에 알려주기
        fun postRequestReplyToReply(context: Context, content: String, parentReplyId: Int, handler: JsonResponseHandler?) {

            // 1. 어디로(URL) 갈 것인가? HOST_URL + EndPoint
            val urlString = "${HOST_URL}/topic_reply"

            // 어떤 데이터를 들고 갈 것인가
            val formData = FormBody.Builder()
                .add("content", content)
                .add("parent_reply_id", parentReplyId.toString())
                .build()

            // 어떤 방식으로 접근할 것인가? Request 에 같이 적어주자
            // 세 가지를 모두 모아서 하나의 Request 정보로 만들어주자

            val request = Request.Builder()
                .url(urlString).post(formData)
                .header("X-Http-Token", ContextUtil.getToken(context)).build()

            // 만들어진 request 를 실제로 호출해야함
            // 요청을 한다 -> 클라이언트의 역할 -> 앱이 클라이언트로 동작해야함
            val client = OkHttpClient()

            // 만들어진 요청을 호출 -> 응답이 왔을 때 분석 / UI 반영
            // 호출을 하면 -> 서버가 데이터 처리... -> 응답 받아서 처리(처리할 코드를 등록)
            client.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    // 실패? 서버 연결 자체를 실패했을 때. 응답이 돌아오지 않은 경우
                    // 비번이 틀려서 로그인 실패 -> 응답이 왔지만 내용이 실패(응답은 온 것)
                    // 인터넷 끊김. 서버 다운 등등.
                }

                override fun onResponse(call: Call, response: Response) {
                    // 어떤 내용이든, 응답이 돌아온 경우(로그인 성공 / 실패 모두 응답임)
                    // 응답에 포함된 데이터들 중 -> 본문에만 관심을 두자
                    val bodyString = response.body!!.string() // toString() 안 됨

                    // 본문을 그냥 String 으로 찍어보면 -> 한글이 깨져보임
                    // JSONObject 형태로 변환해서 -> 다시 String 으로 바꿔보면 한글이 보임
                    val jsonObj = JSONObject(bodyString)
                    Log.d("서버응답본문", jsonObj.toString())

                    // code 값 추출 연습 -> 화면에서 분석해서 토스트를 띄우는 등의 UI 처리
//                    val code = jsonObj.getInt("code")
//
//                    Log.d("코드값)",code.toString())

                    // 받아낸 jsonObj 를 통째로 -> 화면의 응답 처리 코드에 넘겨주자
                    handler?.onResponse(jsonObj)

                }
            })

        }

    }
}
