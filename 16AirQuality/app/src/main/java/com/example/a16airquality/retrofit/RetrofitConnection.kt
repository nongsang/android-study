package com.example.a16airquality.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitConnection {
    // 싱글턴으로 만드는 레트로핏 생성기 클래스
    companion object {
        private const val BASE_URL = "https://api.airvisual.com/v2/"
        private var INSTANCE: Retrofit? = null

        fun getInstance() : Retrofit {
            if (INSTANCE == null) {
                INSTANCE = Retrofit.Builder()
                    .baseUrl(BASE_URL)  // 기본 URL 설정
                    .addConverterFactory(GsonConverterFactory.create()) // 서버에서 온 JSON 응답을 레트로핏에서 사용하는 GSON으로 변환하는 컨버터 설정
                    .build()
            }

            return INSTANCE!!
        }
    }

}