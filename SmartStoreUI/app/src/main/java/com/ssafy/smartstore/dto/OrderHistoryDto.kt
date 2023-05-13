package com.ssafy.smartstore.dto

import java.io.Serializable

data class OrderHistoryDto(
    var user_id: String,
    var order_table: String,
    var order_time: String,
    var completed: String,
    var product_id: Int,
    var quantity: Int
) : Serializable {
    var id = -1

    constructor(
        id: Int,
        user_id: String,
        order_table: String,
        order_time: String,
        completed: String,
        product_id: Int,
        quantity: Int
    ) : this(user_id, order_table, order_time, completed, product_id ,quantity) {
        this.id = id
    }

    override fun toString(): String {
        return "유저ID : $user_id | 상세 테이블 : $order_table | 주문완료 : $completed"
    }
}