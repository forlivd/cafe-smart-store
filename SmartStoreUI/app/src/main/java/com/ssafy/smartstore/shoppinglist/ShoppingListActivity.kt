package com.ssafy.smartstore.shoppinglist

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.app.PendingIntent
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.smartstore.MainActivity
import com.ssafy.smartstore.R
import com.ssafy.smartstore.dao.OrderDao
import com.ssafy.smartstore.databinding.ActivityShoppingListBinding
import com.ssafy.smartstore.dto.OrderDetailDto
import com.ssafy.smartstore.dto.OrderDto
import com.ssafy.smartstore.dto.StampDto
import com.ssafy.smartstore.network.RetroApp
import com.ssafy.smartstore.order.MenuListActivity
import com.ssafy.smartstore.user.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.co.bootpay.Bootpay
import kr.co.bootpay.BootpayAnalytics
import kr.co.bootpay.enums.Method
import kr.co.bootpay.enums.PG
import kr.co.bootpay.enums.UX
import kr.co.bootpay.model.BootExtra
import kr.co.bootpay.model.BootUser
import java.text.DecimalFormat
import java.time.LocalDateTime


private const val TAG = "ShoppingListActivity_싸피"

class ShoppingListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShoppingListBinding

    private var shoppingList: ArrayList<OrderDetailDto> = arrayListOf()
    private lateinit var adapter: ShoppingListAdapter

    private var orderDao = OrderDao()

    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var pIntent: PendingIntent
    private lateinit var filters: Array<IntentFilter>
    private var tableId: String = ""
    private var quantity: Int = 0
    private var flag = "N"

    private var totalprice = 0

    val dec = DecimalFormat("#,###")

    // 결제

    val application_id = "628f903de38c3000258095fc"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShoppingListBinding.inflate(layoutInflater)
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

        binding.btnStore.setOnClickListener {
            binding.btnStore.setBackgroundResource(R.drawable.color_gray_round)
            binding.btnTakeout.setBackgroundResource(R.drawable.button_white_color)
        }

        binding.btnTakeout.setOnClickListener {
            binding.btnTakeout.setBackgroundResource(R.drawable.color_gray_round)
            binding.btnStore.setBackgroundResource(R.drawable.button_white_color)
        }


        // OrderActivity로부터 받은 장바구니 List
        shoppingList = intent.getSerializableExtra("shoppingList") as ArrayList<OrderDetailDto>
        for (i in shoppingList) {
            quantity += i.quantity
            var price1 = 0
            if (i.productId == 1) {
                price1 = 4100
            } else if (i.productId == 2) {
                price1 = 4500
            } else if (i.productId == 3) {
                price1 = 4800
            } else if (i.productId == 4) {
                price1 = 4800
            } else if (i.productId == 5) {
                price1 = 4800
            } else if (i.productId == 6) {
                price1 = 4300
            } else if (i.productId == 7) {
                price1 = 4800
            } else if (i.productId == 8) {
                price1 = 5100
            } else if (i.productId == 9) {
                price1 = 5100
            } else if (i.productId == 10) {
                price1 = 1500
            }
            totalprice += i.quantity * price1
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }

        val intent = Intent(this, ShoppingListActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        pIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val ndef_filter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        ndef_filter.addDataType("text/plain")
        filters = arrayOf(ndef_filter)

        // Recycler 적용
        if (shoppingList != null) {
            binding.rcOrder.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            CoroutineScope(Dispatchers.Main).launch {
                adapter =
                    ShoppingListAdapter(
                        this@ShoppingListActivity,
                        R.layout.order_item,
                        shoppingList
                    )
                binding.rcOrder.adapter = adapter
                adapter.notifyDataSetChanged()
            }
            CoroutineScope(Dispatchers.Main).launch {
                delay(500)
                binding.tvShoppingListCount.text = adapter.allCount.toString() + "잔"
                binding.tvShoppingListPricesum.text = dec.format(adapter.allPrice).toString() + "원"

            }
        }

        // 주문하기 버튼 클릭시
        binding.btnOrder.setOnClickListener {
            if (shoppingList.isEmpty()) {
                Toast.makeText(this, "메뉴를 선택해주세요", Toast.LENGTH_SHORT).show()
            } else {
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


//                    val intent = Intent(this@ShoppingListActivity, MenuListActivity::class.java)
                    CoroutineScope(Dispatchers.Main).launch {
                        // 모든 주문 정보를 가져와 추가할 주문 번호 구하기
//                        var AllOrderList = orderDao.getAllOrder()!!.toList().reversed()
//                        var lastOrderId = AllOrderList[AllOrderList.lastIndex].orderId + 1

                        goBootpayRequest()

//                        // 유저 아이디 가져오기
//                        val sharedPreference = RetroApp
//                        val userId = sharedPreference.prefs.getString("userId", "")
//
//                        // 주문하기
//                        orderDao.addOrder(
//                            OrderDto(
//                                "N",
//                                shoppingList,
//                                "$tableId",
//                                LocalDateTime.now().toString(),
//                                StampDto(lastOrderId, quantity, userId),
//                                userId
//                            )
//                        )
//
//                        Toast.makeText(this@ShoppingListActivity, "주문이 완료되었습니다.", Toast.LENGTH_LONG)
//                            .show()
//
//                        // MenuList에 알리기
//                        intent.putExtra("flag", "Y")
//                        setResult(RESULT_OK, intent)
//                        finish()
                    }
                }
            }
        }

        //결제
        BootpayAnalytics.init(this, application_id)

    }

    //결제

    @RequiresApi(Build.VERSION_CODES.O)
    fun goBootpayRequest() {
        val bootUser = BootUser().setPhone("010-1234-5678") // 실제 사용하게 되면 수정
        val bootExtra = BootExtra().setQuotas(intArrayOf(0, 2, 3))
        val intent = Intent(this@ShoppingListActivity, MenuListActivity::class.java)

        val stuck = 1 //재고 있음

        Bootpay.init(this)
            .setApplicationId(application_id) // 해당 프로젝트(안드로이드)의 application id 값
            .setContext(this)
            .setBootUser(bootUser)
            .setBootExtra(bootExtra)
            .setUX(UX.PG_DIALOG)
            .setPG(PG.INICIS)
            .setMethod(Method.PHONE)
//                .setUserPhone("010-1234-5678") // 구매자 전화번호 실제 사용하게 되면 수정
            .setName("SSAFY CAFE") // 결제할 상품명
            .setOrderId("1234") // 결제 고유번호expire_month
            .setPrice(100) // 결제할 금액
            .addItem("커피", 1, "ITEM_CODE_MOUSE", 100) // 주문정보에 담길 상품정보, 통계를 위해 사용
            .addItem(
                "키보드",
                1,
                "ITEM_CODE_KEYBOARD",
                200,
                "패션",
                "여성상의",
                "블라우스"
            ) // 주문정보에 담길 상품정보, 통계를 위해 사용
            .onConfirm { message ->
                if (0 < stuck) Bootpay.confirm(message); // 재고가 있을 경우.
                else Bootpay.removePaymentWindow(); // 재고가 없어 중간에 결제창을 닫고 싶을 경우
                Log.d("confirm", message);
            }
            .onDone { message ->
                // 유저 아이디 가져오기


                // 모든 주문 정보를 가져와 추가할 주문 번호 구하기
                CoroutineScope(Dispatchers.Main).launch {
                    var AllOrderList = orderDao.getAllOrder()!!.toList().reversed()
                    var lastOrderId = AllOrderList[AllOrderList.lastIndex].orderId + 1

                    val sharedPreference = RetroApp
                    val userId = sharedPreference.prefs.getString("userId", "")

                    // 주문하기
                    orderDao.addOrder(
                        OrderDto(
                            "N",
                            shoppingList,
                            "$tableId",
                            LocalDateTime.now().toString(),
                            StampDto(lastOrderId, quantity, userId),
                            userId
                        )
                    )

                    Toast.makeText(this@ShoppingListActivity, "주문이 완료되었습니다.", Toast.LENGTH_LONG)
                        .show()

                    // MenuList에 알리기
                    intent.putExtra("flag", "Y")
                    setResult(RESULT_OK, intent)
                    finish()
                    Log.d("done", message)
                }
            }
            .onReady { message ->
                Log.d("ready", message)
            }
            .onCancel { message ->
                tableId = ""
                Toast.makeText(this@ShoppingListActivity, "주문이 취소되었습니다.", Toast.LENGTH_SHORT).show()
                intent.putExtra("flag", "Y")
                setResult(RESULT_OK, intent)
                finish()

                Log.d("cancel", message)
            }
            .onError { message ->
                tableId = ""
                Toast.makeText(this@ShoppingListActivity, "주문이 비정상 종료 되었습니다..", Toast.LENGTH_SHORT).show()
                intent.putExtra("flag", "Y")
                setResult(RESULT_OK, intent)
                finish()

                Log.d("error", message)
            }
            .onClose { message ->
                Log.d("close", "close")
                intent.putExtra("flag", "Y")
                setResult(RESULT_OK, intent)
                finish()
            }
            .request();
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

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableForegroundDispatch(this)
    }

    override fun onBackPressed() {
        Intent(this, MenuListActivity::class.java).apply {
            putExtra("flag", flag)
            setResult(Activity.RESULT_OK, this)
        }
        super.onBackPressed()
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

}