package com.wingspan.aimediahub.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.wingspan.aimediahub.models.ChatMessage
import com.wingspan.aimediahub.viewmodel.ChatViewModel

@Composable
fun AIChatPage(
    navController: NavController,
    topic: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(topic) {
        if (topic.isNotBlank()) {
            viewModel.sendMessage(topic)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing) // handles status bar & cutouts
    ) {

        // Chat messages
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.ime) // moves above keyboard automatically
        ) {
            ChatList(messages, onSelectText = {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("ai_text", it)

                // Go back
                navController.popBackStack()
            })

            if (loading) {
                Text(
                    text = "AI is typing...",
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                )
            }
        }

        Divider()

        // Input bar fixed at bottom, keyboard + nav bar safe
        ChatInput(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars) // avoids system nav buttons
                .padding(horizontal = 8.dp)
        ) { text ->
            viewModel.sendMessage(text)
        }
    }
}


@Composable
fun ChatList(messages: List<ChatMessage>, onSelectText: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 70.dp)
    ) {
        items(messages.size) { index ->
            var message=messages[index]
            ChatBubble(message,  onSelectText = onSelectText)
        }
    }
}
@Composable
fun ChatBubble(
    message: ChatMessage,
    onSelectText: (String) -> Unit
) {
    val isUser = message.sender == Sender.USER

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {

        // Message bubble
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 0.dp,
                        bottomEnd = if (isUser) 0.dp else 16.dp
                    )
                )
                .background(
                    if (isUser) Color(0xFFDCF8C6) else Color(0xFFF1F1F1)
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                fontSize = 14.sp,
                color = Color.Black
            )
        }

        // 👇 Only show “Select text” button for AI messages
        if (!isUser) {
            Text(
                text = "Select text",
                fontSize = 12.sp,
                color = Color(0xFF1E88E5),
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { onSelectText(message.text) }
            )
        }
    }
}

@Composable
fun ChatInput(
    modifier: Modifier = Modifier,
    onSend: (String) -> Unit
) {
    var input by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically
    ) {

        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            placeholder = { Text("Ask AI...") },
            modifier = Modifier.weight(1f),
            maxLines = 4
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            enabled = input.isNotBlank(),
            onClick = {
                onSend(input)
                input = ""
            }
        ) {
            Icon(Icons.Default.Send, contentDescription = "Send")
        }
    }
}

enum class Sender {
    USER, AI
}