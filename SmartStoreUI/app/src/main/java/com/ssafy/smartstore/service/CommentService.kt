package com.ssafy.smartstore.service

import com.ssafy.smartstore.dto.CommentDto
import com.ssafy.smartstore.dto.UserDto
import retrofit2.Call
import retrofit2.http.*

interface CommentService {
    //커맨트 정보 추가
    @POST("rest/comment")
    fun addComment(@Body body: CommentDto): Call<Boolean>

    //커멘트 수정
    @PUT("rest/comment")
    fun updateComment(@Body body: CommentDto): Call<Boolean>

    //전달된 ID의 커멘트를 삭제한다.
    @DELETE("rest/comment/{id}")
    fun deleteComment(@Path("id") id: Int): Call<Boolean>

    @GET("rest/comment/all")
    fun getAllComment(): Call<MutableList<CommentDto>>

    @GET("rest/comment/{productId}")
    fun getCommentById(@Path("productId") productId: Int): Call<MutableList<CommentDto>>


}