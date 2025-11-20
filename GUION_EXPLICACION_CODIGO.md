# Guion de Explicación del Código - Banking App

## 1. INTRODUCCIÓN (2-3 minutos)

### Presentación del Proyecto
"Hola, les voy a presentar nuestro proyecto: **Banking App**, una aplicación móvil bancaria desarrollada en Android nativo con Kotlin."

### Características Principales
- Aplicación bancaria completa con gestión de cuentas, transacciones y préstamos
- Desarrollada con tecnologías modernas de Android
- Arquitectura limpia y escalable
- Integración con Firebase para backend

### Tecnologías Utilizadas
- **Lenguaje**: Kotlin
- **UI Framework**: Jetpack Compose (UI declarativa moderna)
- **Arquitectura**: Clean Architecture + MVVM
- **Backend**: Firebase (Authentication, Firestore)
- **Gestión de Estado**: ViewModels con StateFlow
- **Navegación**: Navigation Compose
- **Animaciones**: Lottie

---

## 2. ARQUITECTURA DEL PROYECTO (5-7 minutos)

### Estructura General
"El proyecto sigue una arquitectura Clean Architecture dividida en 3 capas principales:"

```
app/src/main/java/com/example/bankingapp/
├── domain/          → Capa de Dominio (Lógica de negocio)
├── data/            → Capa de Datos (Acceso a datos)
├── ui/              → Capa de Presentación (Interfaz de usuario)
└── core/            → Utilidades y configuración compartida
```

### 2.1 Capa de Dominio (domain/)

**Propósito**: Contiene la lógica de negocio pura, independiente de frameworks.

#### Modelos de Dominio (domain/model/)
"Aquí definimos las entidades principales del negocio:"

- **User.kt**: Modelo del usuario
- **Account.kt**: Cuenta bancaria (balance, número de cuenta, tipo)
- **Transaction.kt**: Transacciones (depósitos, retiros, transferencias)
- **Loan.kt**: Préstamos solicitados
- **Notification.kt**: Notificaciones del sistema

**Ejemplo - Account.kt**:
```kotlin
data class Account(
    val userId: String,
    val balance: Double,
    val currency: String = "USD",
    val accountNumber: String,
    val accountType: String = "SAVINGS"
)
```

#### Repositorios (domain/repository/)
"Interfaces que definen contratos para acceso a datos:"

- `AuthRepository`: Autenticación (login, registro, logout)
- `AccountRepository`: Gestión de cuentas
- `TransactionRepository`: Operaciones con transacciones
- `LoanRepository`: Gestión de préstamos
- `NotificationRepository`: Notificaciones

#### Use Cases (domain/usecase/)
"Casos de uso que encapsulan lógica de negocio compleja:"

- `SimulateLoanUseCase`: Calcula simulación de préstamos con intereses

### 2.2 Capa de Datos (data/)

**Propósito**: Implementa el acceso a datos desde Firebase.

#### DTOs (data/dto/)
"Objetos de transferencia de datos para Firebase:"

- `UserDto`, `AccountDto`, `TransactionDto`, etc.
- Se mapean a modelos de dominio usando Mappers

#### Firebase DataSources (data/remote/firebase/)
"Clases que interactúan directamente con Firebase:"

- `FirebaseAuthDataSource`: Autenticación con Firebase Auth
- `FirebaseAccountDataSource`: CRUD de cuentas en Firestore
- `FirebaseTransactionDataSource`: Gestión de transacciones
- `FirebaseLoanDataSource`: Operaciones de préstamos
- `FirebaseNotificationDataSource`: Notificaciones

**Características importantes**:
- Manejo de errores robusto
- Uso de coroutines para operaciones asíncronas
- Transformación de datos de Firebase a modelos de dominio

#### Repositorios (data/repository/)
"Implementaciones concretas de las interfaces de dominio:"

- `AuthRepositoryImpl`
- `AccountRepositoryImpl`
- `TransactionRepositoryImpl`
- etc.

**Patrón**: Delegan trabajo a los DataSources y mapean DTOs a modelos de dominio.

### 2.3 Capa de Presentación (ui/)

**Propósito**: Interfaz de usuario construida con Jetpack Compose.

#### Patrón MVVM
"Cada pantalla sigue el patrón Model-View-ViewModel:"

```
Screen (Composable) → ViewModel → Repository → DataSource → Firebase
```

#### Módulos de UI Principales

**1. Autenticación (ui/auth/)**
- `login/`: Pantalla de inicio de sesión
  - `LoginScreen.kt`: UI declarativa con Compose
  - `LoginViewModel.kt`: Lógica de autenticación
  - `LoginUiState.kt`: Estado de la UI (loading, error, success)

