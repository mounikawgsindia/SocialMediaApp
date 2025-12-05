package com.wingspan.aimediahub.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wingspan.aimediahub.R
import com.wingspan.aimediahub.utils.AppTextStyles.MainHeadingBlack
import com.wingspan.aimediahub.utils.AppTextStyles.SmallBlack
import com.wingspan.aimediahub.utils.AppTextStyles.SmallPrimary


@Composable
fun  RegistrationScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var otpFlag by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars
                .union(WindowInsets.navigationBars)
                .asPaddingValues()),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(
                    color = Color(0xFFF2F2F2),   // light grey background
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                    navController.popBackStack()
                }
                .padding(10.dp)                // inner padding
        ) {
            Icon(
                painter = painterResource(id= R.drawable.back_icon),
                contentDescription = "Back",
                tint = Color.Black, modifier = Modifier.size(24.dp).padding(3.dp)
            )
        }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
            Text(text = "Registration", style = MainHeadingBlack)
            Spacer(modifier = Modifier.height(20.dp))
            //name
            CustomInputField(
                value = name,
                onValueChange = { name = it },
                error = "",
                placeholder = "Enter your name",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomInputField(
                value = email,
                onValueChange = { email = it },
                error = "",
                placeholder = "Enter your email",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            CustomInputField(
                value = password,
                onValueChange = { password = it },
                error = "",
                placeholder = "Enter your password",
                modifier = Modifier.fillMaxWidth(),
                isPassword = true,
                keyboardType = KeyboardType.Password
            )
            Spacer(modifier = Modifier.height(10.dp))
            if(otpFlag) {

                Spacer(modifier = Modifier.height(10.dp))

                CustomInputField(
                    value = password,
                    onValueChange = { password = it },
                    error = "",
                    placeholder = "Enter your password",
                    modifier = Modifier.fillMaxWidth(),
                    isPassword = true,
                    keyboardType = KeyboardType.Password
                )
            }


            GradientButton(
                text = "Login",
                modifier = Modifier.fillMaxWidth()
            ) { }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Don't have an account?", style = SmallBlack)
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Login now",
                    style = SmallPrimary,
                    fontWeight = FontWeight.Bold, modifier = Modifier.clickable{
                        navController.navigate("login")
                    }
                )
            }
        }

    }

}