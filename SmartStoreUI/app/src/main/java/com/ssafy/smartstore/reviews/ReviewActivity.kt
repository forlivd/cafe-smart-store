package com.ssafy.smartstore.reviews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.MainActivity
import com.ssafy.smartstore.R
import com.ssafy.smartstore.dao.CommentDao
import com.ssafy.smartstore.databinding.ActivityReviewBinding
import com.ssafy.smartstore.dto.CommentDto
import com.ssafy.smartstore.network.RetroApp
import com.ssafy.smartstore.order.OrderDetailActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable

class ReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding

    private lateinit var commentDao: CommentDao
    private lateinit var listView: RecyclerView
    private lateinit var listviewItems: MutableList<CommentDto>

    private var selected = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listView = binding.rvReview

        binding.btnTotalReview.setOnClickListener {
            selected = 0
            updateListview()
        }

        binding.btn1Review.setOnClickListener {
            selected = 1
            updateListview()
        }

        binding.btn2Review.setOnClickListener {
            selected = 2
            updateListview()
        }

        binding.btn3Review.setOnClickListener {
            selected = 3
            updateListview()
        }

        binding.btn4Review.setOnClickListener {
            selected = 5
            updateListview()
        }

        binding.btn5Review.setOnClickListener {
            selected = 5
            updateListview()
        }

        binding.btn6Review.setOnClickListener {
            selected = 6
            updateListview()
        }

        binding.btn7Review.setOnClickListener {
            selected = 7
            updateListview()
        }

        binding.btn8Review.setOnClickListener {
            selected = 8
            updateListview()
        }

        binding.btn9Review.setOnClickListener {
            selected = 9
            updateListview()
        }

        binding.btn10Review.setOnClickListener {
            selected = 10
            updateListview()
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

        val sharedPreference = RetroApp
        val userId = sharedPreference.prefs.getString("userId", "")

        binding.btnAdd.setOnClickListener {
            val intent: Intent = Intent(this@ReviewActivity, AddReviewActivity::class.java).apply {
                putExtra("state", 0)
                putExtra("dto", CommentDto("리뷰를 작성해 주세요!", 0, 3.0, userId) as Serializable)
            }
            startActivity(intent)
        }

        updateListview()
    }

    override fun onResume() {
        super.onResume()
        updateListview()
    }

    private fun updateListview() {
        CoroutineScope(Dispatchers.Main).launch {
            commentDao = CommentDao()

            if (selected == 0) {
                listviewItems = commentDao.getAllComments()
                Log.d("ReviewActivity", "$listviewItems")
            } else {
                listviewItems = commentDao.getCommentsById(selected)
            }

            if (listviewItems.size == 0) {
                listviewItems = mutableListOf(CommentDto("리뷰가 없어요❗ 주문 후 첫 리뷰를 작성해보세요", 0, 0.0, ""))
            }

            val adapter = ReviewAdapter(this@ReviewActivity, R.layout.review_item, listviewItems)
            listView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }
}