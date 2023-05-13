package com.ssafy.smartstore.dto

import java.io.Serializable

data class OrderDto(
    var completed: String,
    var details: ArrayList<OrderDetailDto>,
    var orderTable: String,
    var orderTime: String,
    var stamp: StampDto,
    var userId: String,
) : Serializable {
    var id = -1

    constructor(
        completed: String,
        details: ArrayList<OrderDetailDto>,
        orderTable: String,
        orderTime: String,
        stamp: StampDto,
        userId: String,
        id: Int
    ) : this(completed, details, orderTable, orderTime, stamp, userId) {
        this.id = id
    }

    override fun toString(): String {
        return "completed: $completed, orderTable: $orderTable, orderTime: $orderTime, stamp: ${stamp.toString()}, userId: $userId, orderDetail: $details"
    }
}