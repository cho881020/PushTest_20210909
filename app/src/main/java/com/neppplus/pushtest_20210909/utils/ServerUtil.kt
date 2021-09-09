package com.neppplus.colosseum_20210903.utils

import android.content.Context
import android.util.Log
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject
import java.io.IOException

class ServerUtil {

//    단순 기능 수행이란? -> 서버에 요청을 날리고 -> 응답을 화면에 전달.

//    응답을 화면에 전달 : 나에게 발생한 이벤트를 -> 화면단에게 대신 해달라고 한다.  (Interface 활용)
    interface JsonResponseHandler {
        fun onResponse( jsonObj : JSONObject )
    }


//    이 짓은 어떤 객체가 하던 요청/응답 처리만 잘 되면 그만임
//    이런 함수를 만든다? -> 뭐로 만드는게 낫다? -> static 함수들로 활용. ServerUtil 기능() 코드 작성 가능

    companion object {
//        이 안에 만드는 변수 / 함수는 전부 static처럼 동작함.

//        호스트 주소를 애초에 변수로 저장해두자. (가져다 쓰기 편하게 - ServerUtil안에서만)
        private val HOST_URL = "http://54.180.52.26"

//        로그인 기능 실행 함수.
//        아이디/비번 전달 + 서버에다녀오면 어떤일을 할건지? 인터페이스 객체 같이 전달.
        fun postRequestSignIn( id: String, pw: String, handler : JsonResponseHandler? ) {

//            1. 어디로(url) 갈것인가? HOST_URL + Endpoint
            val urlString = "${HOST_URL}/user"

//            2. 어떤 데이터를 들고 갈것인가? 파라미터
            val formData = FormBody.Builder()
                .add("email", id)
                .add("password", pw)
                .build()

//            3. 어떤 방식으로 접근할 것인지 Request에 같이 적어주자.
//            모두 모아서 하나의 Request 정보로 만들어주자.
            val request = Request.Builder()
                .url(urlString)
                .post(formData)//짐을 넣을거면 여기다 넣어라
                .build()

//            만들어진 request를 실제로 호출해야함.
//            요청을 한다 -> 클라이언트의 역할. -> 앱이 클라이언트로 동작해야함.

            val client = OkHttpClient()

//            만들어진 요청 호출. => 응답이 왔을때 분석 / UI 반영
//             호출을 하면 -> 서버가 데이터 처리...--> 응답 받아서 처리 (처리할 코드를 등록)
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
//                    실패 == 서버 '연결' '자체를' 실패. (응답이 돌아오지 않은 경우에)
//                    ex) 비번 틀려서 로그인 실패 : 응답은 돌아왔는데 그 내용이 실패한 경우임( 응답은 된거 )
//                    인터넷 끊김, 서버 접속 불가 등 == 실패 O
                }

                override fun onResponse(call: Call, response: Response) {
//                    어떤 내용이든, 응답이 돌아온 경우. (로그인 성공, 실패 모두 응답)
//                    응답에 포함된 데이터들중 -> 본문(body)을 보자.

                    val bodyString = response.body!!.string()
//                    본문을 그냥 String으로 찍어보면 -> 한글이 깨져보임.
//                    JSONObject 형태로 변환해서 -> 다시 String으로 바꿔보면 한글이 보임.
                    val jsonObj = JSONObject(bodyString)

                    Log.d("서버응답본문", jsonObj.toString())


//                    code값 추출 연습. -> 화면에서 분석해서, 토스트를 띄우는 등의 UI 처리.
//                    val code = jsonObj.getInt("code")
//
//                    Log.d("코드값", code.toString())


//                    받아낸 jsonObj를 통째로 -> 화면의 응답 처리 코드에 넘겨주자.
                    handler?.onResponse(jsonObj)

                }
            })//request를 호출해주세요하는거
              //enqueue()==호출을 하고 돌아올 때 할 일
        }


