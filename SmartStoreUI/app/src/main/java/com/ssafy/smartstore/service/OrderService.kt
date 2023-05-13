package com.ssafy.smartstore.service

import com.ssafy.smartstore.dto.OrderDetailDto
import com.ssafy.smartstore.dto.OrderDto
import retrofit2.Call
import retrofit2.http.*

interface OrderService {
    //order 객체를 저장하고 추가된 Order의 id를 반환
    @POST("rest/order")
    fun setOrder(@Body body: OrderDto): Call<Int>

    //orderId에 해당하는 주문의 상세내역을 List로 반환
    @GET("rest/order/{orderId}")
    fun getOrderDetail(@Path("orderId") orderId: Int): Call<List<Map<String, Any>>>

    @GET("rest/order/byUser")
    fun getMonthOrder(@Query("id") id: String): Call<List<Map<String, Any>>>

    @GET("rest/order/all")
    fun getAllOrder(): Call<List<OrderDetailDto>>
}