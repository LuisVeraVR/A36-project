package com.example.bankingapp.ui.auth.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bankingapp.ui.components.CustomTextField
import com.example.bankingapp.ui.components.PrimaryButton

/**
 * Pantalla de registro con diseño personalizado.
 *
 * RF01 - HU01: Registro de usuario con validaciones.
 */
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navegar al home cuando el registro sea exitoso
    LaunchedEffect(uiState.isRegisterSuccessful) {
        if (uiState.isRegisterSuccessful) {
            onRegisterSuccess()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Botón de volver
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Título
            Text(
                text = "REGISTRO",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtítulo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Completa todos los campos requeridos, si ya",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Row {
                    Text(
                        text = "tienes una cuenta ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "inicia sesión",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onNavigateBack() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Campos de nombre y apellidos en una fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Nombres
                CustomTextField(
                    value = uiState.firstName,
                    onValueChange = { viewModel.updateFirstName(it) },
                    label = "Nombres:",
                    placeholder = "",
                    modifier = Modifier.weight(1f),
                    isError = uiState.firstNameError != null,
                    errorMessage = uiState.firstNameError
                )

                // Apellidos
                CustomTextField(
                    value = uiState.lastName,
                    onValueChange = { viewModel.updateLastName(it) },
                    label = "Apellidos:",
                    placeholder = "",
                    modifier = Modifier.weight(1f),
                    isError = uiState.lastNameError != null,
                    errorMessage = uiState.lastNameError
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Campo de usuario
            CustomTextField(
                value = uiState.username,
                onValueChange = { viewModel.updateUsername(it) },
                label = "Usuario:",
                placeholder = "Ingrese su usuario",
                isError = uiState.usernameError != null,
                errorMessage = uiState.usernameError
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Campo de email
            CustomTextField(
                value = uiState.email,
                onValueChange = { viewModel.updateEmail(it) },
                label = "Email:",
                placeholder = "example@email.com",
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                isError = uiState.emailError != null,
                errorMessage = uiState.emailError
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Campo de contraseña
            CustomTextField(
                value = uiState.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = "Contraseña:",
                placeholder = "**************",
                isPassword = true,
                isError = uiState.passwordError != null,
                errorMessage = uiState.passwordError
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Mostrar error general si existe
            if (uiState.generalError != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = uiState.generalError!!,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Botón de completar registro
            PrimaryButton(
                text = "Completar Registro",
                onClick = { viewModel.register() },
                enabled = !uiState.isLoading,
                isLoading = uiState.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}