package com.ssafy.smartstore.menu

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.smartstore.MainActivity
import com.ssafy.smartstore.R
import com.ssafy.smartstore.dao.OrderDao
import com.ssafy.smartstore.dao.ProductDao
import com.ssafy.smartstore.databinding.ActivityMenuDetailBinding
import com.ssafy.smartstore.dto.OrderDetailDto
import com.ssafy.smartstore.network.RetroApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.roundToInt

private const val TAG = "MenuDetail_싸피"

class MenuDetailActivity : AppCompatActivity() {
    private lateinit var ratingDialog: Dialog
    private lateinit var binding: ActivityMenuDetailBinding
    private var productInfo: List<Map<String, Any>> = arrayListOf()
    private var productDao = ProductDao()
    private var orderDao = OrderDao()
    val dec = DecimalFormat("#,###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        Log.d("메뉴리스트", intent.getIntExtra("selectedId", 0).toString())

        // 메뉴 이름, 가격, 평점, 별점, 상품평 등록
        var productId = intent.getIntExtra("selectedId", 0)
        CoroutineScope(Dispatchers.Main).launch {
            productInfo = productDao.getProduct(productId)!!.toList()
            binding.imgCoffee.setImageResource(getResources(productInfo[0]["img"].toString()))
            binding.tvCoffeeName.text = productInfo[0]["name"].toString()
            binding.tvCoffeePrice.text = dec.format(productInfo[0]["price"].toString().toDouble().roundToInt()).toString() + "원"
            var avg = 0.0
            if (productInfo[0]["avg"] != null)
                avg = productInfo[0]["avg"].toString().toDouble()

            val dec = DecimalFormat("#.#")
            binding.tvRatingAvg.text = "평점 : ${dec.format(avg)}"
            binding.rbAvgStar.rating = avg.roundToInt().toFloat()
        }

        binding.btnPlus.setOnClickListener {
            var amount = binding.etCoffeeAmount.text.toString().toInt()
            binding.etCoffeeAmount.setText((amount + 1).toString())
        }

        binding.btnMinus.setOnClickListener {
            var amount = binding.etCoffeeAmount.text.toString().toInt()
            if (amount > 0)
                binding.etCoffeeAmount.setText((amount - 1).toString())
        }

        // 담기 버튼
        binding.btnPut.setOnClickListener {
            var amount = binding.etCoffeeAmount.text.toString().toInt()
            if (amount > 0) {
                Toast.makeText(this@MenuDetailActivity, "장바구니에 메뉴를 추가했습니다.", Toast.LENGTH_SHORT)
                    .apply {
                        setGravity(Gravity.CENTER, 50, 50)
                        show()
                    }
                Intent(this, MenuDetailActivity::class.java).apply {
                    CoroutineScope(Dispatchers.Main).launch {
                        var AllOrderList = orderDao.getAllOrder()!!.toList().reversed()
                        var lastOrderId = AllOrderList[AllOrderList.lastIndex].orderId + 1

                        val shoppingItem = OrderDetailDto(
                            lastOrderId,
                            productId,
                            Integer.parseInt(binding.etCoffeeAmount.text.toString())
                        )
                        putExtra("Item", shoppingItem)
                        Log.d("MenuDetailActivity_싸피", "OrderActivty로 넘어가는 값 : $shoppingItem")
                        setResult(Activity.RESULT_OK, this@apply)
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "수량을 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getResources(resName: String): Int {
        var resId: Int

        when (resName) {
            "coffee1.png" -> resId = R.drawable.coffee1
            "coffee2.png" -> resId = R.drawable.coffee2
            "coffee3.png" -> resId = R.drawable.coffee3
            "coffee4.png" -> resId = R.drawable.coffee4
            "coffee5.png" -> resId = R.drawable.coffee5
            "coffee6.png" -> resId = R.drawable.coffee6
            "coffee7.png" -> resId = R.drawable.coffee7
            "coffee8.png" -> resId = R.drawable.coffee8
            "coffee9.png" -> resId = R.drawable.coffee9
            else -> resId = R.drawable.cookie
        }

        return resId
    }
}