//        회원 가입 실행 함수.
        fun putRequestSignUp(email: String, password: String, nickname: String, handler: JsonResponseHandler?) {

            val urlString = "${HOST_URL}/user"

            val formData = FormBody.Builder()
                .add("email",  email)
                .add("password", password)
                .add("nick_name", nickname)
                .build()

            val request = Request.Builder()
                .url(urlString)//어디로
                .put(formData)//어떻게
                .build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body!!.string()
                    val jsonObj = JSONObject(bodyString)//로그에 저걸 그냥 넣으면 깨짐, 그래서 파싱
                    // 파싱인데 코틀린에선 다른문법씀 자바에선 파싱
                    Log.d("서버응답본문", jsonObj.toString())
                    handler?.onResponse(jsonObj)
                    // SignIn 함수와 비교하면서 봐보자
                    // 기능은 만들었지만 아직 화면은 안만듬
                }

            })

        }

//        이메일/닉네임 중복 확인 함수.
        fun getRequestDuplCheck(type: String, value: String, handler: JsonResponseHandler?) {

//            GET메소드로 서버에 요청 -> PUT이나 뭐랑은 다름 -> URL을 적을 때, (query)파라미터들도 같이 적어줘야 한다(웹툰예시)
//            어디로 + 무엇을 들고 => 한 번에 작성됨 (?를 찍는 순간 그 뒤는 파라미터임)(&넣으면 하나 더,뭐 몇 화인지(EX.웹툰))

//            호스트주소/엔드포인트 기반으로, 파라미터들을 쉽게 첨부할 수 있도록 도와주는 변수.
            val url = "${HOST_URL}/user_check".toHttpUrlOrNull()!!.newBuilder()// parse 안에 /(슬래시) 는 어디로 가느냐~임
//            기본 url뒤에, 파라미터들 첨부.
            url.addEncodedQueryParameter("type", type)
            url.addEncodedQueryParameter("value", value)

            val urlString = url.toString()

            Log.d("완성된URL", urlString)


            val request = Request.Builder()
                .url(urlString)
                .get()
                .build()

            val client = OkHttpClient()

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




//        메인화면에 데이터 가져오기(위에 getRequestDuplCheck를 통으로 복사했음)
//          뭘 가져와야할까?  -->> 사이트 보니 일단 토큰, 커에 뺴내냐 getToken함수(ContextUtil클래스의)사용
//        ==저장된 토큰값을 서버에 전송 -> 메모장을 열기 위한 재료로 -> Context가 필요함
        fun getRequestMainData(context : Context, handler: JsonResponseHandler?) {

            val url = "${HOST_URL}/v2/main_info".toHttpUrlOrNull()!!.newBuilder()
//            url.addEncodedQueryParameter("type", type)
//            url.addEncodedQueryParameter("value", value)//어디로 뭘 들고 간다

            val urlString = url.toString()
            Log.d("완성된URL", urlString)


            val request = Request.Builder()
                .url(urlString)
                .get()
                .header("X-Http-Token", ContextUtil.getToken(context))
                .build()

            val client = OkHttpClient()

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



//        토론 상세 정보(특정 주제에 대해서만) 가져오기
        fun getRequestTopicDetail(context : Context, topicId: Int, handler: JsonResponseHandler?) {

            val url = "${HOST_URL}/topic".toHttpUrlOrNull()!!.newBuilder()
//            주소/3 등 어떤 데이터를 보고싶은지, /숫자 형태로 이어붙이는 주소-> path라고 부름
//            주소?type=EMAIL 등 파라미터이름=값 형태로 이어붙이는 주소 -> Query라고 부름

            url.addPathSegment(topicId.toString())

//            url.addEncodedQueryParameter("type", type)
            url.addEncodedQueryParameter("order_type", "NEW")
//            url.addEncodedQueryParameter("value", value)//어디로 뭘 들고 간다

            val urlString = url.toString()
            Log.d("완성된URL", urlString)


            val request = Request.Builder()
                .url(urlString)
                .get()
                .header("X-Http-Token", ContextUtil.getToken(context))
                .build()

            val client = OkHttpClient()

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




//        진영 선택 투표하기
        fun postRequestTopicVote(context: Context, sideId: Int, handler : JsonResponseHandler? ) {
//                                 토큰값을 받아와야해서 context필요함

//            1. 어디로(url) 갈것인가? HOST_URL + Endpoint
             val urlString = "${HOST_URL}/topic_vote"

//            2. 어떤 데이터를 들고 갈것인가? 파라미터
             val formData = FormBody.Builder()
                    .add("side_id", sideId.toString())
//                   .add("password", pw)
                    .build()

//            3. 어떤 방식으로 접근할 것인지 Request에 같이 적어주자.
//            모두 모아서 하나의 Request 정보로 만들어주자.
             val request = Request.Builder()
                   .url(urlString)
                   .post(formData)//짐을 넣을거면 여기다 넣어라
                 .header("X-Http-Token", ContextUtil.getToken(context))
                   .build()

//            만들어진 request를 실제로 호출해야함.
//            요청을 한다 -> 클라이언트의 역할. -> 앱이 클라이언트로 동작해야함.

    val client = OkHttpClient()

//            만들어진 요청 호출. => 응답이 왔을때 분석 / UI 반영
//             호출을 하면 -> 서버가 데이터 처리...--> 응답 받아서 처리 (처리할 코드를 등록)
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
//                    실패 == 서버 '연결' '자체를' 실패. (응답이 돌아오지 않은 경우에)
//                    ex) 비번 틀려서 로그인 실패 : 응답은 돌아왔는데 그 내용이 실패한 경우임( 응답은 된거 )
//                    인터넷 끊김, 서버 접속 불가 등 == 실패 O
        }

        override fun onResponse(call: Call, response: Response) {
//                    어떤 내용이든, 응답이 돌아온 경우. (로그인 성공, 실패 모두 응답)
//                    응답에 포함된 데이터들중 -> 본문(body)을 보자.

            val bodyString = response.body!!.string()
//                    본문을 그냥 String으로 찍어보면 -> 한글이 깨져보임.
//                    JSONObject 형태로 변환해서 -> 다시 String으로 바꿔보면 한글이 보임.
            val jsonObj = JSONObject(bodyString)

            Log.d("서버응답본문", jsonObj.toString())


//                    code값 추출 연습. -> 화면에서 분석해서, 토스트를 띄우는 등의 UI 처리.
//                    val code = jsonObj.getInt("code")
//
//                    Log.d("코드값", code.toString())


//                    받아낸 jsonObj를 통째로 -> 화면의 응답 처리 코드에 넘겨주자.
            handler?.onResponse(jsonObj)

        }
    })//request를 호출해주세요하는거
    //enqueue()==호출을 하고 돌아올 때 할 일
}



