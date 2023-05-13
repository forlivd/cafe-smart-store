package com.ssafy.smartstore.dao

import com.ssafy.smartstore.dto.CouponDto
import com.ssafy.smartstore.network.RetroApp
import com.ssafy.smartstore.service.CouponService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CouponDao {
    private val couponservice = RetroApp.retrofit.create(CouponService::class.java)

    suspend fun getCouponByUser(userId: String): List<CouponDto>? {
        var coupons: List<CouponDto>? = withContext(Dispatchers.IO) {
            val response = couponservice.getCouponByUser(userId).execute()
            val list = if (response.code() == 200) {
                var res = response.body() ?: arrayListOf()
                res
            } else {
                arrayListOf()
            }
            list
        }
        return coupons
    }

    suspend fun addCoupon(couponDto: CouponDto): Int {
        var result: Int = withContext(Dispatchers.IO) {
            val response = couponservice.addCoupon(couponDto).execute()
            val state = if (response.code() == 200) {
                var res = response.body() ?: -1
                res
            } else {
                -1
            }
            state
        }
        return result
    }
}