package com.wingspan.aimediahub.ui.theme.auth

import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.wingspan.aimediahub.R
import com.wingspan.aimediahub.ui.theme.GradientButton
import com.wingspan.aimediahub.utils.AppTextStyles.MainHeadingBlack
import com.wingspan.aimediahub.utils.AppTextStyles.SmallBlack
import com.wingspan.aimediahub.utils.AppTextStyles.SmallPrimary
import com.wingspan.aimediahub.utils.NetworkUtils
import com.wingspan.aimediahub.utils.Prefs
import com.wingspan.aimediahub.utils.Resource
import com.wingspan.aimediahub.viewmodel.AuthViewModel


@Composable
fun  RegistrationScreen(navController: NavHostController, viewmodel: AuthViewModel = hiltViewModel()) {


    var otp by remember { mutableStateOf("") }
    var otpFlag by remember { mutableStateOf(false)}
    var loader by remember { mutableStateOf(false) }
    var registerStatus = viewmodel.registrationState.collectAsState()
    var otpStatus = viewmodel.otpStatus.collectAsState()
    var context= LocalContext.current
    val scrollState = rememberScrollState() // scroll state for the page

    LaunchedEffect(registerStatus.value) {
        when (val result = registerStatus.value) {
            is Resource.Success -> {
                // Registration successful
                otpFlag = true
                loader=false
               Toast.makeText(context, "${result.data?.msg}", Toast.LENGTH_SHORT).show()
            }
            is Resource.Error -> {
                loader=false
                // Registration failed

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
    LaunchedEffect(otpStatus.value) {
        when (val result = otpStatus.value) {
            is Resource.Success -> {
                loader=false
                Log.d("Registration", "Response: $result")
                navController.navigate("login")
                Toast.makeText(context, "${result.data?.msg}", Toast.LENGTH_SHORT).show()

            }
            is Resource.Error -> {
                loader=false
                Log.d("Registration", "Response err: $result")
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
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(
                    color = Color(0xFFF2F2F2),   // light grey background
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                    navController.navigateUp()
                }
                .padding(10.dp)                // inner padding
        ) {
            Icon(
                painter = painterResource(id= R.drawable.back_icon),
                contentDescription = "Back",
                tint = Color.Black, modifier = Modifier.size(24.dp).padding(3.dp)
            )
        }

        Column(modifier = Modifier.verticalScroll(scrollState).padding(72.dp).imePadding(), horizontalAlignment = Alignment.CenterHorizontally,  verticalArrangement = Arrangement.Center){
            Text(text = "Registration", style = MainHeadingBlack)
            Spacer(modifier = Modifier.height(20.dp))
            //name
            CustomInputField(
                value = viewmodel.name,
                onValueChange = { viewmodel.name = it
                    viewmodel.nameError = null},
                error = viewmodel.nameError,
                placeholder = "Enter your name",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            // mobile field
            CustomInputField(
                value = viewmodel.mobileNumber,
                error = viewmodel.mobileNumberError,
                onValueChange = {
                    viewmodel.mobileNumber = it
                    viewmodel.mobileNumberError = null
                },
                placeholder = "Enter your mobile number",
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Number,
                maxLength = 10,
                onlyDigits = true
            )
            Spacer(modifier = Modifier.height(20.dp))

            CustomInputField(
                value = viewmodel.email,
                onValueChange = { viewmodel.email = it
                    viewmodel.emailError = null   },
                error =viewmodel.emailError,
                placeholder = "Enter your email",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            CustomInputField(
                value = viewmodel.password,
                onValueChange = { viewmodel.password = it
                    viewmodel.passwordError = null  },
                error = viewmodel.passwordError,
                placeholder = "Enter your password",
                modifier = Modifier.fillMaxWidth(),
                isPassword = true,
                keyboardType = KeyboardType.Password
            )
            Spacer(modifier = Modifier.height(10.dp))


            GradientButton(
                text = "Register",
                modifier = Modifier.fillMaxWidth(),onClick={
                   if(viewmodel.validate()) {

                       if(NetworkUtils.isNetworkAvailable(context)){
                           viewmodel.registration()
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
                    text = "Login now",
                    style = SmallPrimary,
                    fontWeight = FontWeight.Bold, modifier = Modifier.clickable{
                        navController.navigate("login")
                    }
                )
            }

            if(otpFlag) {
                Spacer(modifier = Modifier.height(10.dp))
                CustomInputField(
                    value = viewmodel.otpNumber,
                    onValueChange = { viewmodel.otpNumber = it
                        viewmodel.otpNumbererror=null},
                    error = viewmodel.otpNumbererror,
                    placeholder = "Enter your Otp",
                    modifier = Modifier.fillMaxWidth()

                )

                Spacer(modifier = Modifier.height(10.dp))


                GradientButton(
                    text = "Verify Otp",
                    modifier = Modifier.fillMaxWidth(),onClick={
                        if(viewmodel.validOtp()) {

                            if(NetworkUtils.isNetworkAvailable(context)){
                                viewmodel.verifyOtp()
                            }else{
                                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }


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