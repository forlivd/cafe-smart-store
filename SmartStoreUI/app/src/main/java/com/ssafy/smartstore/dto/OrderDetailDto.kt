package com.ssafy.smartstore.dto

import java.io.Serializable

data class OrderDetailDto(var orderId: Int, var productId: Int, var quantity: Int) :
    Serializable {
    var id = -1

    constructor(id: Int, orderId: Int, productId: Int, quantity: Int) : this(
        orderId,
        productId,
        quantity
    ) {
        this.id = id
    }

    override fun toString(): String {
        return "주문ID : $orderId | 상품ID : $productId | 수량 : $quantity"
    }
}