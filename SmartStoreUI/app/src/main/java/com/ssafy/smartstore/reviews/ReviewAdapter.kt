package com.ssafy.smartstore.reviews

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.R
import com.ssafy.smartstore.dto.CommentDto
import com.ssafy.smartstore.network.RetroApp

private const val TAG = "ReviewAdapter"

class ReviewAdapter(context: Context, val resource: Int, val objects: MutableList<CommentDto>) :
    RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(resource, parent, false)
        Log.d(TAG, "onCreateViewHolder: ReviewAdapter ViewHolder 객체 생성 !!")

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = objects[position]
        Log.d(TAG, "objects: ${objects[position]}")

        when (comment.productId) {
            1 -> holder.stuffImg.setImageResource(R.drawable.coffee1)
            2 -> holder.stuffImg.setImageResource(R.drawable.coffee2)
            3 -> holder.stuffImg.setImageResource(R.drawable.coffee3)
            4 -> holder.stuffImg.setImageResource(R.drawable.coffee4)
            5 -> holder.stuffImg.setImageResource(R.drawable.coffee5)
            6 -> holder.stuffImg.setImageResource(R.drawable.coffee6)
            7 -> holder.stuffImg.setImageResource(R.drawable.coffee7)
            8 -> holder.stuffImg.setImageResource(R.drawable.coffee8)
            9 -> holder.stuffImg.setImageResource(R.drawable.coffee9)
            10 -> holder.stuffImg.setImageResource(R.drawable.cookie)
        }

        holder.review.text = comment.comment
        holder.rating.rating = comment.rating.toFloat()
        holder.userId.text = comment.userId

        //저장해 둔 유저 아이디 가져오기
        val sharedPreference = RetroApp
        val userId = sharedPreference.prefs.getString("userId", "")

        if (comment.userId == userId) {
            holder.btnEdit.visibility = View.VISIBLE
            holder.btnEdit.setOnClickListener {
                val intent = Intent(holder.itemView.context, AddReviewActivity::class.java).apply {
                    putExtra("dto", comment)
                    putExtra("state", 1)
                }
                ContextCompat.startActivity(holder.itemView.context, intent, null)
            }
        }

        if (comment.productId == 0) {
            holder.rating.visibility = View.GONE
            holder.userId.visibility = View.GONE
            holder.btnEdit.visibility = View.GONE
            holder.review.gravity = Gravity.CENTER
        }
    }

    override fun getItemCount(): Int {
        return objects.size
    }
}

// ViewHolder 클래스 생성
class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var stuffImg = itemView.findViewById(R.id.img_review) as ImageView
    var review = itemView.findViewById(R.id.tv_review) as TextView
    var rating = itemView.findViewById(R.id.tv_rating) as RatingBar
    var userId = itemView.findViewById(R.id.tv_userid) as TextView
    var btnEdit = itemView.findViewById(R.id.btn_edit) as ImageButton
}