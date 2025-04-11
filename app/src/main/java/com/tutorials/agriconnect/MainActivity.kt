package com.tutorials.agriconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tutorials.agriconnect.ui.theme.AgriconnectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgriconnectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent()
                }
            }
        }
    }
}

@Composable
fun MainContent() {
    // Test with either "Sowing & Planting Equipment" or "Land Preparation Equipment"
    CropSpecificScreen(
        cropName = "Coconut",
        onBackClick = {
            // Do nothing or implement a simple back action
        }
    )
}

@Preview(showBackground = true)
@Composable
fun APreview() {
    AgriconnectTheme {
        MainContent()
    }
}