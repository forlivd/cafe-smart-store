package com.ssafy.smartstore.coupon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.zxing.integration.android.IntentIntegrator
import com.ssafy.smartstore.MainActivity
import com.ssafy.smartstore.R
import com.ssafy.smartstore.dao.CouponDao
import com.ssafy.smartstore.databinding.ActivityCouponListBinding
import com.ssafy.smartstore.dto.CouponDto
import com.ssafy.smartstore.network.RetroApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class CouponListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCouponListBinding
    private lateinit var couponList: List<CouponDto>
    private lateinit var adapter: CouponListAdapter
    private lateinit var couponDao: CouponDao
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCouponListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ButtonInit()

        val sharedPreference = RetroApp
        userId = sharedPreference.prefs.getString("userId", "")

        couponDao = CouponDao()

        binding.couponList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        couponList = arrayListOf()


        updateList()

        binding.btnAddCoupon.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setPrompt("쿠폰의 QR코드를 인식시켜 주세요.")
            integrator.setOrientationLocked(true)
            integrator.setBarcodeImageEnabled(false)
            integrator.setBeepEnabled(false)
            integrator.initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            } else {
                val couponInfo = JSONObject(result.contents)
                var title = couponInfo.getString("title")
                var endDate = couponInfo.getString("endDate")
                var percent = couponInfo.getString("percent")
                var used = couponInfo.getString("used")
                CoroutineScope(Dispatchers.Main).launch {
                    var result = couponDao.addCoupon(CouponDto(userId, title, endDate, percent.toInt(), used))
                    Log.d("결과", result.toString())
                    Toast.makeText(this@CouponListActivity, "쿠폰 등록이 완료되었습니다.", Toast.LENGTH_LONG).show()
                    updateList()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun updateList() {
        CoroutineScope(Dispatchers.Main).launch {
            couponList = couponDao.getCouponByUser(userId)!!.toList()
            adapter = CouponListAdapter(this@CouponListActivity, R.layout.coupon_item, couponList)
            binding.couponList.adapter = adapter
            adapter.notifyDataSetChanged()
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