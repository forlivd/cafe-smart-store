package com.ssafy.smartstore.service

import com.ssafy.smartstore.dto.ProductDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductService {
    // 전체 상품목록 반환
    @GET("rest/product")
    fun getAllProduct(): Call<List<ProductDto>>

    // productId에 해당하는 상품의 정보를 comment와 함께 반환
    @GET("rest/product/{productId}")
    fun getProduct(@Path("productId") productId: Int): Call<List<Map<String, Any>>>
}