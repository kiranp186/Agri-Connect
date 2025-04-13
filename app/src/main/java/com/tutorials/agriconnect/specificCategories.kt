package com.tutorials.agriconnect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException

// Data model for equipment
@Serializable
data class Equipment(
    val equipment_name: String,
    val crop: List<String>,
    val category: String,
    val image: String,
    val details: String
)

// Main function to display specific category equipment
@Composable
fun SpecificCategoryScreen(
    category: String,
    onBackClick: () -> Unit
) {
    var equipmentList by remember { mutableStateOf<List<Equipment>>(emptyList()) }
    val context = LocalContext.current
    val TAG = "SpecificCategoryScreen"

    // In the SpecificCategoryScreen function, add more detailed logging:
    LaunchedEffect(category) {
        try {
            Log.d(TAG, "Loading equipment data for category: '$category'")
            val allEquipment = loadEquipmentData(context)

            if (allEquipment.isNotEmpty()) {
                Log.d(TAG, "Successfully loaded ${allEquipment.size} equipment items")

                // Print each equipment item and its category for debugging
                allEquipment.forEach { equipment ->
                    Log.d(TAG, "Equipment: ${equipment.equipment_name}, Category: '${equipment.category}'")
                }

                // Check for exact string match issues
                val exactMatch = allEquipment.filter { it.category == category }
                Log.d(TAG, "Items with exact category match: ${exactMatch.size}")

                // Check with trimmed strings
                val trimmedMatch = allEquipment.filter { it.category.trim() == category.trim() }
                Log.d(TAG, "Items with trimmed category match: ${trimmedMatch.size}")

                // Use your existing case-insensitive filter
                equipmentList = allEquipment.filter { equipment ->
                    val matches = equipment.category.equals(category, ignoreCase = true)
                    if (matches) {
                        Log.d(TAG, "Found matching equipment: ${equipment.equipment_name}")
                    }
                    matches
                }

                Log.d(TAG, "Final filtered count: ${equipmentList.size}")
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
                text = category,
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
                        text = "No equipment found for this category",
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(equipmentList) { equipment ->
                        EquipmentItem(equipment = equipment)
                    }
                }
            }
        }
        // Task Bar (always visible)
//        TaskBar(
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .zIndex(10f)
//        )
  }
}

@Composable
fun EquipmentItem(equipment: Equipment) {
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
                Text(
                    text = equipment.equipment_name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )

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

// Improved JSON loading function
// Add this debugging code to your loadEquipmentData function
fun loadEquipmentData(context: Context): List<Equipment> {
    val TAG = "loadEquipmentData"
    try {
        // Check if the file exists in assets
        val assetsList = context.assets.list("")
        Log.d(TAG, "Assets in root: ${assetsList?.joinToString(", ")}")

        val jsonString = context.assets.open("equipment_data.json").bufferedReader().use { it.readText() }
        Log.d(TAG, "JSON content (first 100 chars): ${jsonString.take(100)}...")

        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        val equipmentList = json.decodeFromString<List<Equipment>>(jsonString)

        // Debug the categories from the loaded data
        val categories = equipmentList.map { it.category }.distinct()
        Log.d(TAG, "Available categories: ${categories.joinToString(", ")}")

        // Debug specifically for Sowing & Planting Equipment
        val sowingEquipment = equipmentList.filter {
            it.category.equals("Sowing & Planting Equipment", ignoreCase = true)
        }
        Log.d(TAG, "Found ${sowingEquipment.size} sowing equipment items")

        return equipmentList
    } catch (e: IOException) {
        Log.e(TAG, "IO Exception reading JSON: ${e.message}")
        e.printStackTrace()
        return emptyList()
    } catch (e: Exception) {
        Log.e(TAG, "Exception parsing JSON: ${e.message}")
        e.printStackTrace()
        return emptyList()
    }
}

@Composable
fun getDrawableResourceId(imageName: String): Int {
    val context = LocalContext.current
    val TAG = "getDrawableResourceId"

    return try {
        if (imageName.isNotEmpty()) {
            // Try with exact name
            var resourceId = context.resources.getIdentifier(
                imageName, "drawable", context.packageName
            )

            if (resourceId == 0) {
                // Try lowercase version
                resourceId = context.resources.getIdentifier(
                    imageName.lowercase(), "drawable", context.packageName
                )
            }

            if (resourceId == 0) {
                // Try with common extensions
                val extensions = listOf("", ".webp", ".png", ".jpg", ".jpeg")
                for (ext in extensions) {
                    val nameWithoutExt = imageName.substringBeforeLast(".", imageName)
                    val resourceName = nameWithoutExt.lowercase()

                    resourceId = context.resources.getIdentifier(
                        resourceName, "drawable", context.packageName
                    )

                    if (resourceId != 0) {
                        Log.d(TAG, "Found resource with name '$resourceName'")
                        break
                    }
                }
            }

            // Return found resource ID or fallback
            if (resourceId != 0) {
                resourceId
            } else {
                Log.e(TAG, "Resource not found for '$imageName', using placeholder")
                R.drawable.placeholder_image
            }
        } else {
            R.drawable.placeholder_image
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error getting resource ID: ${e.message}")
        e.printStackTrace()
        R.drawable.placeholder_image
    }
}