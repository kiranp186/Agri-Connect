package com.tutorials.agriconnect

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerProfileScreen(navController: NavController = rememberNavController())
{
    val primaryColor = Color(0xFF009688)

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Profile") },
                    navigationIcon = {
                        IconButton(onClick = { /* Handle menu click */ }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Handle more options */ }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "More options")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = primaryColor,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(bottom = 72.dp), // Add padding for bottom navigation
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Farmer avatar
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Farmer Avatar",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    tint = primaryColor
                )

                // Farmer name
                Text(
                    text = "Name: RAJESH KUMAR",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // Farmer ID
                Text(
                    text = "Farmer ID: ML-F/C/2019/00025",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Profile sections
                ProfileSection(
                    title = "Personal Information",
                    expanded = false,
                    onToggle = { /* Handle toggle */ }
                )

                ProfileSection(
                    title = "Schemes Availed",
                    expanded = true,
                    onToggle = { /* Handle toggle */ }
                )

                // Schemes details
                SchemeItem(
                    title = "NREGA Job Card",
                    isActive = false
                )

                SchemeItem(
                    title = "PM Kisan Beneficiary",
                    isActive = true
                )

                ProfileSection(
                    title = "Bank Details",
                    expanded = false,
                    onToggle = { /* Handle toggle */ }
                )

                ProfileSection(
                    title = "Land Details",
                    expanded = false,
                    onToggle = { /* Handle toggle */ }
                )

                // Update profile button
                Button(
                    onClick = { /* Handle update profile */ },
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Update Profile")
                }
            }
        }

        // Add the bottom navigation bar here
        BottomNavigationBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(10f),
            navController = navController
        )
    }
}

@Composable
fun ProfileSection(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    val primaryColor = Color(0xFF009688)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Using icons that are definitely available
        Icon(
            imageVector = when (title) {
                "Personal Information" -> Icons.Outlined.Person
                "Schemes Availed" -> Icons.Outlined.List
                "Bank Details" -> Icons.Filled.Search // Changed from Outlined to Filled
                "Land Details" -> Icons.Outlined.Home
                else -> Icons.Outlined.Info
            },
            contentDescription = null,
            tint = primaryColor,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = primaryColor,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        )

        IconButton(onClick = onToggle) {
            Icon(
                imageVector = if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = primaryColor
            )
        }
    }

    Divider()
}

@Composable
fun SchemeItem(
    title: String,
    isActive: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        if (isActive) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = "Active",
                tint = Color.Green,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(modifier: Modifier = Modifier, navController: NavController) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            bottomnavitem(
                icon = Icons.Outlined.Home,
                label = "Home",
                selected = false,
                onClick = {
                    navController.navigate("dashboard"){
                        popUpTo("dashboard") {
                            inclusive = true
                        }
                    }
                }
            )
            bottomnavitem(
                icon = Icons.Default.List,
                label = "Catagories",
                selected = false,
                onClick = {
                    navController.navigate("categories")
                }
            )
            bottomnavitem(
                icon = Icons.Outlined.ShoppingCart,
                label = "My Bookings",
                selected = true,
                onClick = {
                    navController.navigate("my_bookings")
                }
            )
            bottomnavitem(
                icon = Icons.Outlined.AccountCircle,
                label = "My Account",
                selected = false,
                onClick = {
                    navController.navigate("account")
                }
            )
        }
    }
}

@Composable
fun bottomnavitem(
    icon: ImageVector,
    label: String,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) Color(0xFF4CAF50) else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (selected) Color(0xFF4CAF50) else Color.Gray
        )
    }
}

@Preview
@Composable
fun FarmerPreview(){
    FarmerProfileScreen()
}