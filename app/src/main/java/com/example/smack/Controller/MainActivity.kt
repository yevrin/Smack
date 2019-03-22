package com.example.smack.Controller

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.example.smack.Adapters.MessageAdapter
import com.example.smack.Model.Channel
import com.example.smack.Model.Message
import com.example.smack.R
import com.example.smack.Services.AuthService
import com.example.smack.Services.MessageService
import com.example.smack.Services.UserDataService
import com.example.smack.Utililties.BROADCAST_USER_DATA_CHANGE
import com.example.smack.Utililties.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter : ArrayAdapter<Channel>
    lateinit var messageAdapter : MessageAdapter
    var selectedChannel: Channel? = null

    private fun setUpChannelAdapter(){
        channelAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter

    }

    private fun setUpMessageAdapter(){
        messageAdapter = MessageAdapter(this,MessageService.messages)
        msgListViewContent.adapter = messageAdapter

        val layoutManager = LinearLayoutManager(this)
        msgListViewContent.layoutManager = layoutManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        socket.connect()
        socket.on("channelCreated", onNewChannel)
        socket.on("messageCreated", onNewMessage)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        setUpChannelAdapter()
        setUpMessageAdapter()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver,
            IntentFilter(BROADCAST_USER_DATA_CHANGE))

        channel_list.setOnItemClickListener { _, _, position, _ ->
            selectedChannel = MessageService.channels[position]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }

        if(App.sharedPrefs.isLoggedIn){
            AuthService.findUserByEmail(this){}
        }
        /*LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver,
            IntentFilter(BROADCAST_USER_DATA_CHANGE))*/

    }

    private val userDataChangeReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent?) {
            Log.d("USERDATA_CHANGERECEIVER", "object called")
            if(App.sharedPrefs.isLoggedIn){
                usernameTxtNavHeader.text = UserDataService.name
                userEmailTxtNavHeader.text = UserDataService.email

                val resourceId = resources.getIdentifier(UserDataService.avatarImg, "drawable", packageName)
                profileIdImgNavHeader.setImageResource(resourceId)
                profileIdImgNavHeader.setBackgroundColor(UserDataService.getAvatarBGColour(UserDataService.avatarBGColour))

                loginoutBtnNavHeader.text = "Logout"
                Log.d("USERDATA_CHANGERECEIVER", "everything good here")

                MessageService.findAllChannels{complete->
                    if(complete){
                        if(MessageService.channels.count() > 0){
                            selectedChannel = MessageService.channels[0]
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        }

                    }else{
                        Log.d("GET_CHANNELS", "could not receive channels")
                    }

                }

            }else{
                loginoutBtnNavHeader.text = "Login"
                Log.d("USERDATA_CHANGERECEIVER", "not logged in")
            }
        }
    }

    private val onNewChannel = Emitter.Listener{args ->
        if(App.sharedPrefs.isLoggedIn) {
            runOnUiThread {
                val channelName = args[0] as String
                val channelDescription = args[1] as String
                val channelId = args[2] as String

                val newChannel = Channel(channelName, channelDescription, channelId)
                MessageService.channels.add(newChannel)
                channelAdapter.notifyDataSetChanged()
            }
        }
    }

    fun updateWithChannel() {
        channelNameTxtContent.text = "#${selectedChannel?.name}"

        //download messages for content
        if (selectedChannel != null) {
            MessageService.findAllMsgsByChannel(selectedChannel!!.id){complete->
                if(complete){
                    //display messages
                    messageAdapter.notifyDataSetChanged()
                    if (messageAdapter.itemCount > 0){
                        msgListViewContent.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    }
                }else{
                    Log.d("GET_MESSAGES", "could not get messages")
                }
            }
        }
    }

    private val onNewMessage = Emitter.Listener{args ->
        if(App.sharedPrefs.isLoggedIn) {

            runOnUiThread {
                val channelId = args[2] as String
                if (channelId.equals(selectedChannel?.id)) {
                    val messageBody = args[0] as String
                    //val userId = args[1] as String
                    val userName = args[3] as String
                    val avatarImg = args[4] as String
                    val avatarBGColour = args[5] as String
                    val msgId = args[6] as String
                    val msgTimestamp = args[7] as String

                    val newMessage = Message(messageBody, channelId, userName, avatarImg, avatarBGColour, msgId, msgTimestamp)
                    MessageService.messages.add(newMessage)
                    messageAdapter.notifyDataSetChanged()
                    msgListViewContent.smoothScrollToPosition(messageAdapter.itemCount - 1)
                }
            }
        }
    }


    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginoutBtnNavHeaderClicked(view: View) {
        if(App.sharedPrefs.isLoggedIn) {
            //logout
            UserDataService.logout()

            channelAdapter.notifyDataSetChanged()
            messageAdapter.notifyDataSetChanged()

            usernameTxtNavHeader.text = ""
            userEmailTxtNavHeader.text = ""
            profileIdImgNavHeader.setImageResource(R.drawable.profiledefault)
            profileIdImgNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginoutBtnNavHeader.text = "Login"
            channelNameTxtContent.text = "Please log in"


        }else{
            //login
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun addChannelBtnNavHeaderClicked(view: View) {

        if(App.sharedPrefs.isLoggedIn){
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder.setView(dialogView)
                .setPositiveButton("Add"){_: DialogInterface?, _: Int ->
                    //perform logic when clicked
                    val channelNameTextField = dialogView.findViewById<EditText>(R.id.addChannelNameTextField)
                    val channelDescTextField = dialogView.findViewById<EditText>(R.id.addChannelDescTextField)

                    val channelName = channelNameTextField.text.toString()
                    val channelDesc = channelDescTextField.text.toString()

                    //Create channel
                    socket.emit("newChannel", channelName, channelDesc)

                }
                .setNegativeButton("Cancel"){_: DialogInterface?, _: Int ->
                    //Cancel and close dialog
                }
                .show()

        }else{
            //login first
            Toast.makeText(this, "Must be logged in to make a channel", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendMsgBtnContentClicked(view: View){

        if(App.sharedPrefs.isLoggedIn){
            val messageBody = msgTextFieldContent.text.toString()

            if(messageBody.isNotEmpty()){
                    if(selectedChannel != null) {

                        socket.emit(
                            "newMessage", messageBody, UserDataService.userId, selectedChannel!!.id,
                            UserDataService.name, UserDataService.avatarImg, UserDataService.avatarBGColour
                        )
                        msgTextFieldContent.text.clear()
                        hideKeyboard()
                    }else{
                        Toast.makeText(this, "Must select a channel before sending a message", Toast.LENGTH_SHORT).show()
                    }
            }else{
                Toast.makeText(this, "You cannot send an empty message", Toast.LENGTH_SHORT).show()
            }
        }else{
            //login first
            Toast.makeText(this, "Must be logged in to send a message", Toast.LENGTH_SHORT).show()
        }

    }

    private fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

}