- `register/`: Registro de nuevos usuarios
  - Validación de email y contraseña
  - Creación de cuenta en Firebase

- `reset/`: Recuperación de contraseña
  - Envío de email de recuperación

**2. Home (ui/home/)**
- Pantalla principal del usuario
- Muestra balance actual
- Lista de transacciones recientes
- Accesos rápidos a funcionalidades

**3. Transacciones (ui/transactions/)**
- `list/`: Listado de todas las transacciones
  - Filtrado por tipo (ingresos, gastos, transferencias)
  - Ordenamiento por fecha

- `detail/`: Detalle de una transacción específica
  - Información completa de la transacción
  - Comprobante

**4. Transferencias (ui/transfer/)**
- Formulario para realizar transferencias
- Validación de saldo disponible
- Confirmación de transferencia
- Actualización de balance en tiempo real

**5. Préstamos (ui/loans/)**
- `simulator/`: Simulador de préstamos
  - Cálculo de cuotas mensuales
  - Intereses y plazo

- `request/`: Solicitud de préstamo
  - Formulario de solicitud
  - Validación de elegibilidad

- `history/`: Historial de préstamos
  - Préstamos activos y pagados

- `detail/`: Detalle de préstamo
  - Estado, cuotas, pagos realizados

**6. Notificaciones (ui/notifications/)**
- Centro de notificaciones
- Tipos: Transacciones, préstamos, seguridad
- Marca como leídas

**7. Splash (ui/splash/)**
- Pantalla de carga inicial
- Animación con Lottie
- Verificación de autenticación

#### Componentes Reutilizables (ui/components/)
"Componentes personalizados para mantener consistencia:"

- `CustomButton.kt`: Botón con estilo de la app
- `CustomTextField.kt`: Campos de texto personalizados
- `LoadingDialog.kt`: Diálogo de carga
- `ErrorDialog.kt`: Diálogo de errores

### 2.4 Capa Core (core/)

#### Navegación (core/navigation/)
- `Routes.kt`: Definición de rutas de navegación
- `NavigationHost.kt`: Configuración del NavHost
- `NavGraphBuilder.kt`: Construcción del grafo de navegación

#### Utilidades (core/util/)
- `Validators.kt`: Validación de email, contraseña, etc.
- `CurrencyFormatter.kt`: Formateo de moneda
- `DateUtils.kt`: Utilidades para fechas

#### Constantes (core/constants/)
- `Constants.kt`: Valores constantes de la aplicación

---

## 3. FLUJO DE DATOS (3-4 minutos)

### Ejemplo: Realizar una Transferencia

"Vamos a seguir el flujo completo de una transferencia:"

1. **Usuario interactúa con UI** (`TransferScreen.kt`)
   ```kotlin
   // Usuario ingresa datos y presiona "Transferir"
   ```

2. **Screen llama al ViewModel** (`TransferViewModel.kt`)
   ```kotlin
   fun performTransfer(amount: Double, destinationAccount: String)
   ```

3. **ViewModel valida y llama al Repository**
   ```kotlin
   accountRepository.transfer(...)
   transactionRepository.createTransaction(...)
   ```

4. **Repository coordina con DataSources**
   ```kotlin
   // Actualiza balance origen
   // Actualiza balance destino
   // Crea registro de transacción
   ```

5. **DataSource ejecuta operaciones en Firebase**
   ```kotlin
   firestore.collection("accounts").document(id).update(...)
   firestore.collection("transactions").add(...)
   ```

6. **Resultado regresa a UI**
   ```kotlin
   uiState.value = Success / Error
   // UI se actualiza automáticamente
   ```

### Manejo de Estados
"Usamos StateFlow para gestión reactiva de estados:"

```kotlin
sealed class TransferUiState {
    object Idle : TransferUiState()
    object Loading : TransferUiState()
    data class Success(...) : TransferUiState()
    data class Error(val message: String) : TransferUiState()
}
```

---

## 4. INTEGRACIÓN CON FIREBASE (3-4 minutos)

### Firebase Authentication
"Para autenticación de usuarios:"

- Registro con email/password
- Login
- Logout
- Recuperación de contraseña
- Manejo de sesiones

### Cloud Firestore
"Base de datos NoSQL para almacenar:"

#### Colecciones Principales

