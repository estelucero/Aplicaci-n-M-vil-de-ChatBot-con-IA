package com.example.interfaz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
  private var messagesList = mutableListOf<Message>()

  inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvMessage: TextView = itemView.findViewById(R.id.tv_message)
    val tvBotMessage: TextView = itemView.findViewById(R.id.tv_bot_message)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.mensajes, parent, false)
    return MessageViewHolder(view)
  }

  override fun getItemCount(): Int {
    return messagesList.size
  }

  override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
    val currentMessage = messagesList[position]

    when (currentMessage.sender) {
      Sender.USER -> {
        holder.tvMessage.apply {
          text = currentMessage.message
          visibility = View.VISIBLE
        }
        holder.tvBotMessage.visibility = View.GONE
      }
      Sender.BOT -> {
        holder.tvBotMessage.apply {
          text = currentMessage.message
          visibility = View.VISIBLE
        }
        holder.tvMessage.visibility = View.GONE
      }
    }
  }

  fun insertMessage(message: Message) {
    this.messagesList.add(message)
    notifyItemInserted(messagesList.size)
  }
}