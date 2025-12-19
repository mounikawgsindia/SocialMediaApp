package com.wingspan.aimediahub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wingspan.aimediahub.models.AITextRequest
import com.wingspan.aimediahub.models.ChatMessage
import com.wingspan.aimediahub.repository.ChatRepository
import com.wingspan.aimediahub.ui.theme.Sender
import com.wingspan.aimediahub.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val _messages =
        MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _loading =
        MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun sendMessage(text: String) {

        // 1️⃣ Add USER message immediately
        _messages.value += ChatMessage(
            text = text,
            sender = Sender.USER
        )

        viewModelScope.launch {
            _loading.value = true

            val request = AITextRequest(text)

            repository.sendMessage(request).collect { result ->

                when (result) {

                    is Resource.Loading -> {
                        _loading.value = true
                    }

                    is Resource.Success -> {
                        _loading.value = false

                        // 2️⃣ Add AI response to chat list
                        result.data?.let { aiMessage ->
                            _messages.value += aiMessage.copy(
                                sender = Sender.AI
                            )
                        }
                    }

                    is Resource.Error -> {
                        _loading.value = false

                        _messages.value += ChatMessage(
                            text = result.message ?: "AI error",
                            sender = Sender.AI
                        )
                    }
                }
            }
        }
    }
}
