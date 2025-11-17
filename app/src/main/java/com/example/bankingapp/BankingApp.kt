package com.example.bankingapp

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class BankingApp : Application() {

    override fun onCreate() {
        super.onCreate()

        try {
            FirebaseApp.initializeApp(this)
            Log.d("BankingApp", "✅ Firebase inicializado correctamente")
        } catch (e: Exception) {
            Log.e("BankingApp", "❌ Error al inicializar Firebase", e)
        }
    }
}