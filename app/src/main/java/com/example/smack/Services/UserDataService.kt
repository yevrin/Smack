package com.example.smack.Services

import android.graphics.Color
import java.util.*

object UserDataService {
    var name = ""
    var email = ""
    var avatarImg = ""
    var avatarBGColour = ""
    var userId = ""

    fun getAvatarBGColour(components: String) : Int {

        val strippedColour = components
            .replace("[", "")
            .replace("]", "")
            .replace(",", "")

        var red = 0
        var green = 0
        var blue = 0

        var scanner = Scanner(strippedColour)
        if(scanner.hasNext()){
            red = (scanner.nextDouble() * 255).toInt()
            green = (scanner.nextDouble() * 255).toInt()
            blue = (scanner.nextDouble() * 255).toInt()
        }

        return Color.rgb(red, green, blue)
    }

    fun logout(){
        name = ""
        email = ""
        avatarImg = ""
        avatarBGColour = ""
        userId = ""
        AuthService.authToken = ""
        AuthService.userEmail = ""
        AuthService.isLoggedIn = false

    }
}