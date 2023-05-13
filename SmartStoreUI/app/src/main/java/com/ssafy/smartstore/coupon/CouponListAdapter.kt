package com.ssafy.smartstore.coupon

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.R
import com.ssafy.smartstore.dto.CouponDto
import org.w3c.dom.Text

class CouponListAdapter(context: Context, val resources: Int, val objects: List<CouponDto>) :
    RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(resources, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = objects[position].title
        holder.endDate.text = objects[position].endDate + "까지"
        holder.percent.text = objects[position].percent.toString() + "%"
        when (objects[position].used) {
            "Y" -> {
                holder.used.text = "사용완료"
            }
            "N" -> {
                holder.used.text = "미사용"
            }
        }
    }

    override fun getItemCount(): Int {
        return objects.size
    }
}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var title: TextView = itemView.findViewById(R.id.tvCouponTitle)
    var endDate: TextView = itemView.findViewById(R.id.tvCouponEndDate)
    var percent: TextView = itemView.findViewById(R.id.tvCouponPercent)
    var used: TextView = itemView.findViewById(R.id.tvCouponUsed)
}