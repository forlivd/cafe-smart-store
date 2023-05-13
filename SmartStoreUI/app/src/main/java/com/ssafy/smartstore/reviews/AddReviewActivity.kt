package com.ssafy.smartstore.reviews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.ssafy.smartstore.MainActivity
import com.ssafy.smartstore.R
import com.ssafy.smartstore.dao.CommentDao
import com.ssafy.smartstore.databinding.ActivityAddReviewBinding
import com.ssafy.smartstore.databinding.ActivityReviewBinding
import com.ssafy.smartstore.dto.CommentDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddReviewBinding
    private lateinit var comment: CommentDto
    private var state: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        comment = intent.getSerializableExtra("dto") as CommentDto
        state = intent.getIntExtra("state", 0)

        when (state) {
            0 -> {
                binding.btnDeleteReview.visibility = View.GONE
                binding.btnModifyReview.visibility = View.GONE
                binding.btnAddReview.visibility = View.VISIBLE
                binding.spinner.setSelection(0)
                binding.tvComment.isEnabled = true
                comment.productId = 1

            }
            1 -> {
                binding.btnDeleteReview.visibility = View.VISIBLE
                binding.btnModifyReview.visibility = View.VISIBLE
                binding.btnAddReview.visibility = View.GONE
                binding.tvComment.isEnabled = true
                binding.spinner.setSelection(comment.productId - 1)
            }

            2 -> {
                binding.btnDeleteReview.visibility = View.GONE
                binding.btnModifyReview.visibility = View.GONE
                binding.btnAddReview.visibility = View.GONE
                binding.spinner.visibility = View.GONE
                binding.conbtn.visibility = View.GONE
            }
        }

        when (comment.productId) {
            1 -> binding.imgReview.setImageResource(R.drawable.coffee1)
            2 -> binding.imgReview.setImageResource(R.drawable.coffee2)
            3 -> binding.imgReview.setImageResource(R.drawable.coffee3)
            4 -> binding.imgReview.setImageResource(R.drawable.coffee4)
            5 -> binding.imgReview.setImageResource(R.drawable.coffee5)
            6 -> binding.imgReview.setImageResource(R.drawable.coffee6)
            7 -> binding.imgReview.setImageResource(R.drawable.coffee7)
            8 -> binding.imgReview.setImageResource(R.drawable.coffee8)
            9 -> binding.imgReview.setImageResource(R.drawable.coffee9)
            else -> binding.imgReview.setImageResource(R.drawable.cookie)
        }

        val spinner = findViewById<Spinner>(R.id.spinner)
        var spindata = resources.getStringArray(R.array.spins)
        var adapter1 = ArrayAdapter<String>(this, R.layout.item_spinner, spindata)
        spinner.adapter = adapter1
        spinner.setSelection(0)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                comment.productId = (spinner.selectedItemPosition + 1)
                when (comment.productId) {
                    1 -> binding.imgReview.setImageResource(R.drawable.coffee1)
                    2 -> binding.imgReview.setImageResource(R.drawable.coffee2)
                    3 -> binding.imgReview.setImageResource(R.drawable.coffee3)
                    4 -> binding.imgReview.setImageResource(R.drawable.coffee4)
                    5 -> binding.imgReview.setImageResource(R.drawable.coffee5)
                    6 -> binding.imgReview.setImageResource(R.drawable.coffee6)
                    7 -> binding.imgReview.setImageResource(R.drawable.coffee7)
                    8 -> binding.imgReview.setImageResource(R.drawable.coffee8)
                    9 -> binding.imgReview.setImageResource(R.drawable.coffee9)
                    else -> binding.imgReview.setImageResource(R.drawable.cookie)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.ratingbar.rating = comment.rating.toFloat()
        binding.tvComment.hint = comment.comment

        binding.btnAddReview.setOnClickListener {
            comment.comment = binding.tvComment.text.toString()
            comment.rating = binding.ratingbar.rating.toDouble()
            comment.productId = binding.spinner.selectedItemPosition + 1

            CoroutineScope(Dispatchers.Main).launch {
                val result = CommentDao().addComment(comment)
                if (result) {
                    Toast.makeText(this@AddReviewActivity, "리뷰 등록이 완료되었습니다.", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                } else {
                    Toast.makeText(this@AddReviewActivity, "리뷰 등록에 실패하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        binding.btnDeleteReview.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val result = CommentDao().deleteComment(comment.id)
                if (result) {
                    Toast.makeText(this@AddReviewActivity, "리뷰 삭제가 완료되었습니다.", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                } else {
                    Toast.makeText(this@AddReviewActivity, "리뷰 삭제에 실패하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        binding.btnModifyReview.setOnClickListener {
            comment.comment = binding.tvComment.text.toString()
            comment.rating = binding.ratingbar.rating.toDouble()
            comment.productId = binding.spinner.selectedItemPosition + 1

            CoroutineScope(Dispatchers.Main).launch {
                val result = CommentDao().modifyComment(comment)
                if (result) {
                    Toast.makeText(this@AddReviewActivity, "리뷰 수정이 완료되었습니다.", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                } else {
                    Toast.makeText(this@AddReviewActivity, "리뷰 수정에 실패하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

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
//
//    inner class SpinnerListener : AdapterView.OnItemSelectedListener {
//        override fun onNothingSelected(p0: AdapterView<*>?) {
//
//        }
//
//        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) { // p2가 사용자가 선택한 곳의 인덱스
//            comment.productId = binding.spinner.selectedItemPosition-1
//            Log.d("AddReviewActivity", "${binding.spinner.selectedItemPosition}")
//        }
//    }
}