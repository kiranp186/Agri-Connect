package com.tutorials.agriconnect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@Composable
fun MyBookings() {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Fixed header at the top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .shadow(8.dp)
                .background(Color.White)
                .padding(horizontal = 8.dp)
                .zIndex(10f),  // Ensure it stays on top
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = Color.Gray
                )
            }

            Text(
                text = "AgriConnect",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
        }

        // Scrollable content with padding to account for the fixed header
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 72.dp)  // Add padding equal to header height
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .verticalScroll(scrollState)
                    .padding(bottom = 72.dp)  // Add padding for task bar at bottom
                    .padding(horizontal = 16.dp)
            ) {
                // CURRENT ORDERS SECTION
                Text(
                    text = "Current Orders",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF4A6118)
                )

                // Current orders list
                repeat(3) {
                    CurrentOrderItem(
                        equipmentName = "Tractor ${it + 1}",
                        ownerName = "Owner Name ${it + 1}",
                        dateBooked = "March ${it + 5}, 2025"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Divider between sections
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    thickness = 1.dp,
                    color = Color.LightGray
                )

                // PAST ORDERS SECTION
                Text(
                    text = "Past Orders",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF4A6118)
                )

                // Past orders list
                PastOrderItem(
                    equipmentName = "Harvester 1",
                    ownerName = "John Smith",
                    dateBooked = "February 20, 2025",
                    isCompleted = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                PastOrderItem(
                    equipmentName = "Plough 2",
                    ownerName = "Jane Doe",
                    dateBooked = "February 15, 2025",
                    isCompleted = false
                )

                Spacer(modifier = Modifier.height(12.dp))

                PastOrderItem(
                    equipmentName = "Seeder 3",
                    ownerName = "David Johnson",
                    dateBooked = "February 10, 2025",
                    isCompleted = true
                )

                // Extra space at the bottom for better UX
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Task Bar (always visible)
            TaskBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(10f)
            )
        }
    }
}

@Composable
fun CurrentOrderItem(
    equipmentName: String,
    ownerName: String,
    dateBooked: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "IMG",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            // Equipment details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = equipmentName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A6118)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = ownerName,
                    fontSize = 16.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Date Booked: $dateBooked",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Contact section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(60.dp)
            ) {
                Text(
                    text = "Contact",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Message icon
                Icon(
                    imageVector = Icons.Default.MailOutline,
                    contentDescription = "Message",
                    tint = Color(0xFF4A6118),
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Phone icon
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call",
                    tint = Color(0xFF4A6118),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun PastOrderItem(
    equipmentName: String,
    ownerName: String,
    dateBooked: String,
    isCompleted: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "IMG",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            // Equipment details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = equipmentName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A6118)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = ownerName,
                    fontSize = 16.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Date Booked: $dateBooked",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Status section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(80.dp)
            ) {
                if (isCompleted) {
                    Text(
                        text = "Order Completed",
                        fontSize = 12.sp,
                        color = Color(0xFF4CAF50),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Green tick icon
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Order Cancelled",
                        fontSize = 12.sp,
                        color = Color(0xFFF44336),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Red cross icon
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Cancelled",
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}