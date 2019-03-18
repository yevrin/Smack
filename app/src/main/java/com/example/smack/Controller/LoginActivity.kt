package com.example.smack.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.smack.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginBtnLoginClicked(view : View){

    }
    fun registerBtnLoginClicked(view : View){
        val registerIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(registerIntent)
    }
}
