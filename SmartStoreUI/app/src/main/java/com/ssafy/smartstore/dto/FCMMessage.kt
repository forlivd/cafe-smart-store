package com.ssafy.smartstore.dto

import java.io.Serializable
import java.sql.Timestamp

data class FCMMessage(var title: String, var body: String, var timestamp: Long) : Serializable{
    override fun toString(): String {
        return "title: $title, body: $body, timestamp: $timestamp"
    }
}