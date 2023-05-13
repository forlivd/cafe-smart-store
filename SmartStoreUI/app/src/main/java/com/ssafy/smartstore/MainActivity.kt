package com.ssafy.smartstore

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ssafy.smartstore.alarm.AlarmActivity
import com.ssafy.smartstore.databinding.ActivityMainBinding
import com.ssafy.smartstore.network.RetroApp
import com.ssafy.smartstore.order.MenuListActivity
import com.ssafy.smartstore.order.OrderListActivity
import com.ssafy.smartstore.user.LoginActivity


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var backKeyPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreference = RetroApp
        var userName = sharedPreference.prefs.getString("userName", "")
        var nameString = SpannableString("$userName 님")
        nameString.setSpan(
            ForegroundColorSpan(Color.parseColor("#000000")),
            0,
            3,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        nameString.setSpan(RelativeSizeSpan(1.3f), 0, 3, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.name.text = nameString

        // 주문하기
        binding.orderCard.setOnClickListener {
            val intent = Intent(this, MenuListActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.horizon_enter, R.anim.none)
        }

        // 주문내역
        binding.orderListCard.setOnClickListener {
            val intent = Intent(this, OrderListActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.horizon_enter, R.anim.none)
        }

        // 통계
        binding.analyticsCard.setOnClickListener {
            val intent = Intent(this, AnalyticsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.horizon_enter, R.anim.none)
        }

        // 마이페이지
        binding.mypageCard.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.horizon_enter, R.anim.none)
        }

        // 설정
        binding.alarmCard.setOnClickListener {
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.horizon_enter, R.anim.none)
        }

        // 로그아웃
        // sharedPreference에 있는 값 초기화
        binding.logoutCard.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)

            mDialogView.findViewById<TextView>(R.id.dialog_title).text = "로그아웃 하시겠습니까?"

            val mAlertDialog = mBuilder.show()
            mAlertDialog.show()

            mDialogView.findViewById<Button>(R.id.Confirm).setOnClickListener {
                sharedPreference.prefs.setString("userId", "")
                sharedPreference.prefs.setString("userPassward", "")
                sharedPreference.prefs.setString("userName", "")
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }

            mDialogView.findViewById<Button>(R.id.Cancel).setOnClickListener {
                mAlertDialog.dismiss()
            }
        }
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish()
        }
    }
}