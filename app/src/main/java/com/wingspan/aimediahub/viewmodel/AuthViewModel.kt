package com.wingspan.aimediahub.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.wingspan.aimediahub.models.LoginRequest
import com.wingspan.aimediahub.models.LoginResponse
import com.wingspan.aimediahub.models.OtpVerifyRequest
import com.wingspan.aimediahub.models.RegisterRequest
import com.wingspan.aimediahub.models.ResponseData
import com.wingspan.aimediahub.repository.AuthRepository
import com.wingspan.aimediahub.utils.Prefs
import com.wingspan.aimediahub.utils.Resource

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.text.isBlank

@HiltViewModel
open class AuthViewModel  @Inject constructor(private val repository: AuthRepository, private val prefs: Prefs): ViewModel() {



//    var email by mutableStateOf("")
//    var emailError by mutableStateOf<String?>(null)


    //signup related variables
    var name by mutableStateOf("")
    var nameError by mutableStateOf<String?>(null)


    var email by mutableStateOf("")
    var emailError by mutableStateOf<String?>(null)

    var password by mutableStateOf("")
    var passwordError by mutableStateOf<String?>(null)

    var mobileNumber by mutableStateOf("")
    var mobileNumberError by mutableStateOf<String?>(null)


    var otpNumber by mutableStateOf("")
    var otpNumbererror by mutableStateOf<String?>(null)




    private val _loginState =
        MutableStateFlow<Resource<LoginResponse>?>(null)
    val loginState: StateFlow<Resource<LoginResponse>?> = _loginState.asStateFlow()



    private val _registrationState =
        MutableStateFlow<Resource<ResponseData>?>(null)
    val registrationState: StateFlow<Resource<ResponseData>?> = _registrationState.asStateFlow()

    private val _otpStatus =
        MutableStateFlow<Resource<ResponseData>?>(null)
    val otpStatus: StateFlow<Resource<ResponseData>?> = _otpStatus.asStateFlow()

    fun login() {
        viewModelScope.launch {
            var request = LoginRequest(email,password)

            repository.login(request).collect { response ->
                Log.d("data","--->${response}....")
                response.data?.let {data->
                    Log.d("data","--->${response}....${data.userId.toString()}")
                    prefs.saveLoginData(data.userId.toString(), data.username.toString(),
                        data.email.toString(), data.token.toString(), data.mobile.toString())
                }
                _loginState.value = response


            }
        }
    }

    fun registration() {
        val request = RegisterRequest(name, email, mobileNumber, password)
        Log.d("Registration", "Request: $request") // Log request data

        viewModelScope.launch {
            repository.registration(request).collect { response ->
                Log.d("Registration", "Response: $response") // Log response data
                _registrationState.value = response
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun loginValidate(): Boolean{

     var isValid = true

         if (email.isBlank()) {
            emailError = "Email cannot be empty"
            isValid = false
        } else {
            emailError = null
        }


         if (password.isBlank()) {
            passwordError = "Password cannot be empty"
            isValid = false
         } else {
            passwordError = null
        }

        return isValid
    }

    fun validate(): Boolean {
        var isValid = true
        if (name.isBlank()) {
            nameError = "Name cannot be empty"
            isValid = false
        } else {
            nameError = null
        }
        if (email.isBlank()) {
            emailError = "Email cannot be empty"
            isValid = false
        } else {
            emailError = null
        }
        if (mobileNumber.isBlank()) {
            mobileNumberError = "Pincode cannot be empty"
            isValid = false
        } else {
            mobileNumberError = null
        }

        if (password.isBlank()) {
            passwordError = "Password cannot be empty"
            isValid = false
        } else {
            passwordError = null
        }


        return isValid
    }


    fun validOtp(): Boolean {
        var isValid = true
        if (otpNumber.isBlank()) {
            otpNumbererror = "OTP cannot be empty"
            isValid = false
        } else {
            otpNumbererror = null
        }

        return isValid
    }

    fun verifyOtp(){
        Log.d("otp verify", "Request: $otpNumber") // Log request data
        val request = OtpVerifyRequest(email, otpNumber)
        viewModelScope.launch {
            repository.otpVerify(request).collect { response ->
                Log.d("otp", "Response: ${response.data?.msg}") // Log response data
                _otpStatus.value = response
            }
        }
    }



}

