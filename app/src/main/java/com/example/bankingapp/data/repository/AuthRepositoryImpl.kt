package com.example.bankingapp.data.repository

import android.util.Log
import com.example.bankingapp.data.dto.AccountDto
import com.example.bankingapp.domain.model.User
import com.example.bankingapp.domain.repository.AuthRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Implementación del repositorio de autenticación con mensajes en español.
 */
class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val ACCOUNTS_COLLECTION = "accounts"
        private const val TAG = "AuthRepository"
    }

    /**
     * Login con email (método original).
     */
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Usuario no encontrado")

            val user = User(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                fullName = firebaseUser.displayName ?: ""
            )

            Log.d(TAG, "Login exitoso: ${user.email}")
            Result.success(user)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e(TAG, "Credenciales inválidas", e)
            Result.failure(Exception("Contraseña incorrecta"))
        } catch (e: FirebaseAuthInvalidUserException) {
            Log.e(TAG, "Usuario no existe", e)
            Result.failure(Exception("El usuario no existe"))
        } catch (e: Exception) {
            Log.e(TAG, "Error en login", e)
            Result.failure(Exception("Error al iniciar sesión: ${e.localizedMessage}"))
        }
    }

    /**
     * Login con username (busca el email asociado primero).
     */
    override suspend fun loginWithUsername(username: String, password: String): Result<User> {
        return try {
            Log.d(TAG, "Intentando login con username: $username")

            // Buscar el email asociado al username
            val querySnapshot = firestore.collection(USERS_COLLECTION)
                .whereEqualTo("username", username)
                .limit(1)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                Log.w(TAG, "Username no encontrado: $username")
                return Result.failure(Exception("El usuario no existe"))
            }

            val email = querySnapshot.documents[0].getString("email")
                ?: return Result.failure(Exception("Email no encontrado para este usuario"))

            Log.d(TAG, "Email encontrado para username: $email")

            // Hacer login con el email encontrado
            login(email, password)
        } catch (e: Exception) {
            Log.e(TAG, "Error en loginWithUsername", e)
            Result.failure(Exception("Usuario o contraseña incorrectos"))
        }
    }

    /**
     * Registro con username y mensajes en español.
     */
    override suspend fun register(
        email: String,
        password: String,
        fullName: String,
        username: String
    ): Result<User> {
        return try {
            Log.d(TAG, "Iniciando registro - Username: $username, Email: $email")

            // Verificar que el username no exista
            val usernameExists = checkUsernameExists(username)
            if (usernameExists) {
                Log.w(TAG, "Username ya existe: $username")
                return Result.failure(Exception("El nombre de usuario ya está en uso"))
            }

            Log.d(TAG, "Username disponible, creando cuenta en Firebase Auth")

            // Crear usuario en Firebase Auth
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Error al crear usuario")

            Log.d(TAG, "Usuario creado en Auth: ${firebaseUser.uid}")

            // Actualizar perfil con el nombre
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(fullName)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            Log.d(TAG, "Perfil actualizado, guardando en Firestore")

            // Crear documento de usuario en Firestore
            val userData = hashMapOf(
                "id" to firebaseUser.uid,
                "email" to email,
                "fullName" to fullName,
                "username" to username,
                "createdAt" to Timestamp.now()
            )

            firestore.collection(USERS_COLLECTION)
                .document(firebaseUser.uid)
                .set(userData)
                .await()

            Log.d(TAG, "Usuario guardado en Firestore")

            // Crear cuenta bancaria inicial
            createInitialAccount(firebaseUser.uid)

            Log.d(TAG, "Cuenta bancaria creada")

            val user = User(
                id = firebaseUser.uid,
                email = email,
                fullName = fullName,
                createdAt = System.currentTimeMillis()
            )

            Result.success(user)
        } catch (e: FirebaseAuthUserCollisionException) {
            Log.e(TAG, "Email ya existe", e)
            Result.failure(Exception("Este correo electrónico ya está registrado"))
        } catch (e: FirebaseAuthWeakPasswordException) {
            Log.e(TAG, "Contraseña débil", e)
            Result.failure(Exception("La contraseña es muy débil. Debe tener al menos 6 caracteres"))
        } catch (e: Exception) {
            Log.e(TAG, "Error en registro", e)
            Result.failure(Exception("Error al registrar: ${e.localizedMessage}"))
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Log.d(TAG, "Email de reset enviado a: $email")
            Result.success(Unit)
        } catch (e: FirebaseAuthInvalidUserException) {
            Log.e(TAG, "Usuario no existe", e)
            Result.failure(Exception("No existe una cuenta con este correo electrónico"))
        } catch (e: Exception) {
            Log.e(TAG, "Error en resetPassword", e)
            Result.failure(Exception("Error al enviar correo de recuperación"))
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            auth.signOut()
            Log.d(TAG, "Logout exitoso")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error en logout", e)
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): Result<User?> {
        return try {
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                val user = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    fullName = firebaseUser.displayName ?: ""
                )
                Result.success(user)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo usuario actual", e)
            Result.failure(e)
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Verifica si un username ya existe en Firestore.
     */
    private suspend fun checkUsernameExists(username: String): Boolean {
        return try {
            Log.d(TAG, "Verificando si username existe: $username")

            val querySnapshot = firestore.collection(USERS_COLLECTION)
                .whereEqualTo("username", username)
                .limit(1)
                .get()
                .await()

            val exists = !querySnapshot.isEmpty
            Log.d(TAG, "Username existe: $exists")

            exists
        } catch (e: Exception) {
            Log.e(TAG, "Error verificando username", e)
            false
        }
    }

    /**
     * Crea una cuenta bancaria inicial para el nuevo usuario.
     */
    private suspend fun createInitialAccount(userId: String) {
        try {
            val account = AccountDto(
                userId = userId,
                balance = 0.0,  // Saldo inicial en 0
                currency = "USD",
                accountNumber = generateAccountNumber(),
                accountType = "SAVINGS",
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )

            firestore.collection(ACCOUNTS_COLLECTION)
                .document(userId)
                .set(account)
                .await()

            Log.d(TAG, "Cuenta bancaria creada: ${account.accountNumber}")
        } catch (e: Exception) {
            Log.e(TAG, "Error creando cuenta bancaria", e)
            throw e
        }
    }

    /**
     * Genera un número de cuenta aleatorio de 10 dígitos.
     */
    private fun generateAccountNumber(): String {
        return (1000000000..9999999999).random().toString()
    }
}