package com.ssafy.smartstore.dao

import android.util.Log
import com.ssafy.smartstore.dto.CommentDto
import com.ssafy.smartstore.dto.OrderDto
import com.ssafy.smartstore.network.RetroApp
import com.ssafy.smartstore.service.CommentService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

private const val TAG = "TAG[CommentDao]"

class CommentDao {
    private val commentRetro = RetroApp.retrofit.create(CommentService::class.java)

    // 전체 상품평 조회
    suspend fun getAllComments(): MutableList<CommentDto> {
        var result: MutableList<CommentDto> = arrayListOf()

        result = withContext(Dispatchers.IO) {
            val response = commentRetro.getAllComment().execute()
            val comments = if (response.code() == 200) {
                (response.body() as MutableList<CommentDto>) // 중복 시 false, 아니면 true
            } else {
                Log.d("tag", "getAllComments : Error code ${response.code()}")
                arrayListOf()
            }
            comments
        }
        return result
    }

    // 특정 상품 상품평 조회
    suspend fun getCommentsById(productId: Int): MutableList<CommentDto> {
        var result: MutableList<CommentDto> = arrayListOf()

        result = withContext(Dispatchers.IO) {
            val response = commentRetro.getCommentById(productId).execute()
            val comments = if (response.code() == 200) {
                (response.body() as MutableList<CommentDto>) // 중복 시 false, 아니면 true
            } else {
                Log.d("tag", "getCommentsById : Error code ${response.code()}")
                arrayListOf()
            }
            comments
        }
        return result
    }


    // 상품평 추가, 성공하면 true 반환
    suspend fun addComment(comment: CommentDto): Boolean {
        var result = false
        result = withContext(Dispatchers.IO) {
            val response = commentRetro.addComment(comment).execute()
            val state = if (response.code() == 200) {
                var res = response.body()
                if (res != null) {
                    true
                } else {
                    Log.d(TAG, "상품평을 추가할 수 없습니다.")
                    false
                }
            } else {
                Log.d(TAG, "addComment: Error Code ${response.code()}")
                false
            }
            state
        }
        return result
    }


    // 상품평 수정
    suspend fun modifyComment(comment: CommentDto): Boolean {
        var result = false
        result = withContext(Dispatchers.IO) {
            val response = commentRetro.updateComment(comment).execute()
            val state = if (response.code() == 200) {
                var res = response.body()
                if (res != null) {
                    true
                } else {
                    Log.d(TAG, "상품평을 수정할 수 없습니다.")
                    false
                }
            } else {
                Log.d(TAG, "modifyComment: Error Code ${response.code()}")
                false
            }
            state
        }
        return result
    }

    // 상품평 삭제
    suspend fun deleteComment(commentId: Int): Boolean {
        var result = false
        result = withContext(Dispatchers.IO) {
            val response = commentRetro.deleteComment(commentId).execute()
            val state = if (response.code() == 200) {
                var res = response.body()
                if (res != null) {
                    true
                } else {
                    Log.d(TAG, "상품평을 삭제할 수 없습니다.")
                    false
                }
            } else {
                Log.d(TAG, "deleteComment: Error Code ${response.code()}")
                false
            }
            state
        }
        return result
    }

}