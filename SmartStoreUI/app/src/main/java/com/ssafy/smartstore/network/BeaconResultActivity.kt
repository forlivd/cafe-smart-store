package com.ssafy.smartstore.network

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.ssafy.smartstore.MainActivity
import com.ssafy.smartstore.R

class BeaconResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beacon_result)

        val btn = findViewById<Button>(R.id.bt1)

        btn.setOnClickListener {
            finish()
        }
    }
}