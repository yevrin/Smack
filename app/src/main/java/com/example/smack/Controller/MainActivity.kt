package com.example.smack.Controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.smack.R
import com.example.smack.Services.AuthService
import com.example.smack.Services.UserDataService
import com.example.smack.Utililties.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver,
            IntentFilter(BROADCAST_USER_DATA_CHANGE))

    }

    private val userDataChangeReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(AuthService.isLoggedIn){
                usernameTxtNavHeader.text = UserDataService.name
                userEmailTxtNavHeader.text = UserDataService.email

                val resourceId = resources.getIdentifier(UserDataService.avatarImg, "drawable", packageName)
                profileIdImgNavHeader.setImageResource(resourceId)
                profileIdImgNavHeader.setBackgroundColor(UserDataService.getAvatarBGColour(UserDataService.avatarBGColour))

                loginoutBtnNavHeader.text = "Logout"
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginoutBtnNavHeaderClicked(view: View) {
        if(AuthService.isLoggedIn) {
            //logout
            UserDataService.logout()

            usernameTxtNavHeader.text = ""
            userEmailTxtNavHeader.text = ""
            profileIdImgNavHeader.setImageResource(R.drawable.profiledefault)
            profileIdImgNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginoutBtnNavHeader.text = "Login"


        }else{
            //login
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun addChannelBtnNavHeaderClicked(view: View) {

    }

    fun sendMsgBtnContentClicked(view: View){

    }

}
