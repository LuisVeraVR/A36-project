package com.example.bankingapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TestFirebaseConnection : ComponentActivity() {

    private val TAG = "FirebaseTest"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase explícitamente
        try {
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "✅ Firebase inicializado correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al inicializar Firebase", e)
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TestFirebaseScreen()
                }
            }
        }

        // Probar conexión
        testFirestoreConnection()
    }

    private fun testFirestoreConnection() {
        try {
            val db = Firebase.firestore
            Log.d(TAG, "📊 Instancia de Firestore obtenida")

            // Intento 1: Leer colección accounts
            db.collection("accounts")
                .limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    Log.d(TAG, "✅ Conexión exitosa a 'accounts'! Documentos: ${documents.size()}")
                    for (document in documents) {
                        Log.d(TAG, "Documento: ${document.id}")
                        Log.d(TAG, "Datos: ${document.data}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "❌ Error al leer 'accounts'", exception)
                    Log.e(TAG, "Tipo de error: ${exception.javaClass.simpleName}")
                    Log.e(TAG, "Mensaje: ${exception.message}")
                }

            // Intento 2: Leer colección transactions
            db.collection("transactions")
                .limit(3)
                .get()
                .addOnSuccessListener { documents ->
                    Log.d(TAG, "✅ Conexión exitosa a 'transactions'! Documentos: ${documents.size()}")
                    documents.forEach { doc ->
                        Log.d(TAG, "Transacción ${doc.id}: ${doc.data}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "❌ Error al leer 'transactions'", exception)
                }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error general de Firestore", e)
        }
    }
}

@Composable
fun TestFirebaseScreen() {
    var connectionStatus by remember { mutableStateOf("Probando conexión...") }
    var accountsCount by remember { mutableStateOf(0) }
    var transactionsCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val db = Firebase.firestore

        // Probar accounts
        db.collection("accounts")
            .get()
            .addOnSuccessListener { documents ->
                accountsCount = documents.size()
                connectionStatus = "✅ Conectado"
            }
            .addOnFailureListener {
                connectionStatus = "❌ Error de conexión"
            }

        // Probar transactions
        db.collection("transactions")
            .get()
            .addOnSuccessListener { documents ->
                transactionsCount = documents.size()
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Estado de Firebase",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Conexión: $connectionStatus")
                Text("Cuentas: $accountsCount")
                Text("Transacciones: $transactionsCount")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Revisa Logcat para más detalles",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}