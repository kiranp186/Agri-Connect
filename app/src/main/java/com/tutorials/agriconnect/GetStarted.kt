
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A composable that displays the Get Started screen of the AgriConnect app
 */
@Composable
fun GetStartedScreen(onGetStartedClick: () -> Unit = {}) {
    // Define colors
    val backgroundColor = Color(0xFFF5F2E9) // Light wheat color for background
    val primaryColor = Color(0xFFBFA265) // Gold color for accents
    val textColor = Color(0xFF5D4037) // Dark brown for text
    val gradientTop = Color(0xFFF8F4E3)
    val gradientBottom = Color(0xFFEAE0C8)

    // Use a box with gradient background instead of image
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(gradientTop, gradientBottom)
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo placeholder - a colored box
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .padding(top = 16.dp)
                    .background(
                        color = primaryColor,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                // Leaf icon represented as text
                Text(
                    text = "🌱",
                    fontSize = 24.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main headline text
            Text(
                text = "THE NEW ERA OF\nAGRICULTURE",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle text
            Text(
                text = "Sustainable farming solutions for a better tomorrow.",
                fontSize = 16.sp,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Stat cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Growth stat card
                StatCard(
                    emoji = "📈",
                    label = "Growth",
                    value = "12 cm"
                )

                // Moisture stat card
                StatCard(
                    emoji = "💧",
                    label = "Moisture",
                    value = "75%"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Get Started button
            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StatCard(emoji: String, label: String, value: String) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Using emoji instead of icon to avoid resource references
            Text(
                text = emoji,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GetStartedPreview() {
    GetStartedScreen()
}