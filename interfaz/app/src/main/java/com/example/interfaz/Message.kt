package com.example.interfaz

enum class Sender {
  USER,
  BOT
}
data class Message(val message: String, val sender: Sender)