package com.ssafy.smartstore.service

import com.ssafy.smartstore.dto.CouponDto
import com.ssafy.smartstore.dto.OrderDetailDto
import com.ssafy.smartstore.dto.OrderDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CouponService {
    @GET("rest/coupon/{userId}")
    fun getCouponByUser(@Path("userId") userId: String): Call<List<CouponDto>>

    @POST("rest/coupon")
    fun addCoupon(@Body body: CouponDto): Call<Int>
}