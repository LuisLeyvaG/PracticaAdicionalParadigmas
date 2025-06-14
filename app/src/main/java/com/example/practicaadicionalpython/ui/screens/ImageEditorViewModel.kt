import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicaadicionalpython.data.PythonRepository
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Deque
import java.util.LinkedList

class ImageEditorViewModel : ViewModel() {

    private val pythonRepository = PythonRepository()

    private val _uiState = MutableLiveData<UiState>(UiState())
    val uiState: LiveData<UiState> get() = _uiState

    // CAMBIO: La fuente de la verdad ahora es una pila (Stack/Deque) de Uris.
    // Usamos Deque que es la interfaz moderna para pilas en Java/Kotlin.
    private val _imageHistory = MutableLiveData<Deque<Uri>>(LinkedList())
    val imageHistory: LiveData<Deque<Uri>> get() = _imageHistory

    // Al seleccionar una imagen nueva, se reinicia el historial.
    fun onImageSelected(uri: Uri) {
        Log.d("ViewModel", "Imagen seleccionada: $uri")
        val newHistory: Deque<Uri> = LinkedList()
        newHistory.add(uri)
        _imageHistory.value = newHistory
    }

    // NUEVA FUNCIÓN: La lógica para deshacer el último cambio.
    fun undo() {
        val currentHistory = _imageHistory.value
        if (currentHistory != null && currentHistory.size > 1) {
            currentHistory.removeLast() // Quita el último filtro aplicado
            _imageHistory.value = currentHistory!! // Notifica a los observadores del cambio
        }
    }

    fun onSomeFilterButtonClicked(context: Context, filter: String) {
        val currentHistory = _imageHistory.value
        val currentImageUri = currentHistory?.peekLast() // El input es siempre la última imagen del historial

        if (currentImageUri == null) {
            Log.e("ViewModel", "El historial de imágenes está vacío.")
            _uiState.value = _uiState.value?.copy(error = "No hay imagen para procesar.")
            return
        }

        _uiState.value = _uiState.value?.copy(isLoading = true, error = null)

        // El nombre del archivo de entrada ahora puede ser dinámico
        val inputFile = copyUriToLocalFile(context, currentImageUri, "input_${System.currentTimeMillis()}.jpg")

        if (inputFile == null) {
            _uiState.value = _uiState.value?.copy(isLoading = false, error = "No se pudo acceder a la imagen.")
            return
        }

        val inputPath = inputFile.absolutePath
        val outputFile = File(context.cacheDir, "output_${System.currentTimeMillis()}_${filter}.jpg")
        val outputPath = outputFile.absolutePath // Le pasamos la ruta a Python

        viewModelScope.launch {
            Log.d("ViewModel", "Aplicando filtro '$filter' a '${inputPath}'...")

            // Pasamos la ruta de salida a Python
            val result = pythonRepository.applyFilter(inputPath, outputPath, filter)

            result.onSuccess { pathResultado -> // pathResultado es el String que devuelve Python
                Log.d("ViewModel", "Filtro aplicado. Archivo de salida en $pathResultado")

                // Usamos el FileProvider para obtener una Uri segura para nuestro archivo de salida.
                val authority = "com.example.yourapp.fileprovider" // DEBE COINCIDIR CON EL MANIFEST
                val newImageUri = FileProvider.getUriForFile(context, authority, outputFile)
                // --- FIN DE LA CORRECCIÓN ---

                // El resto de la lógica sigue igual
                val currentHistory = _imageHistory.value ?: LinkedList()
                currentHistory.addLast(newImageUri)
                _imageHistory.value = currentHistory

                _uiState.value = _uiState.value?.copy(isLoading = false)

            }.onFailure { error ->
                Log.e("ViewModel", "No se pudo aplicar el filtro.", error)
                _uiState.value = _uiState.value?.copy(isLoading = false, error = error.message ?: "Error desconocido")
            }
        }
    }

    private fun copyUriToLocalFile(context: Context, uri: Uri, filename: String): File? {
        // (Esta función permanece igual)
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)!!
            val tempFile = File(context.cacheDir, filename)
            val outputStream = FileOutputStream(tempFile)
            inputStream.use { input -> outputStream.use { output -> input.copyTo(output) } }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
    )
}