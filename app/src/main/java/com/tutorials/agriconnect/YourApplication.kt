package com.example.agriconnect

import android.app.Application
import com.google.firebase.FirebaseApp

class AgriconnectApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}