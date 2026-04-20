package com.tymerstudio.app.data.models

data class Message(
    val id: String,
    val sessionID: String,
    val role: String, // "user" or "assistant"
    val content: String,
    val timestamp: Long,
    val status: String = "complete" // "complete", "streaming", "error"
)
