package com.example.smack.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.smack.Model.Message
import com.example.smack.R
import com.example.smack.Services.UserDataService

class MessageAdapter(val context : Context, val messages : ArrayList<Message> ) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bindMessage(context, messages[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val userImg = itemView?.findViewById<ImageView>(R.id.msgUserImg)
        val userName = itemView?.findViewById<TextView>(R.id.msgUserNameLbl)
        val timeStamp = itemView?.findViewById<TextView>(R.id.msgTimestampLbl)
        val messageBody = itemView?.findViewById<TextView>(R.id.msgTextLbl)

        fun bindMessage(context: Context, message: Message){
            val resourceId = context.resources.getIdentifier(message.avatarImg, "drawable", context.packageName)
            userImg?.setImageResource(resourceId)
            userImg?.setBackgroundColor(UserDataService.getAvatarBGColour(message.avatarBGColour))

            userName?.text = message.userName
            timeStamp?.text = message.msgTimestamp
            messageBody?.text = message.message
        }
    }
}