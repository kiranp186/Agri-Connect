package com.tutorials.agriconnect

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * A complete Farmers App screen component with added commodity images
 * Updated to support navigation
 */
@Composable
fun OwnerAppScreen(navController: NavController = rememberNavController(),
                   showEquipmentAddedMessage: Boolean = false
)
{
    val scrollState = rememberScrollState()
    var isOwnerSidebarVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(showEquipmentAddedMessage) {
        if (showEquipmentAddedMessage) {
            Toast.makeText(context, "Equipment added successfully!", Toast.LENGTH_LONG).show()
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        // Main content with scroll
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF6B8E23)) // Olive green background
                    .verticalScroll(scrollState)
                    .padding(bottom = 72.dp) // Add padding for task bar at bottom
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Menu button for OwnerSidebar
                    IconButton(
                        onClick = { isOwnerSidebarVisible = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x33FFFFFF))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Menu",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "Hello, ",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .size(150.dp, 24.dp)
                                .background(Color(0xFF6B8E23), shape = RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Row {
                                Text(
                                    "Location",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                IconButton(onClick = { /*TODO*/ }) {
                                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Color.White)
                                }
                            }
                        }
                    }
                }

                // Search Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x99FFFFFF))
                        .padding(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Replace icon with a box
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    Color.Gray.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Search here...",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    Color.Gray.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        )
                    }
                }

                // NEW SECTION: Added New Scrollable Section
                NewScrollableSection()

                // My Equipments Section (Added as requested)
                MyEquipmentsSection(navController)

                Spacer(modifier = Modifier.weight(1f))
            }
        }

        // OwnerSidebar overlay (animated)
        OwnerSidebarOverlay(
            isVisible = isOwnerSidebarVisible,
            onDismiss = { isOwnerSidebarVisible = false },
            navController = navController
        )
    }
}

@Composable
private fun NewScrollableSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Featured Content",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Define items for the new section with titles and image resources
        val FeaturedContents = listOf(
            FeaturedContent("Sowing Machine", R.drawable.sowingmachine),
            FeaturedContent("Market Prices", R.drawable.preparation),
            FeaturedContent("Farming Tips", R.drawable.tract3),
            FeaturedContent("Equipment Rental", R.drawable.sowing1),
            FeaturedContent("Community News", R.drawable.harvester1),
            FeaturedContent("Seasonal Crops", R.drawable.special2)
        )

        // Create an infinite list by repeating the original list
        val infiniteList = remember {
            generateSequence { FeaturedContents }.flatten().take(1000).toList()
        }

        // Auto-scrolling implementation
        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        // Auto-scroll timer effect
        LaunchedEffect(Unit) {
            var currentIndex = 0
            while (isActive) {
                delay(3000) // 3 seconds delay between scrolls
                currentIndex = (currentIndex + 1) % infiniteList.size
                // Smooth scroll to the next item
                listState.animateScrollToItem(
                    index = currentIndex,
                    scrollOffset = 0
                )
            }
        }

        // Add manual scrolling pause/resume
        var isAutoScrollPaused by remember { mutableStateOf(false) }

        // The LazyRow with controlled state
        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                // Pause auto-scrolling when user is interacting
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { isAutoScrollPaused = true },
                        onDragEnd = { isAutoScrollPaused = false },
                        onDragCancel = { isAutoScrollPaused = false },
                        onDrag = { _, _ -> }
                    )
                },
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(infiniteList.size) { index ->
                val item = infiniteList[index]
                FeaturedBox(item)
            }
        }
    }
}

