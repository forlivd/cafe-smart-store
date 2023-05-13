package com.ssafy.smartstore.util

import android.app.Application

class PrefApplication: Application() {
    companion object {
        lateinit var prefs: StoreSharedPreference
    }

    override fun onCreate() {
        prefs = StoreSharedPreference(applicationContext)
        super.onCreate()
    }
}