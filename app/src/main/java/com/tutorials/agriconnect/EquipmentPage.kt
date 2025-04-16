package com.tutorials.agriconnect

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.tutorials.agriconnect.ui.theme.AgriconnectTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import kotlin.math.max
import kotlin.math.min

// Mock data for available dates
val availableDates = listOf(
    LocalDate.now().plusDays(1),
    LocalDate.now().plusDays(3),
    LocalDate.now().plusDays(5),
    LocalDate.now().plusDays(7),
    LocalDate.now().plusDays(10),
    LocalDate.now().plusDays(14)
)

// Mock data for available time slots
val availableTimeSlots = listOf(
    "09:00 AM - 11:00 AM",
    "01:00 PM - 03:00 PM",
    "04:00 PM - 06:00 PM"
)

// Sample equipment data - can be expanded later
val equipmentDetailsMap = mapOf(
    "1" to "John Deere 6135E-135 HP Tractor",
    "2" to "Mahindra 575 DI XP Plus Tractor",
    "3" to "New Holland 3600-2 Tractor",
    // Add more equipment as needed
)

@Composable
fun EquipmentDetailPage(
    equipmentId: String = "1",
    navController: NavController = rememberNavController(),
    onBackClick: () -> Unit = { navController.popBackStack() }
) {
    val scrollState = rememberScrollState()
    val lazyRowState = rememberLazyListState()
    val imageItems = (1..5).toList() // List of 5 images

    // Get equipment name based on ID with fallback
    val equipmentName = equipmentDetailsMap[equipmentId] ?: "John Deere 6135E-135 HP Tractor"

    // State for calendar dialog
    var showCalendarDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTimeSlot by remember { mutableStateOf<String?>(null) }

    // Track the current visible image index
    val firstVisibleItemIndex by remember { derivedStateOf { lazyRowState.firstVisibleItemIndex } }
    val currentImageIndex = firstVisibleItemIndex + 1

    // Calculate top bar opacity based on scroll position
    val topBarOpacity by remember {
        derivedStateOf {
            min(1f, max(0f, scrollState.value / 300f))
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 150.dp)  // Increased space for bottom button and nav bar
        ) {
            // Horizontal scrollable image carousel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(Color(0xFFF5F5F5))
            ) {
                // Scrollable image carousel
                LazyRow(
                    state = lazyRowState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(imageItems) { index ->
                        Box(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .fillParentMaxHeight()
                                .padding(horizontal = if (index > 1) 8.dp else 0.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Placeholder for equipment images
                            Image(
                                painterResource(id = R.drawable.tract3),
                                contentDescription = "Equipment Image",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                // Image counter indicator - now dynamic
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$currentImageIndex/${imageItems.size}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Back button for top part of the screen
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.7f))
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = Color.Gray
                    )
                }
            }

            // Equipment Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Title - Use the equipment name from the ID
                Text(
                    text = equipmentName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // Category
                Text(
                    text = "Tractors",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Ratings
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Row {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < 4) Color(0xFFFFB800) else Color(0xFFFFB800).copy(alpha = 0.5f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = "4.5",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    Text(
                        text = "(256)",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Price
                Text(
                    text = "₹ 5000",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.padding(top = 12.dp)
                )

                // Location (instead of quantity)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = "Location",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )

                        Text(
                            text = "Hassan, Karnataka",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                // Product Details
                Text(
                    text = "Product Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                )

                Text(
                    text = "The popular 75hp 5EN is back and better than ever. Whether spraying, trimming, mowing, fruit handling, or transport—the 5EN narrow frame glides easily through orchards and vineyards. Confidently tackle...",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )

                Text(
                    text = "Read more",
                    fontSize = 14.sp,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Reviews
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Review (50)",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "See all",
                        fontSize = 14.sp,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium
                    )
                }

                // Sample Review
                ReviewItem(
                    name = "Monur Rahman",
                    timePosted = "Today",
                    reviewText = "The owner was professional and prompt, delivering the tractor on time and in well-maintained condition. The booking process was smooth..."
                )

                // Review Images - Changed from Box to Image
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(2) {
                        Image(
                            painter = painterResource(id = R.drawable.review2),
                            contentDescription = "Review Image",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.review),
                        contentDescription = "Review Image",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "See more",
                    fontSize = 14.sp,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                )
            }
        }

        // Scroll-aware top bar
        if (topBarOpacity > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .shadow(8.dp)
                    .background(Color.White.copy(alpha = topBarOpacity))
                    .padding(horizontal = 8.dp)
                    .zIndex(10f),  // Ensure it stays on top
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
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
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(10f)
        ) {
            // Bottom Action Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Available Slot button (replaced Chat button)
                    Button(
                        onClick = { showCalendarDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF4CAF50)
                        ),
                        border = ButtonDefaults.outlinedButtonBorder,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Calendar",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = selectedDate?.let {
                                    it.format(DateTimeFormatter.ofPattern("dd MMM"))
                                } ?: "Available Slot",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Book Now button
                    Button(
                        onClick = {
                            // Navigate to payment screen
                            navController.navigate("payment_screen")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(2f)
                            .height(56.dp),
                        enabled = selectedDate != null && selectedTimeSlot != null
                    ) {
                        Text(
                            text = "Book Now",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Calendar Dialog
        if (showCalendarDialog) {
            Dialog(onDismissRequest = { showCalendarDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Select Date",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Calendar View
                        CalendarView(
                            availableDates = availableDates,
                            onDateSelected = { date ->
                                selectedDate = date
                                // Reset time slot when date changes
                                selectedTimeSlot = null
                            }
                        )

                        // Only show time slots if a date is selected
                        selectedDate?.let { date ->
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Available Time Slots",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                availableTimeSlots.forEach { timeSlot ->
                                    TimeSlotItem(
                                        timeSlot = timeSlot,
                                        isSelected = timeSlot == selectedTimeSlot,
                                        onSelected = {
                                            selectedTimeSlot = timeSlot
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showCalendarDialog = false }
                            ) {
                                Text("Cancel")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    // Close dialog only if a time slot is selected
                                    if (selectedTimeSlot != null) {
                                        showCalendarDialog = false
                                    }
                                },
                                enabled = selectedTimeSlot != null,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50)
                                )
                            ) {
                                Text("Confirm")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarView(
    availableDates: List<LocalDate>,
    onDateSelected: (LocalDate) -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Month navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    currentMonth = currentMonth.minusMonths(1)
                }
            ) {
                Text("<", fontWeight = FontWeight.Bold)
            }

            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                fontWeight = FontWeight.Medium
            )

            IconButton(
                onClick = {
                    currentMonth = currentMonth.plusMonths(1)
                }
            ) {
                Text(">", fontWeight = FontWeight.Bold)
            }
        }

        // Day of week headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (day in DayOfWeek.values()) {
                Text(
                    text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(1),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid
        val firstDayOfMonth = currentMonth.atDay(1)
        val firstCalendarDay = firstDayOfMonth.minusDays(firstDayOfMonth.dayOfWeek.value.toLong() % 7)
        val lastDayOfMonth = currentMonth.atEndOfMonth()

        Column(modifier = Modifier.fillMaxWidth()) {
            var currentDay = firstCalendarDay

            // Calendar weeks (max 6 weeks)
            repeat(6) { weekIndex ->
                if (currentDay.isAfter(lastDayOfMonth) && weekIndex > 0) {
                    // Skip rendering empty weeks after month end
                    return@repeat
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Days in a week
                    repeat(7) { _ ->
                        val day = currentDay
                        val isCurrentMonth = day.month == currentMonth.month
                        val isAvailable = availableDates.contains(day)
                        val isSelected = day == selectedDate

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSelected -> Color(0xFF4CAF50)
                                        isAvailable && isCurrentMonth -> Color(0xFFE8F5E9)
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable(
                                    enabled = isAvailable && isCurrentMonth,
                                    onClick = {
                                        selectedDate = day
                                        onDateSelected(day)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.dayOfMonth.toString(),
                                color = when {
                                    isSelected -> Color.White
                                    !isCurrentMonth -> Color.LightGray
                                    isAvailable -> Color(0xFF4CAF50)
                                    else -> Color.Gray
                                },
                                fontSize = 14.sp,
                                fontWeight = if (isAvailable || isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        currentDay = currentDay.plusDays(1)
                    }
                }
            }
        }

        // Calendar legend
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color(0xFF4CAF50), CircleShape)
            )

            Text(
                text = "Available",
                modifier = Modifier.padding(start = 4.dp, end = 16.dp),
                fontSize = 12.sp,
                color = Color(0xFF4CAF50)
            )

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color.LightGray, CircleShape)
            )

            Text(
                text = "Unavailable",
                modifier = Modifier.padding(start = 4.dp),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun TimeSlotItem(
    timeSlot: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE8F5E9) else Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) Color(0xFF4CAF50) else Color.LightGray
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = { onSelected() },
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFF4CAF50)
                )
            )

            Text(
                text = timeSlot,
                modifier = Modifier.padding(start = 8.dp),
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

@Composable
fun ReviewItem(
    name: String,
    timePosted: String,
    reviewText: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Profile picture
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDDDDDD)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(1),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = timePosted,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Text(
            text = reviewText,
            fontSize = 14.sp,
            color = Color.DarkGray,
            lineHeight = 20.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EquipmentDetailPreview() {
    AgriconnectTheme {
        EquipmentDetailPage()
    }
}