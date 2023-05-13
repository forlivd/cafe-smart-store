package com.ssafy.smartstore.dao

import android.util.Log
import com.ssafy.smartstore.dto.OrderDetailDto
import com.ssafy.smartstore.dto.OrderDto
import com.ssafy.smartstore.network.RetroApp
import com.ssafy.smartstore.service.OrderService
import com.ssafy.smartstore.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderDao {
    private val orderService: OrderService = RetroApp.retrofit.create(OrderService::class.java)

    // 주문 정보 저장, 저장된 주문 id 반환
    suspend fun addOrder(order: OrderDto): Int {
        var result: Int = withContext(Dispatchers.IO) {
            val response = orderService.setOrder(order).execute()
            val state = if (response.code() == 200) {
                var res = response.body() ?: -1
                res
            } else {
                -1
            }
            state
        }
        return result
    }// 기본값 -1

    // 주문 아이디에 대한 상세 주문 정보 반환
    suspend fun getOrderDetail(orderId: Int): List<Map<String, Any>>? {
        var orderDetailList: List<Map<String, Any>>? = withContext(Dispatchers.IO) {
            val response = orderService.getOrderDetail(orderId).execute()
            val list = if (response.code() == 200) {
                var res = response.body() ?: arrayListOf()
                res
            } else {
                arrayListOf()
            }
            list
        }
        return orderDetailList
    }

    // 유저 아이디에 대한 주문 기록 반환
    suspend fun getOrderHistory(userId: String): List<Map<String, Any>>? {
        var orderHistory: List<Map<String, Any>>? = withContext(Dispatchers.IO) {
            val response = orderService.getMonthOrder(userId).execute()
            val list = if (response.code() == 200) {
                var res = response.body() ?: arrayListOf()
                res
            } else {
                arrayListOf()
            }
            list
        } // 기본값 빈 값
        Log.d("확인", orderHistory.toString())
        return orderHistory
    }

    suspend fun getAllOrder(): List<OrderDetailDto>? {
        var orderList: List<OrderDetailDto>? = withContext(Dispatchers.IO) {
            val response = orderService.getAllOrder().execute()
            val list = if (response.code() == 200) {
                var res = response.body() ?: arrayListOf()
                res
            } else {
                arrayListOf()
            }
            list
        }
        return orderList
    }

}