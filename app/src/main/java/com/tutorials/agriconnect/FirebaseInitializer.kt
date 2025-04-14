package com.tutorials.agriconnect

import android.app.Application
import com.google.firebase.FirebaseApp

class AgriConnectApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}