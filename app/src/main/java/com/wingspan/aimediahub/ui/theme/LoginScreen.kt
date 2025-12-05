package com.wingspan.aimediahub.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wingspan.aimediahub.utils.AppTextStyles.MainHeadingPrimary
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.wingspan.aimediahub.utils.AppTextStyles.MainHeadingBlack
import com.wingspan.aimediahub.utils.AppTextStyles.NormalBlack
import com.wingspan.aimediahub.utils.AppTextStyles.SmallBlack
import com.wingspan.aimediahub.utils.AppTextStyles.SmallGrey
import com.wingspan.aimediahub.utils.AppTextStyles.SmallPrimary
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars
                .union(WindowInsets.navigationBars)
                .asPaddingValues()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Welcome Back!", style = MainHeadingBlack)
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

            Text(
                text = "Forgot password?",
                style = SmallBlack,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))

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
                    text = "Register now",
                    style = SmallPrimary,
                    fontWeight = FontWeight.Bold, modifier = Modifier.clickable{
                        navController.navigate("registration")
                    }
                )
            }
        }
    }
}



@Composable
fun CustomInputField(
    value: String,
    error: String?,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    isPassword: Boolean = false,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    maxLength: Int? = null,
    onlyDigits: Boolean = false,
    onClick: (() -> Unit)? = null
) {

    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFEFEFEF), // Light Grey
                    shape = RoundedCornerShape(10.dp)
                )

                .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                BasicTextField(
                    value = value,
                    onValueChange = {
                        if (onClick != null) return@BasicTextField

                        var filtered = it
                        if (onlyDigits) filtered = filtered.filter { ch -> ch.isDigit() }
                        if (maxLength != null) filtered = filtered.take(maxLength)

                        onValueChange(filtered)
                    },

                    singleLine = true,
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    visualTransformation =
                        if (isPassword && !passwordVisible)
                            PasswordVisualTransformation()
                        else
                            VisualTransformation.None,
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (value.isEmpty()) {
                            Text(
                                placeholder,
                                color = Color(0xFF9E9E9E),
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                )

                // 👁 Password Toggle Icon
                if (isPassword) {
                    Icon(
                        imageVector = if (passwordVisible)
                            Icons.Default.Visibility
                        else
                            Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Password",
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { passwordVisible = !passwordVisible },
                        tint = Color.Gray
                    )
                }
            }
        }

        if (!error.isNullOrEmpty()) {
            Text(
                text = error,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
