package com.ssafy.smartstore.order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.R
import com.ssafy.smartstore.dto.OrderHistoryDto
import com.ssafy.smartstore.service.ProductService
import java.text.DecimalFormat

class OrderDetailAdapter
    (context: Context, val resource: Int, val objects: MutableList<Map<String, Any>>) :
    RecyclerView.Adapter<ViewHolder>() {

    // viewType 형태의 아이템 뷰를 위한 뷰홀더 객체 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(resource, parent, false)

        return ViewHolder(itemView)
    }

    // position에 해당하는 데이터를 뷰홀더의 아이템 뷰에 표시
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dec = DecimalFormat("#,###")
        val dec2 = DecimalFormat("#")

        // 한 행에 데이터를 넣어준다.
        when (objects[position]["img"]) {
            "coffee1.png" -> holder.imgStuf.setImageResource(R.drawable.coffee1)
            "coffee2.png" -> holder.imgStuf.setImageResource(R.drawable.coffee2)
            "coffee3.png" -> holder.imgStuf.setImageResource(R.drawable.coffee3)
            "coffee4.png" -> holder.imgStuf.setImageResource(R.drawable.coffee4)
            "coffee5.png" -> holder.imgStuf.setImageResource(R.drawable.coffee5)
            "coffee6.png" -> holder.imgStuf.setImageResource(R.drawable.coffee6)
            "coffee7.png" -> holder.imgStuf.setImageResource(R.drawable.coffee7)
            "coffee8.png" -> holder.imgStuf.setImageResource(R.drawable.coffee8)
            "coffee9.png" -> holder.imgStuf.setImageResource(R.drawable.coffee9)
            else -> holder.imgStuf.setImageResource(R.drawable.cookie)
        }
        holder.stuffName.text = objects[position]["name"].toString()
        holder.price.text = dec.format(objects[position]["price"]).toString() + " 원"
        holder.quantitiy.text = dec2.format(objects[position]["quantity"]).toString() + " 잔"
        holder.totalPrice.text =
            dec.format(objects[position]["price"] as Double * objects[position]["quantity"] as Double)
                .toString() + " 원"

    }


    // 전체 아이템 개수 리턴
    override fun getItemCount(): Int {
        return objects.size
    }
}

// ViewHolder 클래스 생성
class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var imgStuf = itemView.findViewById(R.id.img) as ImageView
    var stuffName = itemView.findViewById(R.id.tvName) as TextView
    var price = itemView.findViewById(R.id.tvPrice) as TextView
    var quantitiy = itemView.findViewById(R.id.tvQuantity) as TextView
    var totalPrice = itemView.findViewById(R.id.tvtotalPrice) as TextView
}