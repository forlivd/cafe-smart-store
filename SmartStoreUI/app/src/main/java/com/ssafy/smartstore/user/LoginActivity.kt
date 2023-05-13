package com.ssafy.smartstore.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ssafy.smartstore.MainActivity
import com.ssafy.smartstore.R
import com.ssafy.smartstore.dao.UserDao
import com.ssafy.smartstore.dto.UserDto
import com.ssafy.smartstore.network.RetroApp
import com.ssafy.smartstore.service.UserService
import com.ssafy.smartstore.util.PrefApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    companion object {
        const val channel_id = "ssafy_channel"
    }

    private lateinit var btnLogin: Button
    private lateinit var btnJoin: Button
    private lateinit var etId: EditText
    private lateinit var etPass: EditText
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin = findViewById(R.id.btnLogin)
        btnJoin = findViewById(R.id.btnJoin)
        etId = findViewById(R.id.etId)
        etPass = findViewById(R.id.etPass)

        //회원 가입으로 이동
        btnJoin.setOnClickListener {
            startActivity(Intent(this, JoinActivity::class.java))
        }

        //아이디, 패스워드가 다 입력되어 있으면 해당 유저가 있는지 확인하고, 있으면 홈으로 이동
        btnLogin.setOnClickListener {
            val id = etId.text.toString();
            val password = etPass.text.toString();

            if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "빈 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                userDao = UserDao()
                var isSuccess = false // 기본 로그인 실패

                CoroutineScope(Dispatchers.Main).launch {
                    var loginUser = userDao.getUserByIdPassword(id, password)!!
                    Log.d("확인", loginUser.toString())
                    if (loginUser == null)
                        Toast.makeText(
                            this@LoginActivity,
                            "Id, Password를 확인해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    else if (loginUser.id == id) {
                        isSuccess = true // 반환 받은 user의 id가 존재하면 성공
                    }
                    //로그인 결과에 따른 처리
                    CoroutineScope(Dispatchers.Main).launch {
                        if (isSuccess) {
                            val sharedPreference = RetroApp
                            sharedPreference.prefs.setString("userId", id)
                            sharedPreference.prefs.setString("userPassward", password)
                            sharedPreference.prefs.setString("userName", loginUser.name)
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "아이디, 비밀번호를 확인해주세요.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                }
            }
        }

    }
}