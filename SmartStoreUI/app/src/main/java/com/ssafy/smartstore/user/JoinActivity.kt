package com.ssafy.smartstore.user

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ssafy.smartstore.MainActivity
import com.ssafy.smartstore.R
import com.ssafy.smartstore.dao.UserDao
import com.ssafy.smartstore.dto.UserDto
import com.ssafy.smartstore.network.RetroApp
import com.ssafy.smartstore.service.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JoinActivity : AppCompatActivity() {
    private var doneIdCheck: Boolean = false

    private lateinit var btnCheckId: ImageButton
    private lateinit var btnRegister: Button
    private lateinit var etId: EditText
    private lateinit var etPass: EditText
    private lateinit var etName: EditText
    private lateinit var userDao: UserDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        btnCheckId = findViewById(R.id.btnId)
        btnRegister = findViewById(R.id.btnJoin)
        etId = findViewById(R.id.etId)
        etPass = findViewById(R.id.etPass)
        etName = findViewById(R.id.etNickname)

        // 아이디 변경 시 다시 id 체크 해야함
        etId.setOnClickListener{
            doneIdCheck = false
        }

        // id 중복 체크 후 사용 가능하면 doneIdCheck true로 바꿈
        btnCheckId.setOnClickListener{
            if(etId.text.toString() != "") {
                     userDao = UserDao()

                    //로그인 결과에 따른 처리
                    CoroutineScope(Dispatchers.Main).launch {
                        var isGoing = userDao.isNotUsed(etId.text.toString())!!

                        if (!isGoing) {
                            Toast.makeText(this@JoinActivity, "사용 가능한 ID입니다.", Toast.LENGTH_SHORT)
                                .show()
                            doneIdCheck = true
                        } else {
                            Toast.makeText(this@JoinActivity, "사용할 수 없는 ID입니다.", Toast.LENGTH_SHORT)
                                .show()
                            etId.setText("")
                        }
                    }

            }else {
                Toast.makeText(this, "아이디 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // id 중복 체크완료, 빈 값 없을 경우 회원 가입 진행
        btnRegister.setOnClickListener{
            if(!doneIdCheck) {
                Toast.makeText(this,"ID 중복을 확인하세요.", Toast.LENGTH_SHORT).show()
            } else if (etId.text.toString() == ("")  || etPass.text.toString() == ("")|| etName.text.toString() == ("")) {
                Toast.makeText(this,"빈 항목을 입력하세요.", Toast.LENGTH_SHORT).show()
            } else {
                //회원 가입 처리
                CoroutineScope(Dispatchers.Main).launch {
                    val newUser = UserDto(etId.text.toString(), etName.text.toString(), etPass.text.toString())
                    var isDone = userDao.addUser(newUser)!!

                    if (isDone) {
                        Toast.makeText(this@JoinActivity,"회원 가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@JoinActivity,"회원 가입을 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
}