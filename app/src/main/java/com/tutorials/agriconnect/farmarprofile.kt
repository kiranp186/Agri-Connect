package com.tutorials.agriconnect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

data class FarmerData(
    val username: String = "",
    val userType: String = "",
    val email: String = "",
    val address: String = "",
    val district: String = "",
    val state: String = "",
    val taluk: String = "",
    val phoneNumber: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerProfileScreen(
    navController: NavController = rememberNavController(),
    userId: String? = FirebaseAuth.getInstance().currentUser?.uid // Default user ID from screenshot
) {
    val primaryColor = Color(0xFF4CAF50)
    val secondaryColor = Color(0xFFE8F5E9)
    val scrollState = rememberScrollState()

    val coroutineScope = rememberCoroutineScope()
    var farmerData by remember { mutableStateOf<FarmerData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch data from Firestore
    LaunchedEffect(userId) {
        coroutineScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                val docRef = userId?.let { db.collection("users").document(it) }
                val document = docRef?.get()?.await()

                if (document != null && document.exists()) {
                    farmerData = FarmerData(
                        username = document.getString("username") ?: "",
                        userType = document.getString("userType") ?: "",
                        email = document.getString("email") ?: "",
                        address = document.getString("address") ?: "",
                        district = document.getString("district") ?: "",
                        state = document.getString("state") ?: "",
                        taluk = document.getString("taluk") ?: "",
                        phoneNumber = document.getString("phoneNumber") ?: ""
                    )
                } else {
                    errorMessage = "User data not found"
                }
                isLoading = false
            } catch (e: Exception) {
                errorMessage = "Error loading data: ${e.message}"
                isLoading = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Farmer Profile",
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Handle edit profile */ }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = primaryColor
                    )
                )
            }
        ) { paddingValues ->
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = primaryColor)
                    }
                }

                errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.Clear,
                                contentDescription = "Error",
                                tint = Color.Red,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = errorMessage ?: "Unknown error",
                                color = Color.Red
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    errorMessage = null
                                    isLoading = true
                                    // Re-trigger the LaunchedEffect
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryColor
                                )
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }

                farmerData != null -> {
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(bottom = 80.dp) // Space for bottom navigation
                    ) {
                        // Header with user info
                        ProfileHeader(
                            username = farmerData?.username ?: "",
                            userType = farmerData?.userType ?: "",
                            email = farmerData?.email ?: "",
                            primaryColor = primaryColor
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Profile details card - using GridInfoSection
                        GridInfoSection(
                            title = "Personal Information",
                            icon = Icons.Outlined.Person,
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor,
                            infoItems = listOf(
                                Pair("District", farmerData?.district ?: ""),
                                Pair("State", farmerData?.state ?: ""),
                                Pair("Taluk", farmerData?.taluk ?: ""),
                                Pair("Address", farmerData?.address ?: ""),
                                if (!farmerData?.phoneNumber.isNullOrBlank()) {
                                    Pair("Phone", farmerData?.phoneNumber ?: "")
                                } else null
                            ).filterNotNull()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Land Details - using standard section
                        ProfileSection(
                            title = "Land Details",
                            icon = Icons.Outlined.Place,
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor
                        ) {
                            Text(
                                text = "No land details available",
                                color = Color.Gray,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Bottom Navigation - Using a unique name to avoid conflicts
        FarmerProfileBottomBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(1f),
            navController = navController
        )
    }
}

@Composable
fun ProfileHeader(
    username: String,
    userType: String,
    email: String,
    primaryColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(primaryColor.copy(alpha = 0.1f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(primaryColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Profile",
                modifier = Modifier.size(60.dp),
                tint = primaryColor
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = username,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = userType,
            fontSize = 16.sp,
            color = primaryColor,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Email,
                contentDescription = "Email",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = email,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        // User ID
        Spacer(modifier = Modifier.height(8.dp))


    }
}

// Grid layout approach to ensure separation
@Composable
fun GridInfoSection(
    title: String,
    icon: ImageVector,
    primaryColor: Color,
    secondaryColor: Color,
    infoItems: List<Pair<String, String>>
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Section header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(secondaryColor)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = primaryColor
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }

            // Section content - Grid layout to ensure proper spacing
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                infoItems.forEach { (label, value) ->
                    // Each item gets its own card for clear separation
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shape = RoundedCornerShape(4.dp),
                        shadowElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            // Label
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                color = primaryColor,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Value
                            Text(
                                text = value,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileSection(
    title: String,
    icon: ImageVector,
    primaryColor: Color,
    secondaryColor: Color,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Section header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(secondaryColor)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = primaryColor
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }

            // Section content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                content()
            }
        }
    }
}

// Renamed to avoid conflicts with other files
@Composable
fun FarmerProfileBottomBar(modifier: Modifier = Modifier, navController: NavController) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProfileBottomNavItem(
                icon = Icons.Outlined.Home,
                label = "Home",
                selected = false,
                onClick = {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") {
                            inclusive = true
                        }
                    }
                }
            )

            ProfileBottomNavItem(
                icon = Icons.Outlined.Category,
                label = "Categories",
                selected = false,
                onClick = {
                    navController.navigate("categories")
                }
            )

            ProfileBottomNavItem(
                icon = Icons.Outlined.ShoppingCart,
                label = "My Bookings",
                selected = false,
                onClick = {
                    navController.navigate("my_bookings")
                }
            )

            ProfileBottomNavItem(
                icon = Icons.Outlined.AccountCircle,
                label = "Profile",
                selected = true,
                onClick = {
                    navController.navigate("account")
                }
            )
        }
    }
}

// Renamed to avoid conflicts with other files
@Composable
fun ProfileBottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val primaryColor = Color(0xFF4CAF50)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) primaryColor else Color.Gray,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = label,
            fontSize = 12.sp,
            color = if (selected) primaryColor else Color.Gray
        )
    }
}