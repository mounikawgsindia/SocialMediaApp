package com.wingspan.aimediahub.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.wingspan.aimediahub.models.LoginRequest
import com.wingspan.aimediahub.models.ResponseData
import com.wingspan.aimediahub.repository.AuthRepository
import com.wingspan.aimediahub.utils.Resource

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.text.isBlank

@HiltViewModel
open class AuthViewModel  @Inject constructor(private val repository: AuthRepository): ViewModel() {



//    var email by mutableStateOf("")
//    var emailError by mutableStateOf<String?>(null)


    //signup related variables
    var name by mutableStateOf("")
    var companyName by mutableStateOf("")

    var nameError by mutableStateOf<String?>(null)
    var companyNameError by mutableStateOf<String?>(null)

    var mobileNumber by mutableStateOf("")
    var mobileNumberError by mutableStateOf<String?>(null)


    var otpNumber by mutableStateOf("")
    var otpNumbererror by mutableStateOf<String?>(null)




    private val _loginState =
        MutableStateFlow<Resource<ResponseData>?>(null)
    val loginState: StateFlow<Resource<ResponseData>?> = _loginState.asStateFlow()



    fun login(request: LoginRequest) {
        viewModelScope.launch {
            repository.login(request).collect { _loginState.value = it }
        }
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



}