//        토론 주제에 의견 등록하기

        fun postRequestTopicReply(context: Context, topicId: Int, content : String, handler : JsonResponseHandler? ) {
//                                 토큰값을 받아와야해서 context필요함

//            1. 어디로(url) 갈것인가? HOST_URL + Endpoint
              val urlString = "${HOST_URL}/topic_reply"

//            2. 어떤 데이터를 들고 갈것인가? 파라미터
             val formData = FormBody.Builder()
                    .add("topic_id", topicId.toString())
                   .add("content", content)
                    .build()

//            3. 어떤 방식으로 접근할 것인지 Request에 같이 적어주자.
//            모두 모아서 하나의 Request 정보로 만들어주자.
              val request = Request.Builder()
                   .url(urlString)
                  .post(formData)//짐을 넣을거면 여기다 넣어라
                    .header("X-Http-Token", ContextUtil.getToken(context))
                     .build()

//            만들어진 request를 실제로 호출해야함.
//            요청을 한다 -> 클라이언트의 역할. -> 앱이 클라이언트로 동작해야함.

    val client = OkHttpClient()

//            만들어진 요청 호출. => 응답이 왔을때 분석 / UI 반영
//             호출을 하면 -> 서버가 데이터 처리...--> 응답 받아서 처리 (처리할 코드를 등록)
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
//                    실패 == 서버 '연결' '자체를' 실패. (응답이 돌아오지 않은 경우에)
//                    ex) 비번 틀려서 로그인 실패 : 응답은 돌아왔는데 그 내용이 실패한 경우임( 응답은 된거 )
//                    인터넷 끊김, 서버 접속 불가 등 == 실패 O
        }

        override fun onResponse(call: Call, response: Response) {
//                    어떤 내용이든, 응답이 돌아온 경우. (로그인 성공, 실패 모두 응답)
//                    응답에 포함된 데이터들중 -> 본문(body)을 보자.

            val bodyString = response.body!!.string()
//                    본문을 그냥 String으로 찍어보면 -> 한글이 깨져보임.
//                    JSONObject 형태로 변환해서 -> 다시 String으로 바꿔보면 한글이 보임.
            val jsonObj = JSONObject(bodyString)

            Log.d("서버응답본문", jsonObj.toString())


//                    code값 추출 연습. -> 화면에서 분석해서, 토스트를 띄우는 등의 UI 처리.
//                    val code = jsonObj.getInt("code")
//
//                    Log.d("코드값", code.toString())


//                    받아낸 jsonObj를 통째로 -> 화면의 응답 처리 코드에 넘겨주자.
            handler?.onResponse(jsonObj)

        }
    })//request를 호출해주세요하는거
    //enqueue()==호출을 하고 돌아올 때 할 일
}





