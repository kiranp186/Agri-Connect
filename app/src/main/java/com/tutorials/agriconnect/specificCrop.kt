package com.tutorials.agriconnect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.foundation.Image
import android.util.Log

@Composable
fun CropSpecificScreen(
    cropName: String,
    onBackClick: () -> Unit
) {
    var equipmentList by remember { mutableStateOf<List<Equipment>>(emptyList()) }
    val context = LocalContext.current
    val TAG = "CropSpecificScreen"

    LaunchedEffect(cropName) {
        try {
            Log.d(TAG, "Loading equipment data for crop: '$cropName'")
            val allEquipment = loadEquipmentData(context)

            if (allEquipment.isNotEmpty()) {
                Log.d(TAG, "Successfully loaded ${allEquipment.size} equipment items")

                // Filter equipment for the selected crop
                equipmentList = allEquipment.filter { equipment ->
                    val cropMatches = equipment.crop.any { crop ->
                        crop.equals(cropName, ignoreCase = true)
                    }
                    if (cropMatches) {
                        Log.d(TAG, "Found matching equipment for crop $cropName: ${equipment.equipment_name}")
                    }
                    cropMatches
                }

                Log.d(TAG, "Final filtered count for crop $cropName: ${equipmentList.size}")

                // Group equipment by category for better organization
                val categoryCounts = equipmentList.groupBy { it.category }
                    .mapValues { it.value.size }
                Log.d(TAG, "Equipment by category: $categoryCounts")

            } else {
                Log.e(TAG, "No equipment data loaded from JSON")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading equipment data: ${e.message}")
            e.printStackTrace()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .shadow(8.dp)
                .background(Color.White)
                .padding(horizontal = 16.dp)
                .zIndex(10f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = Color.Gray
                )
            }

            Text(
                text = "Equipment for $cropName",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Content area with proper padding for header
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 72.dp)
        ) {
            if (equipmentList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No equipment found for $cropName",
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                }
            } else {
                // Group equipment by category for better organization
                val groupedEquipment = equipmentList.groupBy { it.category }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    groupedEquipment.forEach { (category, equipment) ->
                        item {
                            Text(
                                text = category,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        items(equipment) { item ->
                            EquipmentItem(equipment = item)
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        // Task Bar (always visible)
        TaskBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(10f)
        )
    }
}

// Note: This component reuses the EquipmentItem component from the original code
// You can add any crop-specific customization to the EquipmentItem display if needed
// For example, you might want to show which other crops can use this equipment

@Composable
fun CropEquipmentItem(equipment: Equipment, cropName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 180.dp, max = 280.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Equipment Image
            val imageResourceId = getDrawableResourceId(equipment.image)
            Image(
                painter = painterResource(id = imageResourceId),
                contentDescription = equipment.equipment_name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
            )

            // Equipment name and details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f)
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = equipment.equipment_name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = equipment.category,
                        fontSize = 12.sp,
                        color = Color(0xFF666666),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = equipment.details,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}