package com.ssafy.smartstore.shoppinglist

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.R
import com.ssafy.smartstore.dao.ProductDao
import com.ssafy.smartstore.dto.OrderDetailDto
import com.ssafy.smartstore.dto.OrderDto
import com.ssafy.smartstore.dto.ProductDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class ShoppingListAdapter(
    context: Context?,
    val resources: Int,
    val objects: ArrayList<OrderDetailDto>
) :
    RecyclerView.Adapter<ShoppingListViewHolder>() {
    var img: String = ""
    var name: String = ""
    var count: Int = 0
    var price: Int = 0
    var priceSum: Int = 0
    var allCount: Int = 0
    var allPrice: Int = 0

    var mPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(resources, parent, false)

        return ShoppingListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val dec = DecimalFormat("#,###")
        val productDao = ProductDao()
        var productList: List<ProductDto>

        CoroutineScope(Dispatchers.Main).launch {
            productList = productDao.getAllProduct()!!.toList().reversed()
            for (i in productList) {
                if (i.id == objects[position].productId) {
                    img = i.img
                    name = i.name
                    count = objects[position].quantity
                    price = i.price
                    priceSum = count * price
                    allCount += count
                    allPrice += priceSum
                    Log.d("어댑터", "$img + $name + $count + $price + $priceSum")
                }
            }
            holder.img.setImageResource(getResources(img))
            holder.name.text = name
            holder.count.text = count.toString() + "잔"
            holder.price.text = dec.format(price).toString() + "원"
            holder.priceSum.text = dec.format(priceSum).toString() + "원"
        }
    }

    override fun getItemCount(): Int {
        return objects.size
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

class ShoppingListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var img = itemView.findViewById(R.id.img_order_coffee) as ImageView
    var name = itemView.findViewById(R.id.tv_order_coffee_name) as TextView
    var count = itemView.findViewById(R.id.tv_order_coffee_count) as TextView
    var price = itemView.findViewById(R.id.tv_order_price) as TextView
    var priceSum = itemView.findViewById(R.id.tv_order_price_sum) as TextView
    var btnCancel = itemView.findViewById(R.id.btn_cancel) as ImageButton
}