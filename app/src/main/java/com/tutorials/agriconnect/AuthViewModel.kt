package com.tutorials.agriconnect

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    object OtpSent : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val authManager = FirebaseAuthManager()
    private val TAG = "AuthViewModel"

    // Create an exception handler for coroutines
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(TAG, "Coroutine exception: ${exception.message}", exception)
        viewModelScope.launch(Dispatchers.Main) {
            _authState.value = AuthState.Error(exception.message ?: "An unexpected error occurred")
        }
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // Check if user is logged in
    fun isUserLoggedIn(): Boolean {
        return authManager.isUserLoggedIn()
    }

    // Get current user
    fun getCurrentUser() = authManager.getCurrentUser()

    // Register with email and password
    fun registerWithEmail(email: String, password: String, userData: Map<String, Any>) {
        Log.d(TAG, "Attempting registration with email: $email")
        _authState.value = AuthState.Loading

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            try {
                authManager.registerWithEmail(
                    email = email,
                    password = password,
                    userData = userData,
                    onSuccess = {
                        viewModelScope.launch(Dispatchers.Main) {
                            Log.d(TAG, "Registration successful")
                            _authState.value = AuthState.Success
                        }
                    },
                    onError = { errorMessage ->
                        viewModelScope.launch(Dispatchers.Main) {
                            Log.e(TAG, "Registration error: $errorMessage")
                            _authState.value = AuthState.Error(errorMessage)
                        }
                    }
                )
            } catch (e: Exception) {
                viewModelScope.launch(Dispatchers.Main) {
                    Log.e(TAG, "Exception in registerWithEmail: ${e.message}", e)
                    _authState.value = AuthState.Error(e.message ?: "Registration failed")
                }
            }
        }
    }

    // Login with email and password
    fun loginWithEmail(email: String, password: String) {
        Log.d(TAG, "Attempting login with email: $email")
        _authState.value = AuthState.Loading

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            try {
                Log.d(TAG, "Calling authManager.loginWithEmail")
                authManager.loginWithEmail(
                    email = email,
                    password = password,
                    onSuccess = {
                        viewModelScope.launch(Dispatchers.Main) {
                            Log.d(TAG, "Login successful")
                            _authState.value = AuthState.Success
                        }
                    },
                    onError = { errorMessage ->
                        viewModelScope.launch(Dispatchers.Main) {
                            Log.e(TAG, "Login error: $errorMessage")
                            _authState.value = AuthState.Error(errorMessage)
                        }
                    }
                )
            } catch (e: Exception) {
                viewModelScope.launch(Dispatchers.Main) {
                    Log.e(TAG, "Exception in loginWithEmail: ${e.message}", e)
                    _authState.value = AuthState.Error(e.message ?: "Unknown error")
                }
            }
        }
    }

    // Send phone verification code - updated to take ComponentActivity
    fun sendPhoneVerificationCode(activity: ComponentActivity, phoneNumber: String) {
        Log.d(TAG, "Sending verification code to phone: $phoneNumber")
        _authState.value = AuthState.Loading

        try {
            authManager.sendPhoneVerificationCode(
                activity = activity,
                phoneNumber = phoneNumber,
                onCodeSent = {
                    viewModelScope.launch(Dispatchers.Main) {
                        Log.d(TAG, "OTP code sent successfully")
                        _authState.value = AuthState.OtpSent
                    }
                },
                onVerificationFailed = { errorMessage ->
                    viewModelScope.launch(Dispatchers.Main) {
                        Log.e(TAG, "Phone verification failed: $errorMessage")
                        _authState.value = AuthState.Error(errorMessage)
                    }
                }
            )
        } catch (e: Exception) {
            viewModelScope.launch(Dispatchers.Main) {
                Log.e(TAG, "Exception in sendPhoneVerificationCode: ${e.message}", e)
                _authState.value = AuthState.Error(e.message ?: "Failed to send verification code")
            }
        }
    }

    // Verify phone code
    fun verifyPhoneCode(code: String, userData: Map<String, Any>) {
        Log.d(TAG, "Verifying OTP code")
        _authState.value = AuthState.Loading

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            try {
                authManager.verifyPhoneCode(
                    code = code,
                    userData = userData,
                    onSuccess = {
                        viewModelScope.launch(Dispatchers.Main) {
                            Log.d(TAG, "OTP verification successful")
                            _authState.value = AuthState.Success
                        }
                    },
                    onError = { errorMessage ->
                        viewModelScope.launch(Dispatchers.Main) {
                            Log.e(TAG, "OTP verification error: $errorMessage")
                            _authState.value = AuthState.Error(errorMessage)
                        }
                    }
                )
            } catch (e: Exception) {
                viewModelScope.launch(Dispatchers.Main) {
                    Log.e(TAG, "Exception in verifyPhoneCode: ${e.message}", e)
                    _authState.value = AuthState.Error(e.message ?: "Verification failed")
                }
            }
        }
    }

    // Get user data
    fun getUserData(
        onSuccess: (Map<String, Any>) -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d(TAG, "Getting user data")
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            try {
                authManager.getUserData(
                    onSuccess = { userData ->
                        viewModelScope.launch(Dispatchers.Main) {
                            Log.d(TAG, "User data retrieved successfully")
                            onSuccess(userData)
                        }
                    },
                    onError = { errorMessage ->
                        viewModelScope.launch(Dispatchers.Main) {
                            Log.e(TAG, "Error getting user data: $errorMessage")
                            onError(errorMessage)
                        }
                    }
                )
            } catch (e: Exception) {
                viewModelScope.launch(Dispatchers.Main) {
                    Log.e(TAG, "Exception in getUserData: ${e.message}", e)
                    onError(e.message ?: "Failed to get user data")
                }
            }
        }
    }

    // Logout
    fun logout() {
        Log.d(TAG, "Logging out user")
        authManager.logout()
        _authState.value = AuthState.Idle
    }

    // Reset state (useful for UI resets)
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}