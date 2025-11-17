package com.example.bankingapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.bankingapp.core.navigation.NavigationHost
import com.example.bankingapp.core.navigation.Routes
import com.example.bankingapp.ui.theme.BankingAppTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handler global de excepciones no capturadas
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("MainActivity", "Uncaught exception en $thread", throwable)
            throwable.printStackTrace()
        }

        try {
            FirebaseApp.initializeApp(this)
            Log.d("MainActivity", "✅ Firebase inicializado")
        } catch (e: Exception) {
            Log.e("MainActivity", "❌ Error Firebase", e)
        }

        enableEdgeToEdge()

        setContent {
            BankingAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavigationHost(
                        navController = navController,
                        startDestination = Routes.Splash.route
                    )
                }
            }
        }
    }
}