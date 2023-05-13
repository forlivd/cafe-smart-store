package com.ssafy.smartstore.order

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.smartstore.MainActivity
import com.ssafy.smartstore.OrderListAdapter
import com.ssafy.smartstore.R
import com.ssafy.smartstore.dao.OrderDao
import com.ssafy.smartstore.dao.UserDao
import com.ssafy.smartstore.databinding.ActivityOrderListBinding
import com.ssafy.smartstore.dto.UserDto
import com.ssafy.smartstore.network.RetroApp
import com.ssafy.smartstore.user.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable
import kotlin.properties.Delegates

class OrderListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderListBinding
    private lateinit var orderDao: OrderDao
    private lateinit var userDao: UserDao
    private lateinit var user: UserDto
    private lateinit var listViewItems: MutableList<MutableMap<String, Any>>
    private lateinit var adapter: OrderListAdapter

    private lateinit var userId: String
    private lateinit var userPassword: String
    private lateinit var userName: String

    private var flag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ButtonInit()

        binding.listView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.listView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )

        userDao = UserDao()

        val sharedPreference = RetroApp
        userId = sharedPreference.prefs.getString("userId", "")
        userPassword = sharedPreference.prefs.getString("userPassward", "")
        userName = sharedPreference.prefs.getString("userName", "")
        var userTitle = SpannableString("${userName}님의 주문내역입니다.")
        userTitle.setSpan(
            ForegroundColorSpan(Color.parseColor("#000000")),
            0,
            3,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        userTitle.setSpan(RelativeSizeSpan(1.3f), 0, 3, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvUserTitle.text = userTitle
        CoroutineScope(Dispatchers.Main).launch {
            updateListView()
        }
    }

    private fun updateListView() {
        listViewItems = mutableListOf()
        orderDao = OrderDao()
        //userid 기반 주문 목록 불러와서 listview에 넣기
        CoroutineScope(Dispatchers.Main).launch {
            user = userDao.getUserByIdPassword(userId, userPassword)!!
            var orderDtails = orderDao.getOrderHistory(user.id)!!

            for (i in orderDtails) {
                if (listViewItems.size == 0) {
                    listViewItems.add(mutableMapOf("o_id" to i["o_id"]!!, "user_id" to user.id))
                } else if (i.get("o_id") == listViewItems[listViewItems.size - 1]["o_id"]) {
                    continue
                } else if (i.get("o_id") != listViewItems[listViewItems.size - 1]["o_id"]) {
                    listViewItems.add(mutableMapOf("o_id" to i["o_id"]!!, "user_id" to user.id))
                }
            }

            // Adapter 생성
            adapter =
                OrderListAdapter(this@OrderListActivity, R.layout.order_list_item, listViewItems)
            binding.listView.adapter = adapter
            adapter.notifyDataSetChanged()

            flag = true
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

    override fun onResume() {
        super.onResume()
        if (flag) {
            CoroutineScope(Dispatchers.Main).launch {
                updateListView()
            }
        }
    }
}

