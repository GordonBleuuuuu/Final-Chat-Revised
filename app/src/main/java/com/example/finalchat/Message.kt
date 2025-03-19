package com.example.finalchat

data class Message(
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0,
    val message: String
)
