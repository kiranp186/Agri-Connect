package com.tutorials.agriconnect

import coil.compose.rememberAsyncImagePainter

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.util.Calendar



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentOwnerDashboardScreen(navController: NavController = rememberNavController()) {
    val context = LocalContext.current

    var ownerName by remember { mutableStateOf("") }
    var equipmentName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("") }
    val typeOptions = listOf("Harvesting", "Post Harvesting", "Transport", "Special Crop Machine", "Transporters")
    var usagePurpose by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }

    val imageUri = remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri.value = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Adding new equipment and details ", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = ownerName,
            onValueChange = { ownerName = it },
            label = { Text("Owner Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = equipmentName,
            onValueChange = { equipmentName = it },
            label = { Text("Equipment Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // Equipment Type Dropdown
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedType,
                onValueChange = {},
                label = { Text("Equipment Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                readOnly = true,
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                typeOptions.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            selectedType = type
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = usagePurpose,
            onValueChange = { usagePurpose = it },
            label = { Text("Description about equipment") },
            modifier = Modifier.fillMaxWidth()
        )

        // Image Upload
        Text("Upload Equipment Image", fontWeight = FontWeight.Bold)
        Button(onClick = { launcher.launch("image/*") }) {
            Text("Choose Image")
        }

        imageUri.value?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 8.dp)
            )
        }

        OutlinedTextField(
            value = cost,
            onValueChange = { cost = it },
            label = { Text("Cost (per hour)") },
            modifier = Modifier.fillMaxWidth()
        )



        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Handle submit logic here
                Toast.makeText(context, "Submitted Successfully!", Toast.LENGTH_SHORT).show()
                navController.navigate("owner_dashboard?equipment_added=true") {
                    popUpTo("owner_dashboard") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit", fontWeight = FontWeight.Bold)
        }
    }
}
