package com.ssafy.smartstore.alarm

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.smartstore.MainActivity
import com.ssafy.smartstore.R
import com.ssafy.smartstore.databinding.ActivityAlarmBinding
import com.ssafy.smartstore.dto.FCMMessage
import com.ssafy.smartstore.network.RetroApp
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class AlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmBinding
    private lateinit var messages: ArrayList<FCMMessage>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ButtonInit()

        // 메세지 저장할 ArrayList
        messages = arrayListOf()
        updateList()

        binding.btnRefresh.setOnClickListener {
            messages.clear()
            updateList()
        }
    }

    private fun updateList() {
        // sharedpreference에서 JSON형태로 저장된 메세지들 가져오기
        val sharedPreference = RetroApp
        var jsonString = sharedPreference.prefs.getString("messageList", "none")
        // JSON Parsing
        if (jsonString != "none") {
            val jsonObject = JSONArray(jsonString)
            try {
                for (i in 0 until jsonObject.length()) {
                    var title = jsonObject.getJSONObject(i)["title"]
                    var body = jsonObject.getJSONObject(i)["body"]
                    var timestamp = jsonObject.getJSONObject(i)["timestamp"]
                    messages.add(
                        FCMMessage(
                            title.toString(),
                            body.toString(),
                            timestamp.toString().toLong()
                        )
                    )
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            messages.reverse()

            binding.rcAlarm.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            val adapter = AlarmAdapter(this, R.layout.alarm_item, messages)
            binding.rcAlarm.adapter = adapter
        }
    }

    private fun ButtonInit() {
        // 뒤로가기 버튼
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.none, R.anim.horizon_exit)
        }

        // 홈버튼
        val btnHome = findViewById<ImageButton>(R.id.btnHome)
        btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.horizon_enter, R.anim.none)
        }
    }
}