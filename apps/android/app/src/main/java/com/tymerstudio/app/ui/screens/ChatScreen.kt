package com.tymerstudio.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tymerstudio.app.data.models.Message
import com.tymerstudio.app.viewmodels.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onDisconnect: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val isSending by viewModel.isSending.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tymer Studio") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = onDisconnect) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Disconnect")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = viewModel::updateInputText,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...") },
                        enabled = !isSending,
                        maxLines = 4
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { viewModel.sendMessage() },
                        enabled = inputText.isNotBlank() && !isSending
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send")
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages.reversed()) { message ->
                MessageBubble(message = message)
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    val isUser = message.role == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium
                )
                if (message.status == "streaming") {
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
