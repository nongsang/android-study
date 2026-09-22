package com.example.a16airquality.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AirQualityService {
    // 서버로 요청하는 기능

    // nearest_city 상대 URL을 붙여서 GET으로 전송
    @GET("nearest_city")
    // lat 파라미터, lon 파라미터, key 파라미터를 넣어서 서버로 전송
    // 받는 데이터 클래스는 AirQualityResponse
    fun getAirQualityData(@Query("lat") lat: String, @Query("lon") lon: String, @Query("key") key: String): Call<AirQualityResponse>
}