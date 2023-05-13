package com.ssafy.smartstore.order

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.MainActivity
import com.ssafy.smartstore.R
import com.ssafy.smartstore.dao.OrderDao
import com.ssafy.smartstore.dao.ProductDao
import com.ssafy.smartstore.databinding.ActivityOrderDetailBinding
import com.ssafy.smartstore.dto.OrderDetailDto
import com.ssafy.smartstore.dto.OrderDto
import com.ssafy.smartstore.dto.OrderHistoryDto
import com.ssafy.smartstore.dto.StampDto
import com.ssafy.smartstore.network.RetroApp
import com.ssafy.smartstore.shoppinglist.ShoppingListActivity
import com.ssafy.smartstore.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val TAG = "OrderDetailActivity_싸피"

class OrderDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var pIntent: PendingIntent
    private lateinit var filters: Array<IntentFilter>
    private var tableId: String = ""
    private lateinit var listView: RecyclerView
    private lateinit var listViewItems: MutableList<Map<String, Any>>
    private lateinit var orderDao: OrderDao
    private lateinit var productDao: ProductDao

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ButtonInit()

        var o_id: String = intent.getStringExtra("o_id")!!
        var orderDetails = intent.getSerializableExtra("dto") as List<Map<String, Any>>

        orderDao = OrderDao()
        productDao = ProductDao()

        val sharedPreference = RetroApp
        val userId = sharedPreference.prefs.getString("userId", "")

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }

        val intent = Intent(this, OrderDetailActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        pIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val ndef_filter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        ndef_filter.addDataType("text/plain")
        filters = arrayOf(ndef_filter)

        //상단 컴포넌트
        val orderStateTv: TextView = findViewById(R.id.tvOrderState)
        val orderDateTv: TextView = findViewById(R.id.tvOrderDate)
        val totalPriceTv: TextView = findViewById(R.id.tvPrice)


        var rightOrder: MutableList<Map<String, Any>> = mutableListOf()

        var state = "Y"
        var totalPrice = 0.0
        var date = "22.05.14"

        for (i in orderDetails) {
            if (i["o_id"].toString() == o_id) {
                rightOrder.add(i)
                totalPrice += (i["quantity"] as Double * i["price"] as Double)
                state = i["completed"].toString()
                date = i["order_time"].toString()
                Log.d("날짜", date)
            }
        }

        if (state == "N") {
            orderStateTv.text = "완료"
        } else {
            orderStateTv.text = "미완"
        }

        var token = date.chunked(10)
        val dec = DecimalFormat("#,###")

        orderDateTv.text = token[0]
        totalPriceTv.text = dec.format(totalPrice).toString() + " 원";

        listViewItems = rightOrder

        //리사이클러 뷰 세팅
        listView = findViewById(R.id.listView)
        listView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )

        binding.btnReOrder.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)

            mDialogView.findViewById<TextView>(R.id.dialog_title).text = "테이블 NFC를 태깅해주세요!"
            mDialogView.findViewById<Button>(R.id.Cancel).visibility = View.GONE

            val mAlertDialog = mBuilder.show()

            mDialogView.findViewById<Button>(R.id.Confirm).setOnClickListener {
                mAlertDialog.dismiss()
            }

            if (tableId.isEmpty()) {
                mAlertDialog.show()
            } else {
                mAlertDialog.dismiss()
                if (tableId.toInt() > 10) {
                    tableId = "order_table $tableId"
                } else {
                    tableId = "order_table 0${tableId}"
                }
                CoroutineScope(Dispatchers.Main).launch {
                    var AllOrderList = orderDao.getAllOrder()!!.toList().reversed()
                    var lastOrderId = AllOrderList[AllOrderList.lastIndex].orderId + 1

                    var productList = productDao.getAllProduct()!!
                    var quantity = 0

                    val orderDetail: ArrayList<OrderDetailDto> = arrayListOf()
                    for(i in listViewItems){
                        var productId = 0
                        for(j in productList){
                            if(i["name"] == j.name){
                                productId = j.id
                            }
                        }
                        quantity += i["quantity"].toString().toDouble().toInt()
                        orderDetail.add(OrderDetailDto(lastOrderId, productId, i["quantity"].toString().toDouble().toInt()))
                    }

                    orderDao.addOrder(OrderDto("N", orderDetail, "$tableId", LocalDateTime.now().toString(), StampDto(lastOrderId, quantity, userId), userId))
                    Toast.makeText(this@OrderDetailActivity, "재주문이 완료되었습니다.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }

        updateListView()
    }

    private fun updateListView() {
        // Adapter 생성
        val adapter = OrderDetailAdapter(this, R.layout.order_detail_item, listViewItems)
        Log.d("오더디테일스", listViewItems.toString())
        listView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter.enableForegroundDispatch(this, pIntent, filters, null)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val action = intent!!.action
        if (action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED) || action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            processIntent(intent)
        }
    }

    private fun processIntent(intent: Intent) {
        var rawMsg = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)

        if (rawMsg != null) {
            val msgArr = arrayOfNulls<NdefMessage>(rawMsg.size)
            for (i in rawMsg.indices) {
                msgArr[i] = rawMsg[i] as NdefMessage?
            }

            val recInfo = msgArr[0]!!.records[0]

            val data = recInfo.type
            var recType = String(data)

            if (recType == "T") {
                tableId = String(recInfo.payload, 3, recInfo.payload.size - 3)
                Toast.makeText(
                    this, "${tableId}번 테이블 번호가 등록 되었습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableForegroundDispatch(this)
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