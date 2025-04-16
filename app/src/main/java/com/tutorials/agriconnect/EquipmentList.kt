package com.tutorials.agriconnect



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.foundation.clickable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.tutorials.agriconnect.ui.theme.AgriconnectTheme

@Composable
fun equipmentlist(navController: NavController=rememberNavController()) {
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


                // Current orders list

                    currentorderitem(
                        equipmentName = " John Deer Tractor",
                        ownerName = "Shankre Gowda",
                        dateBooked = "Goruru,Hassan,Hassan",
                        mrp = "₹6000",
                        navController = navController
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                currentorderitem(
                    equipmentName = "Mahindra Tractor",
                    ownerName = "Mani",
                    dateBooked = "Kundoor,Aluru,Hassan",
                    mrp = "₹4800"
                )
                Spacer(modifier = Modifier.height(12.dp))

                currentorderitem(
                    equipmentName = "Sonalika Tractor",
                    ownerName = "Mohan",
                    dateBooked = "S.Belagola,C.R Patna,Hassan",
                    mrp = "₹5000"
                )
                Spacer(modifier = Modifier.height(12.dp))


                // Divider between sections


                // PAST ORDERS SECTION


                // Past orders list
                pastorderitem(
                    equipmentName = "Mahindra Tractor",
                    ownerName = "John ",
                    dateBooked = "Dudda,Hassan",
                    isCompleted = true,
                    MRP= "₹4500"
                )

                Spacer(modifier = Modifier.height(12.dp))

                pastorderitem(
                    equipmentName = "Sonalika Tractors",
                    ownerName = "Ramanna",
                    dateBooked = "Adaguru,Hassan,Hassan",
                    isCompleted = false,
                    MRP = "₹5200"
                )

                Spacer(modifier = Modifier.height(12.dp))

                pastorderitem(
                    equipmentName = "Mahindra Tractor",
                    ownerName = "Swami",
                    dateBooked = "Aduvalli,Beluru,Hassan",
                    isCompleted = true,
                    MRP = "₹5000"
                )

                // Extra space at the bottom for better UX
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Task Bar (always visible)
//            BottomNavigationBar(
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .zIndex(10f),
//                navController = navController
//            )
        }
    }
}

@Composable
//private fun BottomNavigationBar(modifier: Modifier = Modifier,navController: NavController) {
//    Surface(
//        color = Color.White,
//        shadowElevation = 8.dp,
//        modifier = modifier
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 8.dp),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            BottomNavitem(
//                icon = Icons.Outlined.Home,
//                label = "Home",
//                selected = false,
//                onClick = {
//                    navController.navigate("dashboard"){
//                        popUpTo("dashboard") {inclusive = true}
//                    }
//                }
//            )
//            BottomNavitem(
//                icon = Icons.Default.List,
//                label = "Catagories",
//                selected = false,
//                onClick = {
//                    navController.navigate("categories")
//                }
//            )
//            BottomNavitem(
//                icon = Icons.Outlined.ShoppingCart,
//                label = "My Bookings",
//                selected = true,
//                onClick = {
//
//                }
//            )
//            BottomNavitem(
//                icon = Icons.Outlined.AccountCircle,
//                label = "My Account",
//                selected = false,
//                onClick = {
//                    navController.navigate("account")
//                }
//            )
//        }
//    }
//}

//@Composable
//fun BottomNavitem(
//    icon: ImageVector,
//    label: String,
//    selected: Boolean = false,
//    onClick: ()-> Unit
//) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier
//            .clickable (onClick = onClick)
//            .padding(8.dp)
//    ) {
//        Icon(
//            imageVector = icon,
//            contentDescription = label,
//            tint = if (selected) Color(0xFF4CAF50) else Color.Gray,
//            modifier = Modifier.size(24.dp)
//        )
//        Text(
//            text = label,
//            fontSize = 12.sp,
//            color = if (selected) Color(0xFF4CAF50) else Color.Gray
//        )
//    }
//}


fun currentorderitem(
    equipmentName: String,
    ownerName: String,
    dateBooked: String,
    mrp: String,
    navController: NavController? = null

) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable {
                // Navigate only if it's the John Deer Tractor
                if (equipmentName.contains("John Deer Tractor", ignoreCase = true)) {
                    navController?.navigate("equipment_detail/1")
                }
            },
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
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A6118)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = ownerName,
                    fontSize = 17.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$dateBooked",
                    fontSize = 15.sp,
                    color = Color.Gray
                )


                Text(
                    text = "$mrp",
                    fontSize = 20.sp,
                    color = Color(0xFF4A6118)
                )
            }

            // Contact section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(60.dp)
            ) {


                Spacer(modifier = Modifier.height(8.dp))

                // Message icon


                Spacer(modifier = Modifier.height(8.dp))

                // Phone icon
            }
        }
    }
}

@Composable
fun pastorderitem(
    equipmentName: String,
    ownerName: String,
    dateBooked: String,
    isCompleted: Boolean,
    MRP:String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
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
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A6118)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = ownerName,
                    fontSize = 17.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$dateBooked",
                    fontSize = 15.sp,
                    color = Color.Gray
                )
                Text(
                    text = "$MRP",
                    fontSize = 20.sp,
                    color = Color.Gray
                )
            }

            // Status section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(80.dp)
            ) {
                if (isCompleted) {


                    Spacer(modifier = Modifier.height(8.dp))

                    // Green tick icon

                } else {


                    Spacer(modifier = Modifier.height(8.dp))

                    // Red cross icon

                }
            }
        }
    }




}

@Preview(showBackground = true)
@Composable
fun list() {
    equipmentlist()
}