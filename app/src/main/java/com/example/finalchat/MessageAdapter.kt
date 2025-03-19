package com.example.finalchat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(private val messageList: List<Message>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_recieved, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messageList[position]
        holder.messageTextView.text = message.text
        holder.timestampTextView.text = android.text.format.DateFormat.format("hh:mm a", message.timestamp)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}
