package com.ssafy.smartstore.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.ssafy.smartstore.MainActivity
import com.ssafy.smartstore.R
import com.ssafy.smartstore.network.RetroApp
import com.ssafy.smartstore.user.LoginActivity


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        val sharedPreference = RetroApp
        val userId = sharedPreference.prefs.getString("userId", "")
        val userPassword = sharedPreference.prefs.getString("userPassward", "")

        val intent: Intent

        if (userId.isEmpty() && userPassword.isEmpty()) {
            intent = Intent(this, LoginActivity::class.java)
        } else {
            intent = Intent(this, MainActivity::class.java)
        }

        Handler().postDelayed({
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.horizon_enter, R.anim.none)
        }, DURATION)
    }

    companion object {
        private const val DURATION: Long = 2500
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}