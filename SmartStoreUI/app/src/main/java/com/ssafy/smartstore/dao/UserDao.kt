package com.ssafy.smartstore.dao

import android.util.Log
import com.ssafy.smartstore.dto.UserDto
import com.ssafy.smartstore.network.RetroApp
import com.ssafy.smartstore.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.round

class UserDao {
    private val userRetro: UserService = RetroApp.retrofit.create(UserService::class.java)

    // 아이디 중복 체크
    suspend fun isNotUsed(userId: String): Boolean {
        var result = false // 기본값 중복

        result = withContext(Dispatchers.IO) {
            val response = userRetro.getIsUsed(userId).execute()
            val notUsed: Boolean = if (response.code() == 200) {
                (response.body() as Boolean) // 중복 시 false, 아니면 true
            } else {
                Log.d("tag", "isNotUsed : Error code ${response.code()}")
                false
            }
            notUsed
        }
        return result
    }

    // 해당 아이디, 비밀번호의 유저 객체 가져오기
    suspend fun getUserByIdPassword(userId: String, password: String): UserDto? {
        var user: UserDto = UserDto(userId, "", password) // 기본값 입력 받은 유저
        var resultUser: UserDto = UserDto("", "", "")

        resultUser = withContext(Dispatchers.IO) {
            val response = userRetro.getLogin(user).execute()
            val user: UserDto = if (response.code() == 200) {
                var res = response.body() ?: UserDto("", "", "")
                res
            } else {
                Log.d("tag", "getLogin : Error code ${response.code()}")
                UserDto("", "", "")
            }
            user
        }
        if (resultUser.id == "string" && resultUser.pass == "string")
            return null
        return resultUser // 해당 유저 없으면 (" ", " ", " ") 반환
    }

    // 사용자 정보 추가 - 아이디, 닉네임, 패스워드, 스탬프 수, 스탬프 정보
    suspend fun addUser(newUser: UserDto): Boolean {
        var result = false // 기본값 실패

        result = withContext(Dispatchers.IO) {
            val response = userRetro.setUser(newUser).execute()
            var success = if (response.code() == 200) {
                response.body() as Boolean
            } else {
                false
            }
            success
        }

        return result

    }

    suspend fun getStampCount(userId: String): Int {
        var result: Int = withContext(Dispatchers.IO) {
            val response = userRetro.getStampByUser(userId).execute()
            val count = if (response.code() == 200) {
                var res = response.body() ?: 0
                res
            } else {
                0
            }
            count
        }
        return result
    }
}