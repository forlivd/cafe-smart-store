package com.ssafy.smartstore.dto

import java.io.Serializable

data class ProductDto  ( var img: String, var name:String, var price: Int, var type: String) :
    Serializable {

    var id: Int = -1;

    constructor(id: Int, img: String, name: String, price: Int, type: String): this(img, name, price, type) {
        this.id = id
    }

    override fun toString(): String {
        return "id $id, name $name, type $type, price $price, img $img"
    }
}