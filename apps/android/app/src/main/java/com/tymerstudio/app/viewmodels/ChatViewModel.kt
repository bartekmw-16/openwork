package com.tymerstudio.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tymerstudio.app.data.models.Message
import com.tymerstudio.app.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel : ViewModel() {
    private var apiService: ApiService? = null
    private var currentSessionID: String? = null

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending

    fun initialize(baseUrl: String) {
        apiService = ApiService(baseUrl)
        createSession()
    }

    private fun createSession() {
        viewModelScope.launch {
            val service = apiService ?: return@launch

            service.createSession().fold(
                onSuccess = { session ->
                    currentSessionID = session.id
                    // Subscribe to events
                    subscribeToEvents(session.id)
                },
                onFailure = { error ->
                    // Handle error
                    addMessage(
                        Message(
                            id = UUID.randomUUID().toString(),
                            sessionID = "error",
                            role = "assistant",
                            content = "Failed to create session: ${error.message}",
                            timestamp = System.currentTimeMillis(),
                            status = "error"
                        )
                    )
                }
            )
        }
    }

    private fun subscribeToEvents(sessionID: String) {
        viewModelScope.launch {
            val service = apiService ?: return@launch

            service.subscribeToEvents(sessionID).collect { event ->
                // Parse and handle SSE events
                // Update messages based on events
            }
        }
    }

    fun updateInputText(text: String) {
        _inputText.value = text
    }

    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isEmpty() || currentSessionID == null) return

        viewModelScope.launch {
            _isSending.value = true

            // Add user message
            val userMessage = Message(
                id = UUID.randomUUID().toString(),
                sessionID = currentSessionID!!,
                role = "user",
                content = text,
                timestamp = System.currentTimeMillis()
            )
            addMessage(userMessage)

            // Clear input
            _inputText.value = ""

            // Send to API
            val service = apiService ?: return@launch
            service.sendPrompt(
                sessionID = currentSessionID!!,
                text = text
            ).fold(
                onSuccess = {
                    // Message sent successfully
                    // Response will come via SSE events
                },
                onFailure = { error ->
                    addMessage(
                        Message(
                            id = UUID.randomUUID().toString(),
                            sessionID = currentSessionID!!,
                            role = "assistant",
                            content = "Error: ${error.message}",
                            timestamp = System.currentTimeMillis(),
                            status = "error"
                        )
                    )
                }
            )

            _isSending.value = false
        }
    }

    private fun addMessage(message: Message) {
        _messages.value = _messages.value + message
    }
}
