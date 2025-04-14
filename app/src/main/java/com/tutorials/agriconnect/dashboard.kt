package com.tutorials.agriconnect

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * A complete Farmers App screen component with added commodity images
 * Updated to support navigation
 */
@Composable
fun FarmersAppScreen(navController: NavController = rememberNavController()) {
    val scrollState = rememberScrollState()
    var isSidebarVisible by remember { mutableStateOf(false) }

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
                    // Menu button for sidebar
                    IconButton(
                        onClick = { isSidebarVisible = true },
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
                        text = "Hello, Farmers",
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
                        // Replace icon with a box
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

                // Commodity Scroll Component
                CommoditiesSection(navController)

                // My Fields Section
                MyFieldsSection()

                Spacer(modifier = Modifier.weight(1f))
            }

            // Task Bar (always visible)
            TaskBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(10f),
                navController = navController
            )
        }

        // Sidebar overlay (animated)
        SidebarOverlay(
            isVisible = isSidebarVisible,
            onDismiss = { isSidebarVisible = false },
            navController = navController
        )
    }
}

@Composable
fun SidebarOverlay(
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
        // Semi-transparent background when sidebar is visible
        if (isVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { onDismiss() }
            )
        }

        // Animated sidebar
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
                        text = "Farmers App",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )

                    // Sidebar menu items
                    SidebarMenuItem(
                        title = "My Account",
                        onClick = {
                            onDismiss()
                            // You can add navigation here if needed
                        }
                    ) {
                        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null)
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

                    SidebarMenuItem(
                        title = "Wish List",
                        onClick = { /* Handle navigation */ }
                    ) {
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = null)
                    }

                    SidebarMenuItem(
                        title = "My Bookings",
                        onClick = { /* Handle navigation */ }
                    ) {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
                    }

                    SidebarMenuItem(
                        title = "Blogs",
                        onClick = { /* Handle navigation */ }
                    ) {
                        Icon(imageVector = Icons.Default.MailOutline, contentDescription = null)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Logout at the bottom
                    SidebarMenuItem(
                        title = "Logout",
                        onClick = {
                            onDismiss()
                            // Navigate back to login
                            navController.navigate("login") {
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
fun SidebarMenuItem(
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
fun TaskBar(modifier: Modifier = Modifier, navController: NavController) {
    // Track the selected index
    var selectedIndex by remember { mutableStateOf(0) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .shadow(8.dp)
            .background(Color.White)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home
        TaskBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(28.dp),
                    tint = if (selectedIndex == 0) Color(0xFF4CAF50) else Color.Gray
                )
            },
            text = "Home",
            isSelected = selectedIndex == 0,
            onClick = {
                selectedIndex = 0
                // No navigation needed as we're already on the home screen
            }
        )

        // Categories
        TaskBarItem(
            icon = {
                // Custom icon from the provided PNG resource
                Image(
                    painter = painterResource(id = R.drawable.categories_icon),
                    contentDescription = "Categories",
                    modifier = Modifier.size(28.dp),
                    colorFilter = ColorFilter.tint(if (selectedIndex == 1) Color(0xFF4CAF50) else Color.Gray)
                )
            },
            text = "Categories",
            isSelected = selectedIndex == 1,
            onClick = {
                selectedIndex = 1
                navController.navigate("categories")            }
        )

        // My Bookings
        TaskBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "My Bookings",
                    modifier = Modifier.size(28.dp),
                    tint = if (selectedIndex == 2) Color(0xFF4CAF50) else Color.Gray
                )
            },
            text = "My Bookings",
            isSelected = selectedIndex == 2,
            onClick = {
                selectedIndex = 2
                navController.navigate("my_bookings")            }
        )

        // My Account
        TaskBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "My Account",
                    modifier = Modifier.size(28.dp),
                    tint = if (selectedIndex == 3) Color(0xFF4CAF50) else Color.Gray
                )
            },
            text = "My Account",
            isSelected = selectedIndex == 3,
            onClick = {
                selectedIndex = 3
                navController.navigate("account")            }
        )
    }
}

