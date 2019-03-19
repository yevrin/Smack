package com.example.smack.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.smack.R
import com.example.smack.Services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginBtnLoginClicked(view : View){
        val email = emailTextFieldLogin.text.toString()
        val password = passwordTextFieldLogin.text.toString()
        AuthService.loginUser(this, email, password){loginSuccess->
            if(loginSuccess){
                AuthService.findUserByEmail(this) { findUserSuccess ->
                    if (findUserSuccess) {
                        finish()

                    } else {

                    }
                }
            }else{

            }

        }
    }

    fun registerBtnLoginClicked(view : View){
        val registerIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(registerIntent)
        finish()
    }
}
