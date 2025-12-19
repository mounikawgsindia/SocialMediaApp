package com.wingspan.aimediahub.ui.theme.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.wingspan.aimediahub.ui.theme.GradientButton
import com.wingspan.aimediahub.utils.AppTextStyles.MainHeadingBlack
import com.wingspan.aimediahub.utils.AppTextStyles.SmallBlack
import com.wingspan.aimediahub.utils.AppTextStyles.SmallPrimary
import com.wingspan.aimediahub.utils.NetworkUtils
import com.wingspan.aimediahub.utils.Prefs
import com.wingspan.aimediahub.utils.Resource
import com.wingspan.aimediahub.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavController, viewmodel: AuthViewModel = hiltViewModel()) {
    var loginState = viewmodel.loginState.collectAsState()
    var loader by remember { mutableStateOf(false) }
    var context = LocalContext.current


    LaunchedEffect(loginState.value) {
        when (val result = loginState.value) {
            is Resource.Success -> {
                loader=false

                navController.navigate("main"){
                    popUpTo("login"){inclusive=true}
                    launchSingleTop = true
                }
                Toast.makeText(context, "${result.data?.msg}", Toast.LENGTH_SHORT).show()
            }
            is Resource.Error -> {
                loader=false
                Toast.makeText(context, result.message ?: "Unknown Error", Toast.LENGTH_SHORT).show()
            }
            is Resource.Loading -> {
                loader=true
            }
            null -> {
                // Initial state, do nothing
            }
        }
    }
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
                value = viewmodel.email,
                onValueChange = { viewmodel.email = it
                    viewmodel.emailError=null
                                },
                error = viewmodel.emailError,
                placeholder = "Enter your email",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            CustomInputField(
                value = viewmodel.password,
                onValueChange = { viewmodel.password = it
                                viewmodel.passwordError=null},
                error =  viewmodel.passwordError,
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
                modifier = Modifier.fillMaxWidth(), onClick = @androidx.annotation.RequiresPermission(
                    android.Manifest.permission.ACCESS_NETWORK_STATE
                ) {
                    Log.d("data","${viewmodel.validate()}")
                    if(viewmodel.loginValidate()){
                        if(NetworkUtils.isNetworkAvailable(context)){
                            viewmodel.login()
                        }else{
                            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            )

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
            Spacer(modifier = Modifier.height(10.dp))
            if(loader){
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp),
                    color = Color.Blue

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