//        좋아요 / 싫어요 찍기
        fun postRequestLikeOrHate(context: Context, replyId: Int, isLike: Boolean, handler : JsonResponseHandler? ) {
//                                 토큰값을 받아와야해서 context필요함

//            1. 어디로(url) 갈것인가? HOST_URL + Endpoint
    val urlString = "${HOST_URL}/topic_reply_like"

//            2. 어떤 데이터를 들고 갈것인가? 파라미터
    val formData = FormBody.Builder()
        .add("reply_id", replyId.toString())
                   .add("is_like", isLike.toString())
        .build()

//            3. 어떤 방식으로 접근할 것인지 Request에 같이 적어주자.
//            모두 모아서 하나의 Request 정보로 만들어주자.
    val request = Request.Builder()
        .url(urlString)
        .post(formData)//짐을 넣을거면 여기다 넣어라
        .header("X-Http-Token", ContextUtil.getToken(context))
        .build()

//            만들어진 request를 실제로 호출해야함.
//            요청을 한다 -> 클라이언트의 역할. -> 앱이 클라이언트로 동작해야함.

    val client = OkHttpClient()

//            만들어진 요청 호출. => 응답이 왔을때 분석 / UI 반영
//             호출을 하면 -> 서버가 데이터 처리...--> 응답 받아서 처리 (처리할 코드를 등록)
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
//                    실패 == 서버 '연결' '자체를' 실패. (응답이 돌아오지 않은 경우에)
//                    ex) 비번 틀려서 로그인 실패 : 응답은 돌아왔는데 그 내용이 실패한 경우임( 응답은 된거 )
//                    인터넷 끊김, 서버 접속 불가 등 == 실패 O
        }

        override fun onResponse(call: Call, response: Response) {
//                    어떤 내용이든, 응답이 돌아온 경우. (로그인 성공, 실패 모두 응답)
//                    응답에 포함된 데이터들중 -> 본문(body)을 보자.

            val bodyString = response.body!!.string()
//                    본문을 그냥 String으로 찍어보면 -> 한글이 깨져보임.
//                    JSONObject 형태로 변환해서 -> 다시 String으로 바꿔보면 한글이 보임.
            val jsonObj = JSONObject(bodyString)

            Log.d("서버응답본문", jsonObj.toString())


//                    code값 추출 연습. -> 화면에서 분석해서, 토스트를 띄우는 등의 UI 처리.
//                    val code = jsonObj.getInt("code")
//
//                    Log.d("코드값", code.toString())


//                    받아낸 jsonObj를 통째로 -> 화면의 응답 처리 코드에 넘겨주자.
            handler?.onResponse(jsonObj)

        }
    })//request를 호출해주세요하는거
    //enqueue()==호출을 하고 돌아올 때 할 일
}





        //알림 갯수 or 목록까지 가져오기
        fun getRequestNotificationCountOrList(context : Context, needList : Boolean, handler: JsonResponseHandler?) {

            val url = "${HOST_URL}/notification".toHttpUrlOrNull()!!.newBuilder()
            url.addEncodedQueryParameter("need_all_notis", needList.toString())
//            url.addEncodedQueryParameter("value", value)//어디로 뭘 들고 간다

            val urlString = url.toString()
            Log.d("완성된URL", urlString)


            val request = Request.Builder()
                .url(urlString)
                .get()
                .header("X-Http-Token", ContextUtil.getToken(context))
                .build()

            val client = OkHttpClient()

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




//        어디까지 읽은 알림인지 서버에 알려주기
        fun postRequestNotificationRead(context: Context, notiId: Int, handler : JsonResponseHandler? ) {
//                                 토큰값을 받아와야해서 context필요함

//            1. 어디로(url) 갈것인가? HOST_URL + Endpoint
            val urlString = "${HOST_URL}/notification"

//            2. 어떤 데이터를 들고 갈것인가? 파라미터
            val formData = FormBody.Builder()
                .add("noti_id", notiId.toString())
//                .add("is_like", isLike.toString())
                .build()

//            3. 어떤 방식으로 접근할 것인지 Request에 같이 적어주자.
//            모두 모아서 하나의 Request 정보로 만들어주자.
            val request = Request.Builder()
                .url(urlString)
                .post(formData)//짐을 넣을거면 여기다 넣어라
                .header("X-Http-Token", ContextUtil.getToken(context))
                .build()

//            만들어진 request를 실제로 호출해야함.
//            요청을 한다 -> 클라이언트의 역할. -> 앱이 클라이언트로 동작해야함.

            val client = OkHttpClient()

//            만들어진 요청 호출. => 응답이 왔을때 분석 / UI 반영
//             호출을 하면 -> 서버가 데이터 처리...--> 응답 받아서 처리 (처리할 코드를 등록)
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
//                    실패 == 서버 '연결' '자체를' 실패. (응답이 돌아오지 않은 경우에)
//                    ex) 비번 틀려서 로그인 실패 : 응답은 돌아왔는데 그 내용이 실패한 경우임( 응답은 된거 )
//                    인터넷 끊김, 서버 접속 불가 등 == 실패 O
                }

                override fun onResponse(call: Call, response: Response) {
//                    어떤 내용이든, 응답이 돌아온 경우. (로그인 성공, 실패 모두 응답)
//                    응답에 포함된 데이터들중 -> 본문(body)을 보자.

                    val bodyString = response.body!!.string()
//                    본문을 그냥 String으로 찍어보면 -> 한글이 깨져보임.
//                    JSONObject 형태로 변환해서 -> 다시 String으로 바꿔보면 한글이 보임.
                    val jsonObj = JSONObject(bodyString)

                    Log.d("서버응답본문", jsonObj.toString())


//                    code값 추출 연습. -> 화면에서 분석해서, 토스트를 띄우는 등의 UI 처리.
//                    val code = jsonObj.getInt("code")
//
//                    Log.d("코드값", code.toString())


//                    받아낸 jsonObj를 통째로 -> 화면의 응답 처리 코드에 넘겨주자.
                    handler?.onResponse(jsonObj)

                }
            })//request를 호출해주세요하는거
            //enqueue()==호출을 하고 돌아올 때 할 일
        }





        //댓글에 답글 달기

        fun postRequestChileReply(context: Context, content : String, parentReply : Int, handler : JsonResponseHandler? ) {
//                                 토큰값을 받아와야해서 context필요함

//            1. 어디로(url) 갈것인가? HOST_URL + Endpoint
            val urlString = "${HOST_URL}/topic_reply"

//            2. 어떤 데이터를 들고 갈것인가? 파라미터
            val formData = FormBody.Builder()
                .add("content", content)
                .add("parent_reply_id", parentReply.toString())
                .build()

//            3. 어떤 방식으로 접근할 것인지 Request에 같이 적어주자.
//            모두 모아서 하나의 Request 정보로 만들어주자.
            val request = Request.Builder()
                .url(urlString)
                .post(formData)//짐을 넣을거면 여기다 넣어라
                .header("X-Http-Token", ContextUtil.getToken(context))
                .build()

//            만들어진 request를 실제로 호출해야함.
//            요청을 한다 -> 클라이언트의 역할. -> 앱이 클라이언트로 동작해야함.

            val client = OkHttpClient()

//            만들어진 요청 호출. => 응답이 왔을때 분석 / UI 반영
//             호출을 하면 -> 서버가 데이터 처리...--> 응답 받아서 처리 (처리할 코드를 등록)
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
//                    실패 == 서버 '연결' '자체를' 실패. (응답이 돌아오지 않은 경우에)
//                    ex) 비번 틀려서 로그인 실패 : 응답은 돌아왔는데 그 내용이 실패한 경우임( 응답은 된거 )
//                    인터넷 끊김, 서버 접속 불가 등 == 실패 O
                }

                override fun onResponse(call: Call, response: Response) {
//                    어떤 내용이든, 응답이 돌아온 경우. (로그인 성공, 실패 모두 응답)
//                    응답에 포함된 데이터들중 -> 본문(body)을 보자.

                    val bodyString = response.body!!.string()
//                    본문을 그냥 String으로 찍어보면 -> 한글이 깨져보임.
//                    JSONObject 형태로 변환해서 -> 다시 String으로 바꿔보면 한글이 보임.
                    val jsonObj = JSONObject(bodyString)

                    Log.d("서버응답본문", jsonObj.toString())


//                    code값 추출 연습. -> 화면에서 분석해서, 토스트를 띄우는 등의 UI 처리.
//                    val code = jsonObj.getInt("code")
//
//                    Log.d("코드값", code.toString())


//                    받아낸 jsonObj를 통째로 -> 화면의 응답 처리 코드에 넘겨주자.
                    handler?.onResponse(jsonObj)

                }
            })//request를 호출해주세요하는거
            //enqueue()==호출을 하고 돌아올 때 할 일
        }






