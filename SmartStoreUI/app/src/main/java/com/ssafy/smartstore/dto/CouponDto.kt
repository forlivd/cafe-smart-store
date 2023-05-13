package com.ssafy.smartstore.dto

import java.io.Serializable

data class CouponDto(var userId: String, var title: String, var endDate: String, var percent: Int, var used: String) : Serializable{
    var id = -1

    constructor(id: Int, userId: String, title: String, endDate: String, percent: Int, used: String) : this(userId, title, endDate, percent, used){
        this.id = id
    }

    override fun toString(): String {
        return "userId: $userId, Title: $title, EndDate: $endDate, Percent: $percent, Used: $used"
    }
}