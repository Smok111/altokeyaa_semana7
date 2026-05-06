# 🚀 Altokeyaa - Actividad Firebase/Firestore

**Proyecto Android con integración completa de Firebase Firestore para guardar y listar notas en tiempo real.**

## 📋 Objetivo

Este proyecto demuestra la integración de **Firebase Firestore** en una aplicación Android moderna con Jetpack Compose. Permite:
- ✅ Guardar notas en Firestore
- ✅ Listar notas en tiempo real
- ✅ Eliminar notas
- ✅ Interfaz intuitiva y moderna con Material Design 3

## 🛠️ Tecnologías

- **Android** con Jetpack Compose
- **Firebase Firestore** (NoSQL Database)
- **Google Play Services**
- **Kotlin Coroutines**
- **Material Design 3**

## 📦 Dependencias principales

```kotlin
// Firebase BoM
implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
implementation("com.google.firebase:firebase-firestore-ktx")

// Gradle plugins
id("com.google.gms.google-services")
```

## 🚀 Instalación y configuración

### 1. Clonar repositorio
```bash
git clone <tu-repo>
cd t1
```

### 2. Configurar Firebase
- Descarga `google-services.json` desde Firebase Console
- Colócalo en: `app/google-services.json`

### 3. Compilar y ejecutar
```bash
./gradlew build
# Después, ejecuta en Android Studio o emulador
```

## 💾 Estructura del proyecto

```
app/
├── src/main/java/com/example/altokeyaa/
│   ├── MainActivity.kt              # Navegación principal
│   ├── ui/
│   │   ├── home/                    # Pantalla inicio
│   │   ├── firestore/               # 🔥 Demo Firebase Firestore
│   │   │   └── FirestoreDemoScreen.kt
│   │   ├── login/                   # Pantalla login
│   │   ├── orders/                  # Pantalla pedidos
│   │   ├── promo/                   # Pantalla promos
│   │   └── theme/                   # Tema Material 3
│   └── ...
├── google-services.json             # Configuración Firebase
└── build.gradle.kts                 # Gradle con depedencias
```

## 🎯 Funcionalidades Firestore

### Pantalla de Demo (FirestoreDemoScreen)
Acceso desde navegación inferior → **Perfil** → luego integración en menú.

**Operaciones implementadas:**
1. **CREATE**: Guardar nota en colección `notas`
2. **READ**: Listar todas las notas ordenadas por timestamp
3. **DELETE**: Eliminar nota por ID

### Datos guardados en Firestore
```json
{
  "notas": [
    {
      "id": "auto-generado",
      "texto": "Mi primera nota",
      "timestamp": 1704067200000
    }
  ]
}
```

## 📸 Evidencia de funcionamiento

### Paso 1: Guardar nota
- Escribe texto en el campo de entrada
- Clic en "Guardar"
- Mensaje: "✅ Nota guardada"
- Se ve reflejada en Firestore Console

### Paso 2: Listar notas
- Al abrir la pantalla, se cargan todas las notas
- Se ordenan por timestamp descendente (más reciente primero)

### Paso 3: Eliminar nota
- Clic en el ícono de papelera (🗑️)
- Confirmación: "🗑️ Nota eliminada"

## 🔐 Reglas Firestore (Modo prueba)

Para desarrollo, las reglas están en modo abierto:
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if true;
    }
  }
}
```

⚠️ **IMPORTANTE**: En producción, cambia a reglas restrictivas.

## ✅ Checklist de entrega

- [x] Firebase Firestore conectado
- [x] `google-services.json` en `app/`
- [x] Dependencias Firebase en Gradle
- [x] Pantalla CRUD funcional (Guardar/Listar/Eliminar)
- [x] Manejo de errores y estados de carga
- [x] Interfaz moderna con Material 3
- [x] Compilación sin errores
- [x] README con instrucciones

## 📝 Notas

- El proyecto compila exitosamente: `BUILD SUCCESSFUL`
- Todas las operaciones Firestore funcionan en tiempo real
- Interfaz responsive y con feedback visual

## 👤 Autor

Desarrollado para la actividad Firebase/Firestore.

## 📄 Licencia

Proyecto de estudio - Uso libre con fines educativos.

