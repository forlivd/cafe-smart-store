package com.ssafy.smartstore

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.ssafy.smartstore.dao.OrderDao
import com.ssafy.smartstore.dao.ProductDao
import com.ssafy.smartstore.dto.OrderDto
import com.ssafy.smartstore.dto.UserDto
import com.ssafy.smartstore.order.OrderDetailActivity
import com.ssafy.smartstore.service.ProductService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable
import java.text.DecimalFormat
import java.text.SimpleDateFormat

private const val TAG = "MainAdapter"

class OrderListAdapter(
    val context: Context,
    val resource: Int,
    val objects: MutableList<MutableMap<String, Any>>
) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(resource, parent, false)

        Log.d(TAG, "onCreateViewHolder: MainAdapter ViewHolder 객체 생성 !!")
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder 데이터 입력!!")
        CoroutineScope(Dispatchers.Main).launch {
            var orderDtails = OrderDao().getOrderHistory(objects[position]["user_id"].toString())!!
            var lastImg = "coffee1.png"
            var totalCnt = 0.0
            var totalPrice = 0.0
            var stuffName = "coffie1"
            var complete = ""
            var date = ""
            if (orderDtails.isNotEmpty()) {
                for (i in orderDtails) {
                    if (i["o_id"] == objects[position]["o_id"]) {
                        lastImg = i["img"].toString()
                        totalCnt += i["quantity"] as Double
                        totalPrice += i["quantity"] as Double * i["price"] as Double
                        stuffName = i["name"].toString()
                        complete = i["completed"].toString()
                        date = i["order_time"].toString()
                    }
                }
            }

            when (lastImg) {
                "coffee1.png" -> holder.imgStuff.setImageResource(R.drawable.coffee1)
                "coffee2.png" -> holder.imgStuff.setImageResource(R.drawable.coffee2)
                "coffee3.png" -> holder.imgStuff.setImageResource(R.drawable.coffee3)
                "coffee4.png" -> holder.imgStuff.setImageResource(R.drawable.coffee4)
                "coffee5.png" -> holder.imgStuff.setImageResource(R.drawable.coffee5)
                "coffee6.png" -> holder.imgStuff.setImageResource(R.drawable.coffee6)
                "coffee7.png" -> holder.imgStuff.setImageResource(R.drawable.coffee7)
                "coffee8.png" -> holder.imgStuff.setImageResource(R.drawable.coffee8)
                "coffee9.png" -> holder.imgStuff.setImageResource(R.drawable.coffee9)
                else -> holder.imgStuff.setImageResource(R.drawable.cookie)
            }

            val dec = DecimalFormat("#,###")

            holder.stuffName.text = "$stuffName 외 ${totalCnt.toInt()} 잔"
            holder.price.text = dec.format(totalPrice).toString() + "원"
            holder.date.text = date.substring(5, 10)
            if (complete == "N") {
                holder.done.text = "완료"
            } else {
                holder.done.text = "미완"
            }

            holder.btnDetail.setOnClickListener {
                Intent(context, OrderDetailActivity::class.java).apply {
                    putExtra("o_id", objects[position]["o_id"].toString())
                    putExtra("dto", orderDtails as Serializable)
                }.run { context.startActivity(this) }

            }
        }

    }

    override fun getItemCount(): Int {
        return objects.size
    }
}

// ViewHolder 클래스 생성
class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var imgStuff = itemView.findViewById(R.id.imgStuff) as ImageView
    var stuffName = itemView.findViewById(R.id.tvOrderStuff) as TextView
    var price = itemView.findViewById(R.id.tvOrderPrice) as TextView
    var date = itemView.findViewById(R.id.tvOrderDate) as TextView
    var done = itemView.findViewById(R.id.tvOrderState) as TextView
    var btnDetail = itemView.findViewById(R.id.btn_order_detail) as Button
}