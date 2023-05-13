package com.ssafy.smartstore.dto

import java.io.Serializable

data class CommentDto(
    var comment: String,
    var productId: Int,
    var rating: Double,
    var userId: String

    ) : Serializable {
    var id = -1

    constructor(id: Int, userId: String, productId: Int, rating: Double, comment: String) : this(
        userId,
        productId,
        rating,
        comment
    ) {
        this.id = id
    }

    override fun toString(): String {
        return "사용자ID : $userId | 상품ID : $productId | 별점 : $rating | 상품평 : $comment"
    }
}