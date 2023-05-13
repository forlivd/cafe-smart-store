package com.ssafy.smartstore.order

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.smartstore.MainActivity
import com.ssafy.smartstore.MapActivity
import com.ssafy.smartstore.R
import com.ssafy.smartstore.dao.OrderDao
import com.ssafy.smartstore.dao.ProductDao
import com.ssafy.smartstore.databinding.ActivityMenuListBinding
import com.ssafy.smartstore.dto.OrderDetailDto
import com.ssafy.smartstore.dto.ProductDto
import com.ssafy.smartstore.menu.MenuDetailActivity
import com.ssafy.smartstore.reviews.AddReviewActivity
import com.ssafy.smartstore.reviews.ReviewActivity
import com.ssafy.smartstore.reviews.ReviewAdapter
import com.ssafy.smartstore.shoppinglist.ShoppingListActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.*

private const val TAG = "메뉴리스트"

class pRank : Comparable<pRank> {
    var pId: Int = 0
    var cnt: Int = 0

    constructor(pId: Int, cnt: Int) {
        this.pId = pId
        this.cnt = cnt
    }

    override fun compareTo(other: pRank): Int {
        return Integer.compare(cnt, other.cnt)
    }

    override fun toString(): String {
        return "pId : $pId, cnt : $cnt"
    }
}

class MenuListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuListBinding

    // List
    private var productList: List<ProductDto> = arrayListOf()
    private var orderList: List<OrderDetailDto> = arrayListOf()
    private var shoppingList: ArrayList<OrderDetailDto> = arrayListOf()

    // Dao
    private var productDao = ProductDao()
    private var orderDao = OrderDao()

    // Adapter
    private lateinit var adapter: MenuListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로가기, 홈, 전화걸기, 리뷰, 지도 버튼 초기화
        ButtonInit()

        // 전체메뉴 RecyclerView 설정
        binding.rcMenu.apply {
            layoutManager =
                LinearLayoutManager(this@MenuListActivity, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                DividerItemDecoration(
                    this@MenuListActivity,
                    LinearLayoutManager.VERTICAL
                )
            )
        }

        // TOP3 메뉴 RecyclerView 설정
//        binding.rcTopMenu.apply {
//            layoutManager =
//                LinearLayoutManager(this@MenuListActivity, LinearLayoutManager.VERTICAL, false)
//            addItemDecoration(
//                DividerItemDecoration(
//                    this@MenuListActivity,
//                    LinearLayoutManager.VERTICAL
//                )
//            )
//        }

        // 서버에서 가져온 모든 Product 정보를 Adapter와 연결
        CoroutineScope(Dispatchers.Main).launch {
            // 전체메뉴
            productList = productDao.getAllProduct()!!.toList().reversed()
            adapter = MenuListAdapter(R.layout.menu_item, productList)
            // 전체메뉴 RecyclerView 설정
            binding.rcMenu.adapter = adapter
            adapter.notifyDataSetChanged()

            // RecyclerView Item 클릭 이벤트
            var itemClickListener = object : MenuListAdapter.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    Intent(this@MenuListActivity, MenuDetailActivity::class.java).apply {
                        var selectedItem = productList[position]
                        putExtra("selectedId", selectedItem.id)
                        menuDetailLauncher.launch(this)
                    }
                }
            }
            adapter.onItemClickListener = itemClickListener

            // TOP3 메뉴
            orderList = orderDao.getAllOrder()!!.toList().reversed()
            var orderCnt = Array<pRank?>(11) { null }
            for (i in orderCnt.indices) {
                orderCnt[i] = pRank(i, 0)
            }
            for (i in orderList) {
                orderCnt[i.productId]!!.cnt++
            }
            Arrays.sort(orderCnt, Collections.reverseOrder())
            var topList: ArrayList<ProductDto> = arrayListOf()
            for (k in productList) {
                if (orderCnt[0]!!.pId == k.id)
                    topList.add(ProductDto(k.img, k.name, k.price, k.type))
            }

//            adapter = MenuListAdapter(R.layout.menu_item, topList)
//            binding.rcTopMenu.adapter = adapter
//            adapter.notifyDataSetChanged()

            // 카페 설명말
            binding.storeSubTitle.text = "${topList[0].name}가 맛있는 카페"
        }


        // 장바구니 이동
        binding.btnMoveShoppinglist.setOnClickListener {
            Intent(this, ShoppingListActivity::class.java).apply {
                putExtra("shoppingList", shoppingList)
                shoppingListLauncher.launch(this)
                overridePendingTransition(R.anim.horizon_enter, R.anim.none)
            }
        }
    }

    private val shoppingListLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val intent = it.data
            var flag = intent!!.getStringExtra("flag")
            // 주문을 했다면 장바구니에 있던 것들 삭제
            if (flag.equals("Y")) {
                shoppingList.clear()
            }
        }
    }

    private val menuDetailLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val intent = it.data
            val shoppingItem: OrderDetailDto =
                intent!!.getSerializableExtra("Item") as OrderDetailDto
            if (shoppingItem != null) {
                shoppingList.add(shoppingItem)
            }
            Log.d(TAG, shoppingList.toString())
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

        // 전화걸기
        binding.btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:010-1234-5678")
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }

        // 리뷰 보기
        binding.btnReview.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.horizon_enter, R.anim.none)
        }

        // 현재위치, 길찾기
        binding.btnMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.horizon_enter, R.anim.none)
        }
    }


}