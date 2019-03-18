package com.example.smack.Controller

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.smack.R
import com.example.smack.Services.AuthService
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profiledefault"
    var avatarColour = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
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

        AuthService.registerUser(this, "person@place.com", "12345678"){
            complete->
            if (complete){
                println("register user successful")
            }else{
                println("register user failed")
            }
        }//emailTextFieldCreate.text, passwordTextFieldCreate.text)
    }

}
