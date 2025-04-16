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
fun OwnerAppScreen(navController: NavController = rememberNavController()) {
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

                // My Fields Section
                MyEquipmentSection()

                Spacer(modifier = Modifier.weight(1f))
            }

//            // Task Bar (always visible)
//            TaskBar(
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .zIndex(10f),
//                navController = navController
//            )
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

/**
 * A data class representing a featured item with a title and image resource
 */
private data class FeaturedContent(
    val title: String,
    val imageResId: Int
)


@Composable
private fun MyEquipmentSection() {

}

/**
 * A data class representing a commodity with a name and image resource
 */


@Preview(showBackground = true)
@Composable
fun OwnerAppScreen() {
    OwnerAppScreen()
}