package com.ssafy.smartstore.util

import android.content.Context

class StoreSharedPreference(context: Context) {

    private val prefs = context.getSharedPreferences("store_pref", Context.MODE_PRIVATE)

    fun getString(key: String, defValue: String) : String {
        return prefs.getString(key, defValue).toString()
    }

    fun setString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

//    사용법
//    val sharedPreference = PrefApplication
//    PrefApplication.prefs.getString("email", "")
//    PrefApplication.prefs.setString("email", test@test.com")

}