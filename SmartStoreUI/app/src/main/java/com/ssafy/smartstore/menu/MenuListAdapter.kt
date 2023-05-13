package com.ssafy.smartstore.order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.R
import com.ssafy.smartstore.dto.ProductDto
import com.ssafy.smartstore.reviews.ReviewAdapter
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class MenuListAdapter(val resources: Int, val objects: List<ProductDto>) :
    RecyclerView.Adapter<MenuListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(resources, parent, false)

        return MenuListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MenuListViewHolder, position: Int) {
        val dec = DecimalFormat("#,###")
        holder.img.setImageResource(getImageResources(objects[position].img))
        holder.name.text = objects[position].name
        holder.price.text = dec.format(objects[position].price).toString() + "원"
        onItemClickListener?.let { holder.bindOnItemClickListener(it) }

        if(position == objects.size - 1){
            holder.divline.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return objects.size
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
    var onItemClickListener: OnItemClickListener? = null
}

class MenuListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var img = itemView.findViewById(R.id.ivMenuImage) as ImageView
    var name = itemView.findViewById(R.id.tvMenuName) as TextView
    var price = itemView.findViewById(R.id.tvMenuPrice) as TextView
    var divline = itemView.findViewById(R.id.divline) as ImageView

    fun bindOnItemClickListener(onItemClickListener: MenuListAdapter.OnItemClickListener) {
        itemView.setOnClickListener {
            onItemClickListener.onItemClick(it, adapterPosition)
        }
    }
}

private fun getImageResources(image: String): Int {
    return when (image) {
        "coffee1.png" -> R.drawable.coffee1
        "coffee2.png" -> R.drawable.coffee2
        "coffee3.png" -> R.drawable.coffee3
        "coffee4.png" -> R.drawable.coffee4
        "coffee5.png" -> R.drawable.coffee5
        "coffee6.png" -> R.drawable.coffee6
        "coffee7.png" -> R.drawable.coffee7
        "coffee8.png" -> R.drawable.coffee8
        "coffee9.png" -> R.drawable.coffee9
        else -> R.drawable.cookie
    }
}