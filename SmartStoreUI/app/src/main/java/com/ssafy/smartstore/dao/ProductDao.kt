package com.ssafy.smartstore.dao

import android.os.SystemClock.sleep
import android.util.Log
import android.widget.Toast
import com.ssafy.smartstore.dto.ProductDto
import com.ssafy.smartstore.network.RetroApp
import com.ssafy.smartstore.service.ProductService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "TAG[ProductDao]"

class ProductDao {
    val productService = RetroApp.retrofit.create(ProductService::class.java)

    suspend fun getAllProduct(): List<ProductDto>? {
        var products: List<ProductDto>? = withContext(Dispatchers.IO) {
            val response = productService.getAllProduct().execute()
            val list = if (response.code() == 200) {
                var res = response.body() ?: arrayListOf()
                res
            } else {
                arrayListOf()
            }
            list
        }
        return products
    }

    suspend fun getProduct(productId: Int): List<Map<String, Any>>? {
        var product: List<Map<String, Any>>? = withContext(Dispatchers.IO) {
            val response = productService.getProduct(productId).execute()
            val list = if (response.code() == 200) {
                var res = response.body() ?: arrayListOf()
                res
            } else {
                arrayListOf()
            }
            list
        }
        return product
    }

}