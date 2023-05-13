package com.ssafy.smartstore.service

import com.ssafy.smartstore.dto.UserDto
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    //사용자 정보 추가
    @POST("rest/user")
    fun setUser(@Body body: UserDto): Call<Boolean>

    //사용자의 정보와 함께 사용자의 주문 내역, 사용자 등급 정보 반환
    @POST("rest/user/info?id={user_id}")
    fun getUserInfo(@Path("user_id") user_id: String): Call<UserDto>

    //전달된 ID가 이미 사용중인지 반환
    @GET("rest/user/isUsed")
    fun getIsUsed(@Query("id") user_id: String): Call<Boolean>

    //로그인 성공시 해당 유저의 id, name, pass, stampList, stamps 반환
    @POST("rest/user/login")
    fun getLogin(@Body body: UserDto): Call<UserDto>

    @GET("rest/user/stamp/{userId}")
    fun getStampByUser(@Query("userId") userId: String): Call<Int>
}