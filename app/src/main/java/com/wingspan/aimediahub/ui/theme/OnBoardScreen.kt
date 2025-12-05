package com.wingspan.aimediahub.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnBoardScreen(
    title: String,
    image: Int,
    currentPage: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    description: String
) {
    Box(modifier = Modifier.fillMaxSize().padding(WindowInsets.statusBars
        .union(WindowInsets.navigationBars)
        .asPaddingValues())) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Top Section: Image + Title + Description
            Column(
                modifier = Modifier.padding(top = 120.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp) // small spacing
            ) {
                Image(
                    painter = painterResource(id = image),
                    contentDescription = "",
                    modifier = Modifier.size(250.dp)
                )

                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Text(
                    text = description,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp)) // optional small spacing

            // Dots Indicator
            DotsIndicator(totalDots = 3, selectedIndex = currentPage)

            Spacer(modifier = Modifier.weight(1f)) // push buttons to bottom

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GradientButton(
                    text = "Skip",
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    onClick = { onSkip() }
                )

                GradientButton(
                    text = "Next",
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    onClick = { onNext() }
                )
            }
        }
    }
}


@Composable
fun DotsIndicator(totalDots: Int, selectedIndex: Int, modifier: Modifier = Modifier,
                  selectedGradient: Brush = Brush.horizontalGradient(listOf(LightSkyBlue, SoftLavender)),
                  unSelectedColor: Color = Color.LightGray) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        repeat(totalDots) { index ->
            val isSelected = index == selectedIndex

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(if (index == selectedIndex) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) selectedGradient else SolidColor(unSelectedColor)
                    )
            )
        }
    }

}
