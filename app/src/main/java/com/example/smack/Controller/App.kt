package com.example.smack.Controller

import android.app.Application
import com.example.smack.Utililties.SharedPrefs

class App : Application() {
    companion object {
        lateinit var sharedPrefs: SharedPrefs
    }
    override fun onCreate() {
        sharedPrefs = SharedPrefs(applicationContext)
        super.onCreate()
    }
}