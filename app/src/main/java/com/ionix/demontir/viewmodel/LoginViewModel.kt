package com.ionix.demontir.viewmodel

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.ionix.demontir.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: AppRepository) : ViewModel() {
    val emailState = mutableStateOf("")
    val passwordState = mutableStateOf("")
    val showPassword = mutableStateOf(false)
    val isFirstTimeEnteringApp = mutableStateOf<Boolean?>(null)
    val showErrorSnackbar = mutableStateOf(false)
    val errorMessage = mutableStateOf("")

    fun loginWithGoogle(launcher: ActivityResultLauncher<IntentSenderRequest>) {
        viewModelScope.launch {
            repository.loginWithGoogle(launcher)
        }
    }

    fun loginWithCredential(credential: AuthCredential) = repository.loginWithCredential(credential)

    fun loginWithEmailPassword(onSuccess: () -> Unit, onFailed: () -> Unit) =
        repository.loginWithEmailPassword(emailState.value, passwordState.value)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailed() }

    fun getFirstTimeEnteringAppState() {
        viewModelScope.launch {
            repository.isEnteringAppFirstTime().collect {
                isFirstTimeEnteringApp.value = it
            }
        }
    }

    fun saveUserInfoToFirestore(
        onSuccess: () -> Unit, onFailed: () -> Unit
    ) = repository.saveUserInfo(
        onSuccess,
        onFailed,
        "https://storage.needpix.com/rsynced_images/attribution-icon-2888829_1280.png",
        emailState.value
    )

    init {
        getFirstTimeEnteringAppState()
    }
}