package com.tutorials.agriconnect

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class FirebaseAuthManager {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "FirebaseAuthManager"

    // Store verification ID for phone auth
    private var storedVerificationId: String? = null

    // Check if user is logged in
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // Get current user
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // Register with email and password
    suspend fun registerWithEmail(
        email: String,
        password: String,
        userData: Map<String, Any>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()

            // Save additional user data to Firestore
            result.user?.uid?.let { uid ->
                saveUserData(uid, userData)
                onSuccess()
            } ?: onError("Failed to create user")
        } catch (e: Exception) {
            Log.e(TAG, "Register failed: ${e.message}")
            val errorMessage = when (e) {
                is com.google.firebase.auth.FirebaseAuthWeakPasswordException -> "Password is too weak. Use at least 6 characters."
                is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Invalid email format."
                is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "Email already in use. Try logging in."
                else -> e.message ?: "Registration failed"
            }
            onError(errorMessage)
        }
    }

    // Login with email and password
    suspend fun loginWithEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            Log.d(TAG, "Attempting Firebase login with email: $email")
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Log.d(TAG, "Firebase login result received")

            if (result.user != null) {
                Log.d(TAG, "Login successful, user: ${result.user?.uid}")
                onSuccess()
            } else {
                Log.e(TAG, "Login result has null user")
                onError("Login failed: User is null")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login failed with exception: ${e.javaClass.simpleName}: ${e.message}")
            // Provide more user-friendly error messages
            val errorMessage = when (e) {
                is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "Account doesn't exist"
                is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Invalid email or password"
                else -> e.message ?: "Login failed"
            }
            onError(errorMessage)
        }
    }

    // Send phone verification code
    // Change this method signature
// Change this method signature
    fun sendPhoneVerificationCode(
        activity: androidx.activity.ComponentActivity,  // Changed from Context
        phoneNumber: String,
        onCodeSent: () -> Unit,
        onVerificationFailed: (String) -> Unit
    ) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            // No changes needed in the callbacks
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: $credential")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e(TAG, "onVerificationFailed: ${e.message}")
                onVerificationFailed(e.message ?: "Verification failed")
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d(TAG, "onCodeSent: $verificationId")
                storedVerificationId = verificationId
                onCodeSent()
            }
        }

        val formattedPhoneNumber = if (!phoneNumber.startsWith("+")) {
            "+91$phoneNumber" // Assuming India country code, adjust as needed
        } else {
            phoneNumber
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(formattedPhoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)  // No need for casting now
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Verify phone code
    suspend fun verifyPhoneCode(
        code: String,
        userData: Map<String, Any>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            storedVerificationId?.let { verificationId ->
                val credential = PhoneAuthProvider.getCredential(verificationId, code)
                val result = auth.signInWithCredential(credential).await()

                // Save user data to Firestore
                result.user?.uid?.let { uid ->
                    saveUserData(uid, userData)
                    onSuccess()
                } ?: onError("Failed to verify code")
            } ?: onError("Verification ID not found")
        } catch (e: Exception) {
            Log.e(TAG, "Verify code failed: ${e.message}")
            val errorMessage = when (e) {
                is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Invalid verification code"
                else -> e.message ?: "Verification failed"
            }
            onError(errorMessage)
        }
    }

    // Save user data to Firestore
    private suspend fun saveUserData(userId: String, userData: Map<String, Any>) {
        try {
            db.collection("users").document(userId).set(userData).await()
            Log.d(TAG, "User data saved successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving user data: ${e.message}")
            // We'll continue even if data saving fails
        }
    }

    // Get user data from Firestore
    suspend fun getUserData(
        onSuccess: (Map<String, Any>) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            val document = db.collection("users").document(userId).get().await()
            if (document.exists()) {
                onSuccess(document.data ?: mapOf())
            } else {
                onError("User data not found")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user data: ${e.message}")
            onError(e.message ?: "Failed to get user data")
        }
    }

    // Logout
    fun logout() {
        auth.signOut()
    }
}