@Composable
fun TaskBarItem(
    icon: @Composable () -> Unit,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(2.dp)
            .clickable(onClick = onClick)
    ) {
        // Icon content
        icon()

        Spacer(modifier = Modifier.height(4.dp))

        // Task name
        Text(
            text = text,
            fontSize = 14.sp,
            color = if (isSelected) Color(0xFF4CAF50) else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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
        val featuredItems = listOf(
            FeaturedItem("Sowing Machine", R.drawable.sowingmachine),
            FeaturedItem("Market Prices", R.drawable.preparation),
            FeaturedItem("Farming Tips", R.drawable.tract3),
            FeaturedItem("Equipment Rental", R.drawable.sowing1),
            FeaturedItem("Community News", R.drawable.harvester1),
            FeaturedItem("Seasonal Crops", R.drawable.special2)
        )

        // Create an infinite list by repeating the original list
        val infiniteList = remember {
            generateSequence { featuredItems }.flatten().take(1000).toList()
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
private fun FeaturedBox(item: FeaturedItem) {
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

/**
 * A data class representing a featured item with a title and image resource
 */
private data class FeaturedItem(
    val title: String,
    val imageResId: Int
)

@Composable
private fun CommoditiesSection(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Commodities and Food",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Define commodity items for first row with image resources
        val commoditiesRow1 = listOf(
            Commodity("Rice", R.drawable.rice),
            Commodity("Corn", R.drawable.corn),
            Commodity("Ragi", R.drawable.ragi),
            Commodity("Wheat", R.drawable.wheat),
            Commodity("Potato", R.drawable.potato),
            Commodity("Vegetable", R.drawable.vegetables),
            Commodity("Soyabean", R.drawable.soyabean)
        )

        // Define commodity items for second row with image resources
        val commoditiesRow2 = listOf(
            Commodity("Ginger", R.drawable.ginger),
            Commodity("Areca nut", R.drawable.areca_nut),
            Commodity("Sugarcane", R.drawable.sugarcane),
            Commodity("Cotton", R.drawable.cotton),
            Commodity("Groundnut", R.drawable.groundnut),
            Commodity("ChickPea", R.drawable.chickpea),
            Commodity("Coconut", R.drawable.coconut),
            Commodity("Fruits", R.drawable.fruits)
        )

        // Create an infinite list by repeating the original list
        val infiniteList1 = remember {
            generateSequence { commoditiesRow1 }.flatten().take(1000).toList()
        }

        // First row of commodities
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(infiniteList1.size) { index ->
                val commodity = infiniteList1[index]
                CommodityBox(
                    commodity = commodity,
                    onClick = {
                        // Navigate to crop specific screen when commodity is clicked
                        navController.navigate("crop_specific")
                    }
                )
            }
        }

        // Add spacing between rows
        Spacer(modifier = Modifier.height(12.dp))

        // Create an infinite list by repeating the original list
        val infiniteList2 = remember {
            generateSequence { commoditiesRow2 }.flatten().take(1000).toList()
        }

        // Second row of commodities
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(infiniteList2.size) { index ->
                val commodity = infiniteList2[index]
                CommodityBox(
                    commodity = commodity,
                    onClick = {
                        // Navigate to crop specific screen when commodity is clicked
                        navController.navigate("crop_specific")
                    }
                )
            }
        }
    }
}

@Composable
private fun CommodityBox(commodity: Commodity, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(64.dp)
            .clickable(onClick = onClick)
    ) {
        // Image container with white background
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            // Load the actual image
            Image(
                painter = painterResource(id = commodity.imageResId),
                contentDescription = commodity.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = commodity.name,
            fontSize = 12.sp,
            color = Color.White
        )
    }
}

@Composable
private fun MyFieldsSection() {
    // Combined list of all crops from both commodity rows
    val allCrops = listOf(
        "Rice", "Corn", "Ragi", "Wheat", "Potato", "Vegetable", "Soyabean",
        "Ginger", "Areca nut", "Sugarcane", "Cotton", "Groundnut", "ChickPea", "Coconut", "Fruits"
    )

    // State for dropdown expanded status
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // State for selected crop - null means no selection yet
    var selectedCrop by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Title and dropdown row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Section title
            Text(
                text = "My Fields",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Crop Selection Dropdown
            Box {
                // Dropdown trigger button
                Button(
                    onClick = { isDropdownExpanded = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF4A6118)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = selectedCrop ?: "Select crop",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        // Dropdown icon
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "Select Crop",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Dropdown menu
                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false },
                    modifier = Modifier
                        .background(Color.White)
                        .width(180.dp)
                ) {
                    allCrops.forEach { crop ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = crop,
                                    fontSize = 14.sp,
                                    color = if (crop == selectedCrop) Color(0xFF4A6118) else Color.Black
                                )
                            },
                            onClick = {
                                selectedCrop = crop
                                isDropdownExpanded = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = Color.Black
                            )
                        )
                    }
                }
            }
        }

        // Fields display area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x22FFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            // Display based on whether a crop is selected
            if (selectedCrop != null) {
                // Show the selected crop info
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "selectedCrop",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White

                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Field information for $selectedCrop will be displayed here",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                // Show prompt to select a crop
                Text(
                    text = "Please select a crop to view field information",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

/**
 * A data class representing a commodity with a name and image resource
 */
private data class Commodity(
    val name: String,
    val imageResId: Int
)

@Preview(showBackground = true)
@Composable
fun FarmersAppScreenPreview() {
    FarmersAppScreen()
}