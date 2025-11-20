# Banking App - Aplicación Bancaria Android

![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg)
![Firebase](https://img.shields.io/badge/Backend-Firebase-orange.svg)

## Equipo de Desarrollo

**Proyecto Final - Desarrollo de Aplicaciones Móviles**

- **Miguel Lema**
- **Luis Vera**

---

## Descripción del Proyecto

**Banking App** es una aplicación bancaria móvil completa desarrollada en Android nativo con Kotlin. La aplicación permite a los usuarios gestionar sus finanzas personales de manera segura y eficiente, ofreciendo funcionalidades como:

- Gestión de cuentas bancarias
- Transferencias entre cuentas
- Historial de transacciones
- Simulador y solicitud de préstamos
- Sistema de notificaciones
- Autenticación segura con Firebase

### Características Principales

- **Arquitectura Clean Architecture**: Separación clara entre capas de dominio, datos y presentación
- **Patrón MVVM**: ViewModel para gestión de estado y lógica de presentación
- **Jetpack Compose**: UI moderna y declarativa
- **Firebase Integration**:
  - Authentication para manejo de usuarios
  - Cloud Firestore para almacenamiento de datos
  - Analytics para seguimiento de uso
- **Gestión Reactiva**: StateFlow para manejo de estados
- **Navegación Moderna**: Navigation Compose
- **Animaciones**: Lottie para animaciones fluidas

---

## Tecnologías Utilizadas

### Lenguaje y Framework
- **Kotlin 1.9+**: Lenguaje principal
- **Jetpack Compose**: Framework de UI declarativa
- **Material Design 3**: Sistema de diseño

### Arquitectura
- **Clean Architecture**: Separación de responsabilidades
- **MVVM Pattern**: Model-View-ViewModel
- **Repository Pattern**: Abstracción de fuentes de datos

### Backend y Servicios
- **Firebase Authentication**: Autenticación de usuarios
- **Cloud Firestore**: Base de datos NoSQL en tiempo real
- **Firebase Analytics**: Análisis de uso

### Librerías Principales
- **Navigation Compose**: Navegación declarativa
- **Coroutines**: Programación asíncrona
- **Lottie**: Animaciones JSON
- **Material Icons Extended**: Iconos de Material Design

---

## Estructura del Proyecto

```
app/src/main/java/com/example/bankingapp/
│
├── domain/                    # Capa de Dominio
│   ├── model/                # Modelos de negocio
│   │   ├── Account.kt
│   │   ├── Transaction.kt
│   │   ├── Loan.kt
│   │   ├── User.kt
│   │   └── Notification.kt
│   ├── repository/           # Interfaces de repositorios
│   └── usecase/              # Casos de uso
│
├── data/                      # Capa de Datos
│   ├── dto/                  # Data Transfer Objects
│   ├── mapper/               # Mappers DTO ↔ Domain
│   ├── remote/firebase/      # Firebase DataSources
│   └── repository/           # Implementación de repositorios
│
├── ui/                        # Capa de Presentación
│   ├── auth/                 # Autenticación
│   │   ├── login/
│   │   ├── register/
│   │   └── reset/
│   ├── home/                 # Dashboard principal
│   ├── transactions/         # Gestión de transacciones
│   ├── transfer/             # Transferencias
│   ├── loans/                # Préstamos
│   │   ├── simulator/
│   │   ├── request/
│   │   ├── history/
│   │   └── detail/
│   ├── notifications/        # Notificaciones
│   ├── components/           # Componentes reutilizables
│   └── theme/                # Tema y estilos
│
└── core/                      # Núcleo de la aplicación
    ├── navigation/           # Configuración de navegación
    ├── util/                 # Utilidades
    └── constants/            # Constantes
```

---

## Requisitos Previos

Antes de ejecutar la aplicación, asegúrate de tener instalado:

- **Android Studio** (versión Hedgehog o superior)
- **JDK 17**
- **Android SDK** (API 26 o superior)
- **Git**
- **Cuenta de Firebase** (para configuración del backend)

---

## Cómo Ejecutar la Aplicación

### 1. Clonar el Repositorio

```bash
git clone https://github.com/LuisVeraVR/A36-project.git
cd A36-project
```

### 2. Configurar Firebase

#### Opción A: Usar Configuración Existente
El proyecto ya incluye el archivo `google-services.json` configurado. Si deseas usar tu propia instancia de Firebase:

#### Opción B: Crear tu Propio Proyecto Firebase

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Crea un nuevo proyecto o usa uno existente
3. Agrega una aplicación Android:
   - Package name: `com.example.bankingapp`
4. Descarga el archivo `google-services.json`
5. Colócalo en `app/google-services.json`

6. Habilita los siguientes servicios:
   - **Authentication** → Email/Password
   - **Cloud Firestore** → Crear base de datos

7. Configurar reglas e índices de Firestore:

```bash
# Instalar Firebase CLI
npm install -g firebase-tools

# Login
firebase login

# Inicializar (si es necesario)
firebase init firestore

# Desplegar reglas e índices
firebase deploy --only firestore:rules
firebase deploy --only firestore:indexes
```

### 3. Abrir el Proyecto en Android Studio

1. Abre Android Studio
2. Selecciona **Open an Existing Project**
3. Navega hasta la carpeta `A36-project`
4. Espera a que Gradle sincronice las dependencias

### 4. Compilar el Proyecto

```bash
./gradlew build
```

O desde Android Studio: **Build > Make Project**

### 5. Ejecutar en Emulador o Dispositivo Físico

#### Usando Emulador:
1. Abre **AVD Manager** (Android Virtual Device)
2. Crea un dispositivo virtual o selecciona uno existente
3. Inicia el emulador
4. Click en **Run** (▶️) en Android Studio

#### Usando Dispositivo Físico:
1. Habilita **Opciones de Desarrollador** en tu dispositivo Android
2. Activa **Depuración USB**
3. Conecta el dispositivo a tu computadora
4. Selecciona tu dispositivo en Android Studio
5. Click en **Run** (▶️)

### 6. Ejecutar Tests

#### Tests Unitarios:
```bash
./gradlew test
```

#### Tests Instrumentados:
```bash
./gradlew connectedAndroidTest
```

O desde Android Studio: **Run > Run 'All Tests'**

---

## Configuración de Firestore

El proyecto incluye configuración automática de Firestore. Los archivos relevantes son:

- `firestore.rules`: Reglas de seguridad
- `firestore.indexes.json`: Índices compuestos necesarios
- `FIRESTORE_SETUP.md`: Guía detallada de configuración

### Colecciones Principales

- **users**: Información de usuarios
- **accounts**: Cuentas bancarias
- **transactions**: Historial de transacciones
- **loans**: Préstamos
- **notifications**: Notificaciones del sistema

---

## Funcionalidades Principales

### 1. Autenticación
- Registro de nuevos usuarios
- Login con email y contraseña
- Recuperación de contraseña
- Cierre de sesión

### 2. Dashboard
- Visualización de balance actual
- Resumen de transacciones recientes
- Accesos rápidos a funcionalidades

### 3. Transacciones
- Listado completo de transacciones
- Filtrado por tipo (ingresos, gastos, transferencias)
- Detalle de cada transacción

### 4. Transferencias
- Transferir dinero a otras cuentas
- Validación de saldo disponible
- Confirmación de operación

### 5. Préstamos
- Simulador de préstamos con cálculo de intereses
- Solicitud de préstamos
- Historial de préstamos activos y pagados
- Detalle de cada préstamo

### 6. Notificaciones
- Centro de notificaciones
- Alertas de transacciones
- Avisos de préstamos
- Marcar como leídas

---

## Flujo de Usuario

1. **Splash Screen** → Pantalla de bienvenida con animación
2. **Login/Registro** → Autenticación del usuario
3. **Dashboard** → Pantalla principal con balance y resumen
4. **Operaciones** → Realizar transacciones, transferencias, préstamos
5. **Historial** → Consultar movimientos y préstamos

---

## Arquitectura y Patrones

### Clean Architecture

El proyecto está organizado en tres capas principales:

1. **Domain Layer**: Lógica de negocio pura
   - Modelos de dominio
   - Interfaces de repositorios
   - Casos de uso

2. **Data Layer**: Acceso a datos
   - Implementación de repositorios
   - Data sources (Firebase)
   - DTOs y mappers

3. **Presentation Layer**: UI y ViewModels
   - Composables (Jetpack Compose)
   - ViewModels (MVVM)
   - Estados de UI

### Flujo de Datos

```
UI (Composable) → ViewModel → Repository → DataSource → Firebase
                    ↓             ↓            ↓
                UiState      Domain Model    DTO
```

---

## Seguridad

- **Autenticación**: Firebase Authentication con email/password
- **Reglas de Firestore**: Solo usuarios autenticados pueden acceder a sus datos
- **Validación**: Validaciones tanto en cliente como en servidor
- **Encriptación**: Firebase maneja encriptación de contraseñas automáticamente

---

## Testing

El proyecto incluye tests en varios niveles:

- **Unit Tests**: Tests de lógica de negocio (Use Cases)
- **ViewModel Tests**: Validación de estados y flujos
- **UI Tests**: Tests de Composables (pendientes de expansión)

---

## Documentación Adicional

- **GUION_EXPLICACION_CODIGO.md**: Guion detallado para presentación del código
- **FIRESTORE_SETUP.md**: Guía de configuración de Firestore
- **firestore.rules**: Reglas de seguridad de la base de datos
- **firestore.indexes.json**: Índices compuestos

---

## Problemas Conocidos y Soluciones

### Error de Índices de Firestore
Si aparecen errores relacionados con índices:
```bash
firebase deploy --only firestore:indexes
```

### Crash al Cargar Transacciones
Verificar que los índices de Firestore estén desplegados correctamente.

---

## Mejoras Futuras

- [ ] Pagos con QR
- [ ] Tarjetas virtuales
- [ ] Gráficos de análisis financiero
- [ ] Autenticación biométrica
- [ ] Notificaciones push
- [ ] Modo oscuro
- [ ] Soporte multi-idioma
- [ ] Exportación de reportes en PDF
- [ ] Modo offline con sincronización

---

## Licencia

Este proyecto fue desarrollado con fines educativos como parte del curso de Desarrollo de Aplicaciones Móviles.

---

## Contacto

**Equipo de Desarrollo**:
- Miguel Lema
- Luis Vera

**Repositorio**: [https://github.com/LuisVeraVR/A36-project](https://github.com/LuisVeraVR/A36-project)

---

## Agradecimientos

- **Firebase** por proporcionar una plataforma robusta de backend
- **Jetpack Compose** por revolucionar el desarrollo de UI en Android
- **Material Design** por las guías de diseño
- **Lottie** por las hermosas animaciones

---

**Desarrollado con ❤️ por Miguel Lema y Luis Vera**