//        댓글 상세 정보(답글 목록) 가져오기

        fun getRequestReplyDetail(context : Context, replyId: Int, handler: JsonResponseHandler?) {

    val url = "${HOST_URL}/topic_reply".toHttpUrlOrNull()!!.newBuilder()
//            주소/3 등 어떤 데이터를 보고싶은지, /숫자 형태로 이어붙이는 주소-> path라고 부름
//            주소?type=EMAIL 등 파라미터이름=값 형태로 이어붙이는 주소 -> Query라고 부름

    url.addPathSegment(replyId.toString())

//            url.addEncodedQueryParameter("type", type)
//    url.addEncodedQueryParameter("order_type", "NEW")//별개의 헤더에 담아서 담을거 없음.이제끝
//            url.addEncodedQueryParameter("value", value)//어디로 뭘 들고 간다

    val urlString = url.toString()
    Log.d("완성된URL", urlString)


    val request = Request.Builder()
        .url(urlString)
        .get()
        .header("X-Http-Token", ContextUtil.getToken(context))
        .build()

    val client = OkHttpClient()

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






//        로그인한 내 사용자 정보 가져오기
        fun getRequestUserData(context : Context, handler: JsonResponseHandler?) {

    val url = "${HOST_URL}/user_info".toHttpUrlOrNull()!!.newBuilder()//이거만바꿈
//            url.addEncodedQueryParameter("type", type)
//            url.addEncodedQueryParameter("value", value)//어디로 뭘 들고 간다

    val urlString = url.toString()
    Log.d("완성된URL", urlString)


    val request = Request.Builder()
        .url(urlString)
        .get()
        .header("X-Http-Token", ContextUtil.getToken(context))
        .build()

    val client = OkHttpClient()

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







//        댓글 삭제하기
        fun deleteRequestReply(context : Context, replyId : Int, handler: JsonResponseHandler?) {

          val url = "${HOST_URL}/topic_reply".toHttpUrlOrNull()!!.newBuilder()
          url.addEncodedQueryParameter("reply_id", replyId.toString())
//            url.addEncodedQueryParameter("value", value)//어디로 뭘 들고 간다

           val urlString = url.toString()
              Log.d("완성된URL", urlString)


    val request = Request.Builder()
        .url(urlString)
        .delete()
        .header("X-Http-Token", ContextUtil.getToken(context))
        .build()

    val client = OkHttpClient()

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








    }

}