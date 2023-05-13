package com.ssafy.smartstore

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ssafy.smartstore.coupon.CouponListActivity
import com.ssafy.smartstore.dao.UserDao
import com.ssafy.smartstore.databinding.ActivityMyPageBinding
import com.ssafy.smartstore.network.RetroApp
import com.ssafy.smartstore.order.OrderListActivity
import com.ssafy.smartstore.user.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyPageBinding

    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buttonInit()

        //저장해 둔 유저 아이디 가져오기
        val sharedPreference = RetroApp
        val userId = sharedPreference.prefs.getString("userId", "")
        val userName = sharedPreference.prefs.getString("userName", "")

        userDao = UserDao()

        binding.myUserName.text = userName
        binding.tvMyUserId.text = userId

        CoroutineScope(Dispatchers.Main).launch {
            var stampCnt = userDao.getStampCount(userId)
            binding.tvMyUserStamp.text = stampCnt.toString() + "개"
            binding.progressbar.progress = stampCnt
            Log.d("마이페이지", stampCnt.toString())
            setLevel(stampCnt)
        }

        binding.btnCouponList.setOnClickListener {
            val intent = Intent(this, CouponListActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.horizon_enter, R.anim.none)
        }

        // 주문내역 이동 버튼
        binding.btnOrderList.setOnClickListener {
            val intent = Intent(this, OrderListActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.horizon_enter, R.anim.none)
        }

        // 로그아웃 버튼
        binding.btnLogout.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)

            mDialogView.findViewById<TextView>(R.id.dialog_title).text = "로그아웃 하시겠습니까?"

            val mAlertDialog = mBuilder.show()

            mDialogView.findViewById<Button>(R.id.Confirm).setOnClickListener {
                sharedPreference.prefs.setString("userId", "")
                sharedPreference.prefs.setString("userPassward", "")
                sharedPreference.prefs.setString("userName", "")
                val intent = Intent(this@MyPageActivity, LoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }

            mDialogView.findViewById<Button>(R.id.Cancel).setOnClickListener {
                mAlertDialog.dismiss()
            }
        }

        // 쿠폰 이동 버튼
        binding.btnCouponList.setOnClickListener {
            val intent = Intent(this, CouponListActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.horizon_enter, R.anim.none)
        }
    }

    private fun buttonInit() {
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

    private fun setLevel(stampCnt: Int) {
        if (stampCnt < 10) {
            binding.imgSeed.setImageResource(R.drawable.coffee_tree)
            binding.tvGrade.text = "나무"
            binding.tvGradePer.text = "$stampCnt/10"
            binding.tvGradeMention.text = "다음 레벨까지 ${10 - stampCnt}잔 남았습니다."
            binding.progressbar.max = 10
            binding.progressbar.progress = stampCnt
        }

        // 씨앗 단계(10, 20, 30, 40, 50)
        else if (stampCnt in 10..64) {
            binding.imgSeed.setImageResource(R.drawable.seeds)
            if (stampCnt in 10..49) {
                binding.tvGrade.text = "씨앗 ${stampCnt / 10}단계"
                binding.tvGradePer.text = "${stampCnt % 10}/10"
                binding.tvGradeMention.text =
                    "다음 레벨까지 ${((stampCnt / 10) + 1) * 10 - stampCnt}잔 남았습니다."
                binding.progressbar.max = 10
                binding.progressbar.progress = stampCnt % 10
            } else if (stampCnt in 50..64) {
                binding.tvGrade.text = "씨앗 5단계"
                binding.tvGradePer.text = "${stampCnt - 50}/15"
                binding.tvGradeMention.text = "다음 레벨까지 ${65 - stampCnt}잔 남았습니다."
                binding.progressbar.max = 15
                binding.progressbar.progress = (stampCnt - 50) % 15
            }
        }

        // 꽃 단계(65, 80, 95, 110, 125)
        else if (stampCnt in 65..144) {
            binding.imgSeed.setImageResource(R.drawable.flower)
            if (stampCnt in 65..124) {
                binding.tvGrade.text = "꽃 ${(stampCnt - 50) / 15}단계}"
                binding.tvGradePer.text = "${(stampCnt - 50) % 15}/15"
                binding.tvGradeMention.text =
                    "다음 레벨까지 ${((stampCnt - 50) / 15 + 1) * 15 - (stampCnt - 50)}잔 남았습니다.}"
                binding.progressbar.max = 15
                binding.progressbar.progress = (stampCnt - 50) % 15
            } else if (stampCnt in 125..144) {
                binding.tvGrade.text = "꽃 5단계"
                binding.tvGradePer.text = "${stampCnt - 125}/20"
                binding.tvGradeMention.text = "다음 레벨까지 ${145 - stampCnt}잔 남았습니다."
                binding.progressbar.max = 20
                binding.progressbar.progress = (stampCnt - 125) % 20
            }
        }

        // 열매 단계(145, 165, 185, 205, 225)
        else if (stampCnt in 145..249) {
            binding.imgSeed.setImageResource(R.drawable.coffee_fruit)
            if (stampCnt in 145..224) {
                binding.tvGrade.text = "열매 ${(stampCnt - 125) / 20}단계"
                binding.tvGradePer.text = "${(stampCnt - 145) % 20}/20"
                binding.tvGradeMention.text =
                    "다음 레벨까지 ${((stampCnt - 125) / 20 + 1) * 20 - (stampCnt - 125)}잔 남았습니다."
                binding.progressbar.max = 20
                binding.progressbar.progress = (stampCnt - 125) % 20
            } else if (stampCnt in 225..249) {
                binding.tvGrade.text = "열매 5단계"
                binding.tvGradePer.text = "${stampCnt - 225}/25"
                binding.tvGradeMention.text = "다음 레벨까지 ${224 - stampCnt}잔 남았습니다."
                binding.progressbar.max = 25
                binding.progressbar.progress = (stampCnt - 145) % 20
            }
        }

        // 커피콩 단계(250, 275, 300, 325, 350)
        else if (stampCnt in 250..349) {
            binding.imgSeed.setImageResource(R.drawable.coffee_beans)
            binding.tvGrade.text = "커피콩 ${(stampCnt - 225) / 25}단계"
            binding.tvGradePer.text = "${(stampCnt - 250) % 25}/25"
            binding.tvGradeMention.text =
                "다음 레벨까지 ${((stampCnt - 225) / 25 + 1) * 25 - (stampCnt - 225)}잔 남았습니다."
            binding.progressbar.max = 25
            binding.progressbar.progress = (stampCnt - 225) % 25
        } else {
            binding.imgSeed.setImageResource(R.drawable.coffee_beans)
            binding.tvGrade.text = "커피콩 5단계"
            binding.tvGradePer.text = "25/25"
            binding.tvGradeMention.text = "최고 레벨입니다!!"
            binding.progressbar.max = 25
            binding.progressbar.progress = 25
        }
    }
}