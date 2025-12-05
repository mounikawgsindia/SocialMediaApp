package com.wingspan.aimediahub.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.wingspan.aimediahub.ui.theme.LightSkyBlue
import com.wingspan.aimediahub.ui.theme.SoftLavender
import com.wingspan.aimediahub.ui.theme.TextGrey
import com.wingspan.aimediahub.ui.theme.greytext

import com.wingspan.aimediahub.ui.theme.textColor


object AppTextStyles {

    val MainHeadingPrimary = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = SoftLavender,
        textAlign = TextAlign.Center
    )

    val MainHeadingBlack = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color =Color.Black,
        textAlign = TextAlign.Center
    )

    val HeadingBlack = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = greytext,
        textAlign = TextAlign.Center
    )

    val HeadingPrimary = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = LightSkyBlue,
        textAlign = TextAlign.Center
    )

    val NormalPrimary = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        color = LightSkyBlue
    )

    val NormalGrey = TextStyle(
        fontSize = 16.sp,
        color = Color.Gray
    )
    val SmallBlack = TextStyle(
        fontSize = 14.sp,
        color = greytext,
    )
    val SmallPrimary = TextStyle(
        fontSize = 14.sp,
        color = SoftLavender,
    )
    val NormalWhite = TextStyle(
        fontSize = 16.sp,
        color = Color.White
    )
    val NormalBlack = TextStyle(
        fontSize = 16.sp,
        color = greytext,
    )


    val SmallGrey = TextStyle(
        fontSize = 12.sp,
        color = Color(0xFF7C7E7C)
    )


}
