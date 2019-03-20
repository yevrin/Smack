package com.example.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.smack.Controller.App
import com.example.smack.Model.Channel
import com.example.smack.Utililties.FIND_ALL_CHANNELS
import org.json.JSONException
object MessageService {
    val channels = ArrayList<Channel>()


    fun findAllChannels(context: Context, complete: (Boolean)-> Unit){
        val findAllChannelsRequest = object : JsonArrayRequest(
            Method.GET, FIND_ALL_CHANNELS, null,
            Response.Listener { response->
                try {
                    for (i in 0 until response.length()) {
                        val jsonChannel = response.getJSONObject(i)

                        val channelId = jsonChannel.getString("_id")
                        val channelName = jsonChannel.getString("name")
                        val channelDescription = jsonChannel.getString("description")

                        val channel = Channel(channelName, channelDescription, channelId)
                        this.channels.add(channel)

                    }
                    complete(true)

                }catch (e: JSONException){
                    Log.d("JSON", "EXC: ${e.localizedMessage}")
                    complete(false)
                }
            },
            Response.ErrorListener { error ->
                Log.d("ERROR", "Could not find channels: $error")
                complete(false)
            }){

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.sharedPrefs.authToken}")

                return headers
            }
        }

        App.sharedPrefs.requestQueue.add(findAllChannelsRequest)
    }
}