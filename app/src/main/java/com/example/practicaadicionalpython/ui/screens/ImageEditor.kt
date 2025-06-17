package com.example.practicaadicionalpython.ui.screens

import ImageEditorViewModel
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practicaadicionalpython.domain.Filtro
import com.example.practicaadicionalpython.domain.Imagen

@Composable
fun ImageEditorScreen() {

    val viewModel: ImageEditorViewModel = viewModel()

    val imageHistory by viewModel.imageHistory.observeAsState()
    val image = imageHistory?.peekLast()
    val imageUri = image?.uri // Obtenemos la última imagen del historial

    /*// --- LOGS DE DEPURACIÓN ---
    // Este bloque se ejecutará en cada recomposición
    Log.d("DEBUG_UI", "---------------------------------")
    Log.d("DEBUG_UI", "Recomposición disparada.")
    Log.d("DEBUG_UI", "Tamaño del Historial: ${imageHistory?.size}")
    Log.d("DEBUG_UI", "URI Actual para la imagen: $imageUri")
    Log.d("DEBUG_UI", "---------------------------------")
    // --- FIN DE LOGS ---*/

    // --- Lanzador para seleccionar imagen ---
    // Este es el lanzador de actividad de Jetpack Compose.
    // Se registra para obtener contenido (una imagen en este caso).
    // Cuando el usuario selecciona una imagen, el lambda se ejecuta con la Uri de la imagen.
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Una vez que tenemos la Uri, la pasamos al ViewModel para que
            // la procese y actualice el estado.
            viewModel.onImageSelected(it)
        }
    }

    // --- UI Layout ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // --- SECCIÓN DE INFORMACIÓN DE LA IMAGEN (NUEVO) ---
        // Mostramos la tarjeta de información solo si hay una imagen cargada.
        if (image != null) {
            ImageInfoCard(imagen = image)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- Visor de Imagen ---
        // Este contenedor mostrará la imagen seleccionada o un placeholder.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                // Si la Uri de la imagen no es nula, la mostramos.
                // Se recomienda usar una librería como Coil para cargar imágenes de forma asíncrona.
                // Asegúrate de añadir la dependencia de Coil en tu build.gradle:
                // implementation("io.coil-kt:coil-compose:2.5.0")
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUri)
                        .crossfade(true) // Efecto de transición suave
                        .build(),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop // Escala la imagen para que llene el contenedor
                )
            } else {
                // Si no hay imagen, muestra un texto indicativo.
                Text(
                    text = "Ninguna imagen seleccionada.\n\nUsa el botón para elegir una de la galería.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Botón para Seleccionar Imagen ---
        Button(
            onClick = {
                // Al hacer clic, se lanza el selector de imágenes del sistema.
                // Se especifica 'image/*' para que solo muestre archivos de imagen.
                imagePickerLauncher.launch("image/*")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Seleccionar Imagen de la Galería")
        }

        Spacer(modifier = Modifier.height(16.dp))

        val context = LocalContext.current

        // --- Botón para Procesar con Chaquopy (Ejemplo) ---
        // Este botón estaría deshabilitado si no hay imagen.
        // Su lógica estaría en el ViewModel.
        Button(
            onClick = {
                // Aquí llamarías a una función en tu ViewModel que use Chaquopy.
                viewModel.onSomeFilterButtonClicked(context, viewModel.availableFilters[1]) // Ejemplo de filtro
            },
            enabled = imageUri != null, // Solo se habilita si hay una imagen
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = Color.Gray
            )
        ) {
            Text(text = "Procesar con Chaquopy")
        }
    }
}

/**
 * Un Card que muestra información clave de un objeto Imagen.
 * @param imagen El objeto Imagen del cual se extraerán los datos.
 */
@Composable
fun ImageInfoCard(imagen: Imagen, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Título de la tarjeta
            Text(
                text = "Detalles de la Imagen",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            // Separador visual
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Fila para el estado/nombre del filtro
            InfoRow(label = "Nombre:", value = imagen.displayName)

            // Fila para el filtro del archivo
            InfoRow(label = "Filtro:", value = imagen.appliedFilter?.displayName ?: "Ninguno")

        }
    }
}

/**
 * Un Composable auxiliar para mostrar una fila de "Etiqueta: Valor".
 */
@Composable
private fun InfoRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
