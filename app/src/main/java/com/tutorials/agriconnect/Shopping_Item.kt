package com.tutorials.agriconnect

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tutorials.agriconnect.R

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors


class FarmTechHomeScreen {

    // Main composable function for the Farm Tech home screen
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FarmTechApp() {
        val lightGreen = Color(0xFFE8F5E9)

        Scaffold(
            topBar = { AppTopBar() },
            bottomBar = { BottomNavigationBar() },
            containerColor = Color(0xFF6A8E22)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                SearchBar()
                Spacer(modifier = Modifier.height(16.dp))

                CategorySection()
                Spacer(modifier = Modifier.height(24.dp))

                FeaturedProductsSection()
                Spacer(modifier = Modifier.height(24.dp))

                NewArrivalsSection()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AppTopBar() {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "AGRI",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                    Text(
                        text = " CONNECT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = Color(0xFF6A8E22)
            )
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun SearchBar() {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = { Text("Search here....") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            colors = outlinedTextFieldColors(
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray,
                containerColor = Color(0xFFF5F5F5)
            )
        )
    }

    @Composable
    private fun CategorySection() {
        Text(
            text = "Categories",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Categories row with horizontal scrolling
        val categories = listOf(
            CategoryData(R.drawable.landpreparation1, "Land Preparation Equipment", Color(0xFFE8F5E9)),
            CategoryData(R.drawable.sowing1, "Sowing & Planting Equipment", Color(0xFFE3F2FD)),
            CategoryData(R.drawable.drone1, "Fertilizer & Pesticide Equipment", Color(0xFFFFF8E1)),
            CategoryData(R.drawable.harvester1, "Harvesting Equipment", Color(0xFFE1F5FE)),
            CategoryData(R.drawable.postharvest, "Post-Harvest Equipment", Color(0xFFFFF3E0)),
            CategoryData(R.drawable.tractor, "Transport & Handling Equipment", Color(0xFFFFF3E0)),
            CategoryData(R.drawable.special, "Specialized Equipment", Color(0xFFFFF3E0))
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(categories) { category ->
                CategoryItem(
                    icon = category.icon,
                    title = category.title,
                    color = category.color
                )
            }
        }
    }

    data class CategoryData(val icon: Int, val title: String, val color: Color)

    @Composable
    private fun CategoryItem(icon: Int, title: String, color: Color) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(66.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    @Composable
    private fun FeaturedProductsSection() {
        SectionHeader(title = "Featured Products")

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProductCard(
                image = R.drawable.tractor,
                title = "John Deere 6155E-159 HP Tractor",
                price = "Rs8,500",
                rating = 4.8f,
                reviewCount = 180,
                modifier = Modifier.weight(1f)
            )

            ProductCard(
                image = R.drawable.harvester,
                title = "Yanmar Combine Harvester",
                price = "Rs10,000",
                rating = 4.7f,
                reviewCount = 157,
                modifier = Modifier.weight(1f)
            )
        }
    }

    @Composable
    private fun NewArrivalsSection() {
        SectionHeader(title = "New Arrivals")

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HorizontalProductCard(
                image = R.drawable.drone,
                title = "Agras T40 Agricultural Drone Sprayer",
                price = "Rs13,000"
            )

            HorizontalProductCard(
                image = R.drawable.transplanter,
                title = "2ZS-4D Self-Propelled Rice Transplanter",
                price = "Rs9,999"
            )
            HorizontalProductCard(
                image = R.drawable.special2,
                title = "John Deere's Autonomous Farming Tractor",
                price = "Rs11,999"
            )
        }
    }

    @Composable
    private fun SectionHeader(title: String) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            TextButton(onClick = { /* Handle see all click */ }) {
                Text(
                    text = "See all",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }

    @Composable
    private fun ProductCard(
        image: Int,
        title: String,
        price: String,
        rating: Float,
        reviewCount: Int,
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(230.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.7f))
                            .padding(4.dp),
                        tint = Color.Gray
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = price,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF4CAF50)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RatingBar(rating = rating)

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "($reviewCount)",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun HorizontalProductCard(
        image: Int,
        title: String,
        price: String
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row {
                Image(
                    painter = painterResource(id = image),
                    contentDescription = title,
                    modifier = Modifier
                        .width(100.dp)
                        .fillMaxHeight(),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = price,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF4CAF50)
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF5F5F5))
                            .padding(4.dp),
                        tint = Color.Gray
                    )
                }
            }
        }
    }

    @Composable
    private fun RatingBar(rating: Float) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..5) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (i <= rating) Color(0xFFFFC107) else Color.LightGray,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }

    @Composable
    private fun BottomNavigationBar() {
        Surface(
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BottomNavItem(
                    icon = Icons.Outlined.Home,
                    label = "Home",
                    selected = false
                )

                BottomNavItem(
                    icon = Icons.Default.List,
                    label = "Catagories",
                    selected = true
                )

                BottomNavItem(
                    icon = Icons.Outlined.ShoppingCart,
                    label = "My Bookings"
                )

                BottomNavItem(
                    icon = Icons.Outlined.AccountCircle,
                    label = "My Account"
                )
            }
        }
    }

    @Composable
    private fun BottomNavItem(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        label: String,
        selected: Boolean = false
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable { /* Handle navigation */ }
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
}

@Preview(showBackground = true)
@Composable
fun PreviewFarmTechApp() {
    FarmTechHomeScreen().FarmTechApp()
}
