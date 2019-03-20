package com.example.smack.Services

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.smack.Model.Channel
import com.example.smack.Utililties.*
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    var isLoggedIn = false
    var userEmail = ""
    var authToken = ""

    fun registerUser(context: Context, email: String, password: String, complete: (Boolean)-> Unit){

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)

        val requestBody = jsonBody.toString()

        val registerRequest = object : StringRequest(Method.POST, REGISTER_URL,
            Response.Listener {response->
                //println(response)
                complete(true)
            },
            Response.ErrorListener {error ->
                Log.d("ERROR", "Could not register user: $error")
                complete(false)
            }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        Volley.newRequestQueue(context).add(registerRequest)
    }

    fun loginUser(context: Context, email: String, password: String, complete: (Boolean)-> Unit){

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)

        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, LOGIN_URL, null,
            Response.Listener {response->
                //parse the json object
                try{
                    userEmail = response.getString("user")
                    authToken = response.getString("token")
                    isLoggedIn = true
                    //println(response)
                    complete(true)
                }catch (e: JSONException){
                    Log.d("JSON", "EXC: ${e.localizedMessage}")
                    complete(false)
                }

            },
            Response.ErrorListener {error ->
                Log.d("ERROR", "Could not login user: $error")
                complete(false)
            }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        Volley.newRequestQueue(context).add(loginRequest) //Not correct way -- memory leaks
    }

    fun addUser(context: Context, name: String, email: String, avatarName: String, avatarBGColour: String, complete: (Boolean)-> Unit){

        val jsonBody = JSONObject()
        jsonBody.put("name", name)
        jsonBody.put("email", email)
        jsonBody.put("avatarName", avatarName)
        jsonBody.put("avatarColor", avatarBGColour)

        val requestBody = jsonBody.toString()

        val addUserRequest = object : JsonObjectRequest(Method.POST, ADD_USER_URL, null,
            Response.Listener {response->
                try {
                    UserDataService.avatarBGColour = response.getString("avatarColor")
                    UserDataService.avatarImg = response.getString("avatarName")
                    UserDataService.email = response.getString("email")
                    UserDataService.name = response.getString("name")
                    UserDataService.userId = response.getString("_id")

                    complete(true)

                }catch (e: JSONException){
                    Log.d("JSON", "EXC: ${e.localizedMessage}")
                    complete(false)
                }
            },
            Response.ErrorListener {error ->
                Log.d("ERROR", "Could not add new user: $error")
                complete(false)
            }){

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer $authToken")

                return headers
            }
        }

        Volley.newRequestQueue(context).add(addUserRequest) //Not correct way -- memory leaks
    }

    fun findUserByEmail(context: Context, complete: (Boolean)-> Unit){
        val findUserByEmailRequest = object : JsonObjectRequest(Method.GET, "$FIND_USER_BY_EMAIL_URL$userEmail", null,
            Response.Listener {response->
                try {
                    UserDataService.userId = response.getString("_id")
                    UserDataService.avatarBGColour = response.getString("avatarColor")
                    UserDataService.avatarImg = response.getString("avatarName")
                    UserDataService.email = response.getString("email")
                    UserDataService.name = response.getString("name")

                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)

                    complete(true)

                }catch (e: JSONException){
                    Log.d("JSON", "EXC: ${e.localizedMessage}")
                    complete(false)
                }
            },
            Response.ErrorListener {error ->
                Log.d("ERROR", "Could not find user: $error")
                complete(false)
            }){

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer $authToken")

                return headers
            }
        }

        Volley.newRequestQueue(context).add(findUserByEmailRequest) //Not correct way -- memory leaks
    }

}