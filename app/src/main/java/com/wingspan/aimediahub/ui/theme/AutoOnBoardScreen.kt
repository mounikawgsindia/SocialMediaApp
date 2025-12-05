package com.wingspan.aimediahub.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.wingspan.aimediahub.models.OnBoardModel
import kotlinx.coroutines.delay

@Composable
fun AutoOnBoardScreen(
    onBoardList: List<OnBoardModel>,
    onSkip: () -> Unit,
    onFinish: () -> Unit
) {
    val totalPages = onBoardList.size
    var currentPage by remember { mutableStateOf(0) }

    // Auto scroll every 3 seconds
    LaunchedEffect(key1 = currentPage) {
        delay(3000)
        currentPage = if (currentPage < totalPages - 1) currentPage + 1 else 0
    }

    val item = onBoardList[currentPage]

    OnBoardScreen(
        title = item.title,
        image = item.image,
        currentPage = currentPage,
        onNext = {
            currentPage = if (currentPage < totalPages - 1) currentPage + 1 else currentPage
            if (currentPage == totalPages - 1) onFinish()
        },
        onSkip = onSkip,
        description = item.description // Add this parameter in OnBoardScreen
    )
}
