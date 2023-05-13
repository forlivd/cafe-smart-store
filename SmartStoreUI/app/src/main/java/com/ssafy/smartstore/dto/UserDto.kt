package com.ssafy.smartstore.dto

import java.io.Serializable

data class UserDto (var id: String, var name: String, var pass: String) : Serializable {
    var stamps: Int = 0
    var stampList: MutableList<StampDto> = mutableListOf()

    constructor(id: String, name: String, pass: String, stamps: Int, stampList: MutableList<StampDto>): this(id, name, pass) {
        this.stamps = stamps
        this.stampList = stampList
    }

    override fun toString(): String {
        return "id $id, name $name, pass $pass, stamps $stamps"
    }
}
