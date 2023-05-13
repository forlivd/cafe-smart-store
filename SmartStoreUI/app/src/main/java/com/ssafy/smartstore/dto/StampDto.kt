package com.ssafy.smartstore.dto

import java.io.Serializable

data class StampDto (var orderId:Int, var quantity: Int, var userId: String) :
    Serializable {
    var id = -1;

    constructor(id: Int, orderId:Int,quantity: Int, userId: String) :
            this(orderId, quantity, userId) {
        this.id = id
    }

    override fun toString(): String {
        return "id $id, user_id $userId, order_id $orderId, quantity $quantity"
    }
}
