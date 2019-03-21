package com.example.smack.Controller

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.smack.R
import com.example.smack.Services.AuthService
import kotlinx.android.synthetic.main.activity_login.*
import android.app.Activity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginSpinner.visibility = View.INVISIBLE
    }

    fun loginBtnLoginClicked(view : View){
        enableSpinner(true)
        hideKeyboard()

        val email = emailTextFieldLogin.text.toString()
        val password = passwordTextFieldLogin.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()) {
            AuthService.loginUser(email, password) { loginSuccess ->
                if (loginSuccess) {
                    AuthService.findUserByEmail(this) { findUserSuccess ->
                        if (findUserSuccess) {
                            enableSpinner(false)
                            finish()

                        } else {
                            errorToast("Unable to find user")
                        }
                    }
                } else {
                    errorToast("Unable to login user")
                }

            }
        }else{
            errorToast("Please properly fill in the email and password fields.")
        }
    }

    fun registerBtnLoginClicked(view : View){
        val registerIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(registerIntent)
        finish()
    }

    private fun enableSpinner(enable: Boolean){
        if(enable){
            loginSpinner.visibility = View.VISIBLE
        }else{
            loginSpinner.visibility = View.INVISIBLE
        }

        loginBtnLogin.isEnabled = !enable
        registerBtnLogin.isEnabled = !enable

    }

    private fun errorToast(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    private fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

}
