package com.ssafy.smartstore

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ViewPortHandler
import com.ssafy.smartstore.alarm.AlarmActivity
import com.ssafy.smartstore.dao.OrderDao
import com.ssafy.smartstore.dao.ProductDao
import com.ssafy.smartstore.databinding.ActivityAnalyticsBinding
import com.ssafy.smartstore.dto.OrderDetailDto
import com.ssafy.smartstore.dto.ProductDto
import com.ssafy.smartstore.order.MenuListActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

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

class AnalyticsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnalyticsBinding

    private var orderList: List<OrderDetailDto> = arrayListOf()
    private var productList: List<ProductDto> = arrayListOf()
    private var topList: ArrayList<ProductDto> = arrayListOf()

    // Dao
    private var productDao = ProductDao()
    private var orderDao = OrderDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buttonInit()
        chartOption()
        updateChart()

        binding.btnGoOrder.setOnClickListener {
            val intent = Intent(this, MenuListActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.horizon_enter, R.anim.none)
        }
    }

    private fun chartOption() {
        binding.orderChart.apply {
            description.isEnabled = false
            setExtraOffsets(5f, 10f, 5f, 5f)
            dragDecelerationFrictionCoef = 0.95f
            isDrawHoleEnabled = false
            setHoleColor(Color.WHITE)
            transparentCircleRadius = 61f
            legend.isEnabled = false
        }
    }

    private fun updateChart() {
        CoroutineScope(Dispatchers.Main).launch {
            productList = productDao.getAllProduct()!!.toList().reversed()
            orderList = orderDao.getAllOrder()!!.toList().reversed()
            var orderCnt = Array<pRank?>(11) { null }
            for (i in orderCnt.indices) {
                orderCnt[i] = pRank(i, 0)
            }
            for (i in orderList) {
                orderCnt[i.productId]!!.cnt++
            }
            Arrays.sort(orderCnt, Collections.reverseOrder())

            for (element in orderCnt) {
                for (k in productList) {
                    if (element!!.pId == k.id)
                        topList.add(ProductDto(k.img, k.name, k.price, k.type))
                }
            }

            Log.d("통계", orderCnt.contentToString())
            Log.d("통계", topList.toString())
            Log.d("통계", "${orderCnt.size}, ${topList.size}")

            val entries = ArrayList<PieEntry>()

            for (i in 0 until topList.size) {
                entries.add(PieEntry(orderCnt[i]!!.cnt.toFloat(), topList[i].name))
            }

            val colorItems = ArrayList<Int>()
            for (c in ColorTemplate.VORDIPLOM_COLORS) colorItems.add(c)
            for (c in ColorTemplate.JOYFUL_COLORS) colorItems.add(c)
            for (c in ColorTemplate.COLORFUL_COLORS) colorItems.add(c)
            for (c in ColorTemplate.LIBERTY_COLORS) colorItems.add(c)
            for (c in ColorTemplate.PASTEL_COLORS) colorItems.add(c)
            colorItems.add(ColorTemplate.getHoloBlue())

            val pieDataSet = PieDataSet(entries, "")
            pieDataSet.apply {
                colors = colorItems
                valueTextColor = Color.WHITE
                valueTextSize = 5f
                sliceSpace = 3f
                selectionShift = 5f
                xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                valueLinePart1Length = 0.4f
                valueLinePart2Length = 0.4f
                valueFormatter = IValueFormatter { value, entry, dataSetIndex, viewPortHandler ->
                    value.toInt().toString()
                }
                valueTypeface = Typeface.createFromAsset(assets, "chartFont/bmhanna_11yrs_ttf.ttf")
            }

            val pieData = PieData(pieDataSet)
            binding.orderChart.apply {
                data = pieData
                data.setValueTextSize(14f)
                data.setValueTextColor(Color.BLACK)
                setEntryLabelColor(Color.BLACK)
                animateY(1000, Easing.EasingOption.EaseInOutQuad)
                animate()
            }

            binding.firstMenu.text = topList[0].name
            binding.secondMenu.text = topList[1].name
            binding.thirdMenu.text = topList[2].name
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
}