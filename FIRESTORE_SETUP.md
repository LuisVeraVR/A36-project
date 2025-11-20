# Configuración de Firestore

Este documento explica cómo configurar los índices de Firestore para la aplicación A36 Banking App.

## Error de Índices

Si ves un error como este:
```
FAILED_PRECONDITION: The query requires an index. You can create it here: https://console.firebase.google.com/...
```

Significa que Firestore necesita índices compuestos para ejecutar consultas con múltiples filtros u ordenamientos.

## Solución

### Opción 1: Crear índices automáticamente (Recomendado)

1. **Instalar Firebase CLI** (si no lo tienes):
   ```bash
   npm install -g firebase-tools
   ```

2. **Iniciar sesión en Firebase**:
   ```bash
   firebase login
   ```

3. **Inicializar el proyecto** (si no está inicializado):
   ```bash
   firebase init firestore
   ```
   - Selecciona el proyecto `a36-project`
   - Acepta usar los archivos `firestore.rules` y `firestore.indexes.json` existentes

4. **Desplegar los índices**:
   ```bash
   firebase deploy --only firestore:indexes
   ```

5. **Esperar** a que los índices se creen (puede tomar unos minutos)

### Opción 2: Crear índices manualmente

Si prefieres crear los índices uno por uno:

1. Cuando veas el error, copia el enlace que aparece en el mensaje
2. Abre el enlace en tu navegador
3. Haz clic en "Crear índice"
4. Espera a que se complete la creación

## Índices Configurados

El archivo `firestore.indexes.json` incluye índices para las siguientes colecciones:

### 1. **transactions** (Transacciones)
- `userId` + `type` + `date` (DESC)
- `userId` + `category` + `date` (DESC)

### 2. **notifications** (Notificaciones)
- `userId` + `isRead` + `timestamp` (DESC)

### 3. **loans** (Préstamos)
- `userId` + `status` + `createdAt` (DESC)

### 4. **messages** (Mensajes)
- `egU_isTrue` + `created`

## Verificar el Estado de los Índices

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Selecciona tu proyecto `a36-project`
3. Navega a **Firestore Database** → **Indexes**
4. Verifica que todos los índices estén en estado "Enabled"

## Problemas Comunes

### Error al desplegar
```
Error: HTTP Error: 403, Missing necessary permission...
```
**Solución**: Asegúrate de tener permisos de propietario o editor en el proyecto Firebase.

### Los índices tardan mucho
- La creación de índices puede tomar desde unos minutos hasta horas, dependiendo del tamaño de tus colecciones
- Puedes continuar desarrollando mientras se crean

### Error persiste después de crear índices
- Asegúrate de que el índice esté en estado "Enabled" en Firebase Console
- Limpia la caché de la app: Settings → Clear Data
- Reinicia la aplicación

## Recursos

- [Documentación de Índices de Firestore](https://firebase.google.com/docs/firestore/query-data/indexing)
- [Firebase CLI Reference](https://firebase.google.com/docs/cli)
