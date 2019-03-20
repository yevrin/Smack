package com.example.smack.Controller

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.smack.R
import com.example.smack.Services.AuthService
import com.example.smack.Utililties.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profiledefault"
    var avatarColour = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        createSpinner.visibility = View.INVISIBLE
    }

    fun generateBGColorBtnCreateClicked(view : View){
        val random = Random()
        val red = random.nextInt(256)
        val green = random.nextInt(256)
        val blue = random.nextInt(256)

        avatarImgCreate.setBackgroundColor(Color.rgb(red, green, blue))

        val redPercent = red.toDouble() / 255
        val greenPercent = green.toDouble() / 255
        val bluePercent = blue.toDouble() / 255

        avatarColour = "[$redPercent, $greenPercent, $bluePercent, 1]"

    }

    fun generateAvatarClicked(view : View){

        hideKeyboard()
        val random = Random()
        val whatHue = random.nextInt(2)
        val whatImg = random.nextInt(28)

        if(whatHue == 0){
            userAvatar="light$whatImg"
        }else{
            userAvatar= "dark$whatImg"
        }

        val resourceId = resources.getIdentifier(userAvatar, "drawable", packageName)
        avatarImgCreate.setImageResource(resourceId)

    }


    fun createUserBtnCreateClicked(view : View){

        enableSpinner(true)
        hideKeyboard()

        val username = usernameTextFieldCreate.text.toString()
        val email = emailTextFieldCreate.text.toString()
        val password = passwordTextFieldCreate.text.toString()

        if(username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            AuthService.registerUser(this, email, password) { registerSuccess ->
                if (registerSuccess) {
                    AuthService.loginUser(this, email, password) { loginSuccess ->
                        if (loginSuccess) {
                            AuthService.addUser(this, username, email, userAvatar, avatarColour) { addUserSuccess ->
                                if (addUserSuccess) {
                                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)

                                    enableSpinner(false)

                                    finish()
                                } else {
                                    errorToast("adding user failed")
                                }
                            }
                        } else {
                            errorToast("login user failed")
                        }
                    }
                } else {
                    errorToast("register user failed")
                }
            }
        }else{
            errorToast("Please make sure all text fields are filled in properly.")
        }
    }

    private fun errorToast(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    private fun enableSpinner(enable: Boolean){
        if(enable){
            createSpinner.visibility = View.VISIBLE
        }else{
            createSpinner.visibility = View.INVISIBLE
        }

        createUserBtnCreate.isEnabled = !enable
        avatarImgCreate.isEnabled = !enable
        generateBGColorBtnCreate.isEnabled = !enable

    }

    private fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

}