**1. users/**
```
users/{userId}
├── email
├── name
└── createdAt
```

**2. accounts/**
```
accounts/{accountId}
├── userId
├── balance
├── accountNumber
├── currency
└── accountType
```

**3. transactions/**
```
transactions/{transactionId}
├── userId
├── amount
├── type (INCOME, EXPENSE, TRANSFER)
├── category
├── description
├── timestamp
└── balanceAfter
```

**4. loans/**
```
loans/{loanId}
├── userId
├── amount
├── interestRate
├── months
├── monthlyPayment
├── status (PENDING, APPROVED, REJECTED, PAID)
└── requestDate
```

**5. notifications/**
```
notifications/{notificationId}
├── userId
├── type
├── title
├── message
├── timestamp
└── isRead
```

### Índices de Firestore
"Configurados en `firestore.indexes.json`:"

- Índice para transacciones por usuario y fecha
- Índice para préstamos por usuario y estado
- Índice para notificaciones por usuario y timestamp

### Reglas de Seguridad
"Definidas en `firestore.rules`:"

- Usuarios solo pueden acceder a sus propios datos
- Validación de datos en servidor
- Prevención de modificaciones no autorizadas

---

## 5. CARACTERÍSTICAS DESTACADAS (3-4 minutos)

### 1. Simulador de Préstamos
"Funcionalidad única que permite:"

- Calcular cuotas mensuales según monto y plazo
- Visualizar tabla de amortización
- Comparar diferentes opciones de préstamo

**Use Case**: `SimulateLoanUseCase.kt`
```kotlin
// Calcula intereses, cuotas mensuales, total a pagar
```

### 2. Gestión de Transacciones en Tiempo Real
- Actualización automática de balance
- Historial completo de movimientos
- Categorización de transacciones
- Filtros y búsqueda

### 3. Sistema de Notificaciones
- Notificaciones de transacciones
- Alertas de préstamos
- Avisos de seguridad
- Centro de notificaciones unificado

### 4. Validaciones Robustas
"Sistema completo de validación:"

- Email válido
- Contraseña segura (mínimo 6 caracteres)
- Saldo suficiente para transferencias
- Montos válidos (positivos, no cero)

### 5. Manejo de Errores
"Estrategia multicapa:"

- Try-catch en DataSources
- Estados de error en ViewModels
- Diálogos informativos en UI
- Logs detallados para debugging

### 6. Animaciones con Lottie
- Splash screen animado
- Feedback visual en operaciones
- Mejora la experiencia de usuario

---

## 6. TESTING (2 minutos)

### Unit Tests
**Ubicación**: `app/src/test/`

- `SimulateLoanUseCaseTest.kt`: Pruebas del simulador de préstamos
  - Validación de cálculos de intereses
  - Casos edge (montos límite, plazos máximos)

### Instrumentación Tests
**Ubicación**: `app/src/androidTest/`

- Tests de UI con Compose
- Verificación de navegación
- Tests de integración

---

## 7. CONFIGURACIÓN DEL PROYECTO (2 minutos)

### Dependencias Principales (`build.gradle.kts`)

**Core Android**:
- Kotlin 1.9+
- AndroidX Core KTX
- Lifecycle & ViewModel

**Jetpack Compose**:
- Compose BOM 2024.02.00
- Material3
- Navigation Compose

**Firebase**:
- Firebase BOM 32.7.1
- Authentication
- Firestore
- Analytics

**Utilidades**:
- Coroutines (async/await)
- Lottie (animaciones)

### Configuración de Firebase

**Archivos clave**:
- `google-services.json`: Configuración del proyecto Firebase
- `firestore.rules`: Reglas de seguridad
- `firestore.indexes.json`: Índices de base de datos
- `FIRESTORE_SETUP.md`: Guía de configuración

---

## 8. DEMOSTRACIÓN (5-7 minutos)

### Flujo de Usuario Completo

**1. Primer Uso**
- Splash screen con animación
- Pantalla de login/registro
- Registro de nuevo usuario
- Creación automática de cuenta

**2. Dashboard Principal**
- Visualización de balance
- Últimas transacciones
- Accesos rápidos

**3. Realizar Transferencia**
- Ingresar monto y cuenta destino
- Validar saldo
- Confirmar operación
- Ver actualización de balance

**4. Solicitar Préstamo**
- Simular préstamo
- Ver tabla de amortización
- Enviar solicitud
- Recibir notificación

**5. Ver Historial**
- Transacciones completas
- Filtrar por tipo
- Ver detalles de cada operación

---

## 9. PUNTOS TÉCNICOS DESTACADOS (2-3 minutos)

### 1. Clean Architecture
"Separación clara de responsabilidades:"

- **Domain**: No conoce Android ni Firebase
- **Data**: Puede cambiar de Firebase a otra DB sin afectar domain
- **UI**: Puede cambiar de Compose a otra UI sin afectar lógica

### 2. Programación Reactiva
- StateFlow para estados reactivos
- Compose se re-renderiza automáticamente
- Flujo de datos unidireccional

### 3. Inyección de Dependencias Manual
- Repositorios inyectados en ViewModels
- DataSources inyectados en Repositorios
- Fácil migración a Hilt/Dagger si es necesario

### 4. Seguridad
- Contraseñas manejadas por Firebase Auth (hasheadas)
- Reglas de Firestore protegen datos
- Validaciones cliente y servidor
- No hay datos sensibles en código

### 5. Escalabilidad
- Estructura permite agregar fácilmente:
  - Nuevas pantallas
  - Nuevas funcionalidades
  - Nuevos métodos de pago
  - Integración con APIs externas

---

## 10. DESAFÍOS Y SOLUCIONES (2 minutos)

### Desafío 1: Índices de Firestore
**Problema**: Queries complejas requieren índices
**Solución**: Archivo `firestore.indexes.json` con índices pre-configurados

### Desafío 2: Crash al Cargar Transacciones
**Problema**: Errores de índices causaban crashes
**Solución**:
- Try-catch robusto en DataSources
- Manejo de estados de error en ViewModels
- Feedback claro al usuario

### Desafío 3: Validación de Email
**Problema**: Firebase Auth requiere formato específico
**Solución**: Validador personalizado con regex en `Validators.kt`

### Desafío 4: Sincronización de Balance
**Problema**: Balance desactualizado tras múltiples operaciones
**Solución**:
- Transacciones atómicas en Firestore
- Actualización de timestamp en cada cambio
- Re-fetch automático del balance

---

## 11. MEJORAS FUTURAS (1-2 minutos)

### Funcionalidades Propuestas
1. **Pagos con QR**: Escanear QR para pagar
2. **Tarjetas Virtuales**: Generación de tarjetas temporales
3. **Inversiones**: Módulo de inversión en fondos
4. **Análisis Financiero**: Gráficos de gastos e ingresos
5. **Biometría**: Login con huella/Face ID
6. **Notificaciones Push**: Firebase Cloud Messaging
7. **Modo Oscuro**: Theme switcher
8. **Multi-idioma**: Soporte i18n
9. **Pagos Programados**: Transferencias automáticas
10. **Exportar Reportes**: PDF de transacciones

### Mejoras Técnicas
1. **Inyección de Dependencias**: Migrar a Hilt
2. **Testing**: Aumentar cobertura a 80%+
3. **CI/CD**: GitHub Actions para builds automáticos
4. **Offline First**: Room database para cache local
5. **Performance**: Paginación de transacciones
6. **Monitoreo**: Firebase Crashlytics

---

## 12. CONCLUSIÓN (1 minuto)

### Resumen
"Desarrollamos una aplicación bancaria completa que demuestra:"

- Dominio de Kotlin y Jetpack Compose
- Implementación de Clean Architecture
- Integración efectiva con Firebase
- Manejo robusto de errores
- UI/UX moderna y fluida
- Código limpio y mantenible

### Aprendizajes Clave
- Arquitectura modular facilita mantenimiento
- Firebase acelera desarrollo de backend
- Compose simplifica creación de UI
- Testing es crucial para calidad
- Seguridad debe ser prioridad desde el inicio

### Valor del Proyecto
"Esta app es un ejemplo profesional de desarrollo Android moderno, lista para ser base de un producto real con algunas mejoras adicionales."

---

## ANEXO: COMANDOS ÚTILES

### Compilar el proyecto
```bash
./gradlew build
```

### Ejecutar tests
```bash
./gradlew test
```

### Ejecutar tests instrumentados
```bash
./gradlew connectedAndroidTest
```

### Limpiar proyecto
```bash
./gradlew clean
```

### Deploy de reglas de Firestore
```bash
firebase deploy --only firestore:rules
```

### Deploy de índices de Firestore
```bash
firebase deploy --only firestore:indexes
```

---

## PREGUNTAS FRECUENTES

**Q: ¿Por qué Clean Architecture?**
A: Facilita testing, mantenimiento y escalabilidad. Cada capa tiene una responsabilidad clara.

**Q: ¿Por qué Jetpack Compose en lugar de XML?**
A: Compose es declarativo, reduce boilerplate, facilita animaciones y es el futuro de Android UI.

**Q: ¿Cómo se manejan las transacciones concurrentes?**
A: Firestore maneja atomicidad. Usamos timestamps para ordenamiento y re-fetching para sincronización.

**Q: ¿La app funciona offline?**
A: Actualmente no. Mejora futura: implementar Room + sincronización.

**Q: ¿Cómo se protegen los datos sensibles?**
A: Firebase Auth maneja contraseñas. Firestore Rules controlan acceso. No hay datos sensibles hardcodeados.

---

**Duración Total Estimada**: 30-40 minutos
**Nivel**: Intermedio-Avanzado
**Audiencia**: Desarrolladores, estudiantes, revisores técnicos