@Composable
private fun FeaturedBox(item: FeaturedContent) {
    Box(
        modifier = Modifier
            .width(320.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .shadow(4.dp)
    ) {
        // Display the actual image with content scale
        Image(
            painter = painterResource(id = item.imageResId),
            contentDescription = item.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Semi-transparent overlay at the bottom for text
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(8.dp)
        ) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

// Data class for equipment items
data class EquipmentItem(
    val id: String,
    val name: String,
    val imageResId: Int,
    val pricePerDay: Double,
    val status: String = "Available" // Default status
)

@Composable
private fun MyEquipmentsSection(navController: NavController) {
    // Sample data for equipment items
    val equipmentItems = remember {
        listOf(
            EquipmentItem("1", "Tractor - John Deere", R.drawable.tract3, 5000.0),
            EquipmentItem("2", "Harvester", R.drawable.harvester1, 7500.0),
            EquipmentItem("3", "Sowing Machine", R.drawable.sowingmachine, 2500.0),
            EquipmentItem("4", "Sprayer", R.drawable.special2, 1500.0)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Title and Add Button row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Section title
            Text(
                text = "My Equipments",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Add Equipment Button
            Button(
                onClick = { navController.navigate("add_equipment") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A6118)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Equipment",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Equipment", color = Color.White)
            }
        }

        // Equipment Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        ) {
            items(equipmentItems) { equipment ->
                EquipmentCard(equipment) {
                    // Navigate to equipment detail screen
                    navController.navigate("equipment_detail/${equipment.id}")
                }
            }
        }
    }
}

@Composable
private fun EquipmentCard(equipment: EquipmentItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Equipment Image (60% of card height)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                Image(
                    painter = painterResource(id = equipment.imageResId),
                    contentDescription = equipment.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Status chip
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    color = if (equipment.status == "Available") Color(0xFF4CAF50) else Color(0xFFFF9800),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = equipment.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }

            // Equipment Details (40% of card height)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = equipment.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "₹${equipment.pricePerDay}/day",
                    color = Color(0xFF4A6118),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/**
 * A data class representing a featured item with a title and image resource
 */
private data class FeaturedContent(
    val title: String,
    val imageResId: Int
)

@Composable
fun OwnerSidebarOverlay(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    navController: NavController
) {
    // Track if the language dropdown is expanded
    var isLanguageDropdownExpanded by remember { mutableStateOf(false) }

    // Track the selected language
    var selectedLanguage by remember { mutableStateOf("English") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f)
    ) {
        // Semi-transparent background when OwnerSidebar is visible
        if (isVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { onDismiss() }
            )
        }

        // Animated OwnerSidebar
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(300)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(300)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(250.dp)
                    .background(Color(0xFF4A6118))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "AgriConnect",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )

                    // OwnerSidebar menu items
                    OwnerSidebarMenuItem(
                        title = "My Account",
                        onClick = {
                            onDismiss()
                            navController.navigate("account")
                        }
                    ) {
                        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, tint = Color.White)
                    }

                    // Language menu item with dropdown
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .clickable { isLanguageDropdownExpanded = true },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = "Languages",
                                tint = Color.White
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = "Languages ($selectedLanguage)",
                                fontSize = 16.sp,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.weight(1f))
                        }

                        // Language dropdown menu
                        DropdownMenu(
                            expanded = isLanguageDropdownExpanded,
                            onDismissRequest = { isLanguageDropdownExpanded = false },
                            modifier = Modifier
                                .background(Color(0xFF3A4F11))
                                .width(200.dp)
                        ) {
                            // English option
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "English",
                                        color = if (selectedLanguage == "English")
                                            Color.White else Color.White.copy(alpha = 0.7f),
                                        fontWeight = if (selectedLanguage == "English")
                                            FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    selectedLanguage = "English"
                                    isLanguageDropdownExpanded = false
                                }
                            )

                            // Kannada option
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Kannada",
                                        color = if (selectedLanguage == "Kannada")
                                            Color.White else Color.White.copy(alpha = 0.7f),
                                        fontWeight = if (selectedLanguage == "Kannada")
                                            FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    selectedLanguage = "Kannada"
                                    isLanguageDropdownExpanded = false
                                }
                            )
                        }
                    }

                    OwnerSidebarMenuItem(
                        title = "Wish List",
                        onClick = { /* Handle navigation */ }
                    ) {
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = Color.White)
                    }

                    OwnerSidebarMenuItem(
                        title = "Bookings",
                        onClick = {
                            onDismiss()
                            navController.navigate("")
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                    }

                    OwnerSidebarMenuItem(
                        title = "Blogs",
                        onClick = { /* Handle navigation */ }
                    ) {
                        Icon(imageVector = Icons.Default.MailOutline, contentDescription = null, tint = Color.White)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Logout at the bottom
                    OwnerSidebarMenuItem(
                        title = "Logout",
                        onClick = {
                            onDismiss()
                            // Navigate back to login
                            navController.navigate("get_started") {
                                popUpTo("dashboard") { inclusive = true }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = null,
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OwnerSidebarMenuItem(
    title: String,
    onClick: () -> Unit = {},
    icon: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
        )
    }
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon content
        icon()

        Spacer(modifier = Modifier.width(16.dp))

        // Menu item text
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color.White
        )
    }
}

@Composable
fun Ownerdashboard(navController: NavController) {
    // Get navigation parameters
    val equipmentAdded = navController.currentBackStackEntry
        ?.arguments?.getString("equipment_added").toBoolean()

    OwnerAppScreen(navController, showEquipmentAddedMessage = equipmentAdded)
}

@Preview(showBackground = true)
@Composable
fun OwnerAppScreenPreview() {
    OwnerAppScreen()
}