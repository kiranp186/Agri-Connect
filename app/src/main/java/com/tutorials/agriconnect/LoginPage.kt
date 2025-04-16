package com.tutorials.agriconnect

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.*
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// User data model
// Update your UserData class with nullable fields and proper types
data class UserData(
    val uid: String = "",
    val username: String = "",
    val phoneNumber: String? = null,
    val email: String? = null,
    val userType: String = "",
    val state: String = "",
    val district: String = "",
    val taluk: String = "",
    val address: String = "",
    val authType: String = "",
    // Handle Timestamp conversion properly
    val createdAt: com.google.firebase.Timestamp? = null
)

// Singleton object to store current user data throughout the app
object UserSession {
    var currentUser: UserData? = null
}

class LoginPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LoginScreen(
                    onLoginClick = {
                        // Navigate to main screen or dashboard
                        // For example: startActivity(Intent(this, DashboardActivity::class.java))
                    }
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    onSignUpClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    // Authentication Type
    var authType by remember { mutableStateOf("Phone") } // "Phone" or "Email"

    val context = LocalContext.current

    // Firebase instances
    val auth = try {
        FirebaseAuth.getInstance()
    } catch (e: Exception) {
        Toast.makeText(context, "Firebase Auth initialization error: ${e.message}", Toast.LENGTH_LONG).show()
        null
    }

    val db = try {
        FirebaseFirestore.getInstance()
    } catch (e: Exception) {
        Toast.makeText(context, "Firestore initialization error: ${e.message}", Toast.LENGTH_LONG).show()
        null
    }

    // State variables
    var username by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var otpSent by remember { mutableStateOf(false) }
    var otp by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Function to fetch user data
    // Updated fetchUserData function with better error handling
    fun fetchUserData(userId: String) {
        if (db == null) {
            Toast.makeText(context, "Database not initialized", Toast.LENGTH_LONG).show()
            return
        }

        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val documentSnapshot = db.collection("users").document(userId).get().await()

                if (documentSnapshot.exists()) {
                    try {
                        // Try to convert to UserData class
                        val userData = documentSnapshot.toObject(UserData::class.java)

                        // Store in session
                        userData?.let {
                            // Make sure to set the uid if it's not already set
                            val updatedUserData = it.copy(uid = userId)
                            UserSession.currentUser = updatedUserData

                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(context, "Welcome, ${it.username}!", Toast.LENGTH_SHORT).show()
                                onLoginClick()
                            }
                        }
                    } catch (e: Exception) {
                        // If automatic conversion fails, try manual mapping
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(context, "Error converting user data: ${e.message}", Toast.LENGTH_LONG).show()
                            try {
                                // Manual mapping as fallback
                                val userData = UserData(
                                    uid = userId,
                                    username = documentSnapshot.getString("username") ?: "",
                                    phoneNumber = documentSnapshot.getString("phoneNumber"),
                                    email = documentSnapshot.getString("email"),
                                    userType = documentSnapshot.getString("userType") ?: "",
                                    state = documentSnapshot.getString("state") ?: "",
                                    district = documentSnapshot.getString("district") ?: "",
                                    taluk = documentSnapshot.getString("taluk") ?: "",
                                    address = documentSnapshot.getString("address") ?: "",
                                    authType = documentSnapshot.getString("authType") ?: "",
                                    createdAt = documentSnapshot.getTimestamp("createdAt")
                                )

                                UserSession.currentUser = userData
                                Toast.makeText(context, "Welcome, ${userData.username}!", Toast.LENGTH_SHORT).show()
                                onLoginClick()
                            } catch (e2: Exception) {
                                Toast.makeText(context, "Manual mapping failed: ${e2.message}", Toast.LENGTH_LONG).show()
                                isLoading = false
                            }
                        }
                    }
                } else {
                    // User exists in Auth but not in Firestore - handle this edge case
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "User profile not found. Please complete your profile.", Toast.LENGTH_LONG).show()
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "Error fetching user data: ${e.message}", Toast.LENGTH_LONG).show()
                    isLoading = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Authentication Type Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    authType = "Phone"
                    otpSent = false
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (authType == "Phone")
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("Phone")
            }

            Button(
                onClick = {
                    authType = "Email"
                    otpSent = false
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (authType == "Email")
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("Email")
            }
        }

        // Show different fields based on auth type
        if (authType == "Phone") {
            // Phone number field with OTP button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        if (it.length <= 10 && it.all { char -> char.isDigit() }) phoneNumber = it
                    },
                    label = { Text("Phone Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    singleLine = true
                )

                Button(
                    onClick = {
                        if (auth == null) {
                            Toast.makeText(context, "Firebase not initialized. Please restart the app.", Toast.LENGTH_LONG).show()
                            return@Button
                        }
                        try {
                            isLoading = true
                            // Format phone number with country code (assuming India)
                            val formattedPhoneNumber = "+91$phoneNumber"

                            // Simple callback for phone verification
                            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                    // Sign in with the credential
                                    auth.signInWithCredential(credential)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                val userId = auth.currentUser?.uid
                                                if (userId != null) {
                                                    fetchUserData(userId)
                                                } else {
                                                    isLoading = false
                                                    Toast.makeText(context, "Failed to get user ID", Toast.LENGTH_SHORT).show()
                                                }
                                            } else {
                                                isLoading = false
                                                Toast.makeText(context, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                }

                                override fun onVerificationFailed(e: FirebaseException) {
                                    isLoading = false
                                    Toast.makeText(context, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
                                }

                                override fun onCodeSent(
                                    verId: String,
                                    token: PhoneAuthProvider.ForceResendingToken
                                ) {
                                    verificationId = verId
                                    otpSent = true
                                    isLoading = false
                                    Toast.makeText(context, "OTP sent to your phone", Toast.LENGTH_SHORT).show()
                                }
                            }

                            // Use try-catch for all Firebase operations
                            try {
                                val options = PhoneAuthOptions.newBuilder(auth)
                                    .setPhoneNumber(formattedPhoneNumber)
                                    .setTimeout(60L, TimeUnit.SECONDS)
                                    .setActivity(context as ComponentActivity)
                                    .setCallbacks(callbacks)
                                    .build()
                                PhoneAuthProvider.verifyPhoneNumber(options)
                            } catch (e: Exception) {
                                isLoading = false
                                Toast.makeText(context, "Error sending OTP: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            isLoading = false
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    },
                    enabled = phoneNumber.length == 10 && !isLoading,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    if (isLoading && !otpSent) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Get OTP")
                    }
                }
            }

            // OTP Field (visible only after "Get OTP" is clicked)
            if (otpSent) {
                Text(
                    text = "Enter OTP sent to your phone",
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )

                OutlinedTextField(
                    value = otp,
                    onValueChange = {
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            otp = it
                        }
                    },
                    label = { Text("Enter 6-digit OTP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp
                    )
                )
            }
        } else {
            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(
                            text = if (passwordVisible) "👁" else "👁‍🗨",
                            fontSize = 18.sp
                        )
                    }
                }
            )
        }

        // Login button
        Button(
            onClick = {
                if (auth == null || db == null) {
                    Toast.makeText(context, "Firebase not initialized. Please restart the app.", Toast.LENGTH_LONG).show()
                    return@Button
                }

                isLoading = true

                if (authType == "Phone" && otpSent) {
                    // Verify OTP and sign in
                    try {
                        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
                        auth.signInWithCredential(credential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val userId = auth.currentUser?.uid
                                    if (userId != null) {
                                        fetchUserData(userId)
                                    } else {
                                        isLoading = false
                                        Toast.makeText(context, "Failed to get user ID", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    isLoading = false
                                    Toast.makeText(context, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    } catch (e: Exception) {
                        isLoading = false
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } else if (authType == "Email") {
                    // Email and password sign in
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val userId = auth.currentUser?.uid
                                    if (userId != null) {
                                        fetchUserData(userId)
                                    } else {
                                        isLoading = false
                                        Toast.makeText(context, "Failed to get user ID", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    isLoading = false
                                    Toast.makeText(context, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    } else {
                        isLoading = false
                        Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isLoading = false
                    Toast.makeText(context, "Please complete authentication first", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = !isLoading && ((authType == "Phone" && otpSent && otp.length == 6) ||
                    (authType == "Email" && email.isNotEmpty() && password.isNotEmpty())),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(bottom = 8.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { /* Handle forgot password */ }) {
            Text("Forgot Password?")
        }

        // Sign Up button with navigation
        TextButton(onClick = onSignUpClick) {
            Text("Don't have an account? Sign Up")
        }
    }
}

// You also need to update the SignUpPage to save user data to Firestore
// Here's a function that should be added to the SignUpPage.kt file:

fun saveUserDataToFirestore(userData: UserData) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    val userId = auth.currentUser?.uid
    if (userId == null) {
        // Handle error - user not authenticated
        return
    }

    // Add user ID to the data
    val userDataWithId = userData.copy(uid = userId)

    // Save to Firestore
    db.collection("users").document(userId)
        .set(userDataWithId)
        .addOnSuccessListener {
            // Data saved successfully
            UserSession.currentUser = userDataWithId
        }
        .addOnFailureListener { e ->
            // Handle failure
        }
}