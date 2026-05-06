package com.example.altokeyaa.ui.firestore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Nota(
    val id: String = "",
    val texto: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun FirestoreDemoScreen() {
    var texto by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf(listOf<Nota>()) }
    var mensaje by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    val db = Firebase.firestore
    val coleccion = "notas"
    val scope = rememberCoroutineScope()

    suspend fun cargarNotas() {
        try {
            cargando = true
            val snapshot = db.collection(coleccion).orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING).get().await()
            notas = snapshot.documents.mapNotNull { doc ->
                Nota(
                    id = doc.id,
                    texto = doc.getString("texto") ?: "",
                    timestamp = doc.getLong("timestamp") ?: 0L
                )
            }
            mensaje = "Notas cargadas: ${notas.size}"
            cargando = false
        } catch (e: Exception) {
            mensaje = "Error al cargar: ${e.message}"
            cargando = false
        }
    }

    // Cargar notas al iniciar
    LaunchedEffect(Unit) {
        cargarNotas()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título
        Text(
            "📝 Demo Firestore",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = Color(0xFFE91E63)
        )

        Text(
            "Guarda y lista tus notas en la nube",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        // Input + Botón
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Escribe tu nota...") },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = false,
                    maxLines = 3
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (texto.isNotBlank()) {
                                scope.launch {
                                    try {
                                        val nuevoTexto = texto.trim()
                                        val timestamp = System.currentTimeMillis()

                                        // Agregar localmente al instante (sin esperar Firestore)
                                        val notaTemporal = Nota(
                                            id = "temp-${timestamp}",
                                            texto = nuevoTexto,
                                            timestamp = timestamp
                                        )
                                        notas = listOf(notaTemporal) + notas
                                        texto = ""
                                        mensaje = "✅ Guardando..."

                                        // Guardar en Firestore en background
                                        val nuevaNota = mapOf(
                                            "texto" to nuevoTexto,
                                            "timestamp" to timestamp
                                        )
                                        db.collection(coleccion).add(nuevaNota).await()
                                        mensaje = "✅ Nota guardada"
                                    } catch (e: Exception) {
                                        mensaje = "❌ Error: ${e.message}"
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !cargando
                    ) {
                        Text("Guardar", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            scope.launch { cargarNotas() }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !cargando
                    ) {
                        Text("Recargar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Mensaje de estado
        if (mensaje.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (mensaje.startsWith("✅")) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp)
                    ),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (mensaje.startsWith("✅")) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                )
            ) {
                Text(
                    mensaje,
                    modifier = Modifier.padding(12.dp),
                    color = if (mensaje.startsWith("✅")) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
            }
        }

        // Lista de notas
        if (notas.isEmpty() && !cargando) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay notas. ¡Crea una!", color = Color.Gray)
            }
        } else if (cargando) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFE91E63))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notas) { nota ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                nota.texto,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            IconButton(
                                onClick = {
                                    scope.launch {
                                        try {
                                            // Eliminar localmente al instante
                                            notas = notas.filter { it.id != nota.id }
                                            mensaje = "🗑️ Eliminando..."

                                            // Eliminar en Firestore en background
                                            db.collection(coleccion).document(nota.id).delete().await()
                                            mensaje = "🗑️ Nota eliminada"
                                        } catch (e: Exception) {
                                            mensaje = "❌ Error al eliminar"
                                            // Recargar la nota si falla
                                            cargarNotas()
                                        }
                                    }
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFE91E63))
                            }
                        }
                    }
                }
            }
        }
    }
}

