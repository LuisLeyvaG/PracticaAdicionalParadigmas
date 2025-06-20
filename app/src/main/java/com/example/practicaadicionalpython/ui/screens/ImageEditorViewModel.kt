import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicaadicionalpython.data.JavaRepository
import com.example.practicaadicionalpython.data.PythonRepository
import com.example.practicaadicionalpython.domain.Filtro
import com.example.practicaadicionalpython.domain.Imagen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Deque
import java.util.LinkedList

class ImageEditorViewModel : ViewModel() {

    private val pythonRepository = PythonRepository()
    private val javaRepository = JavaRepository()

    private val _uiState = MutableLiveData<UiState>(UiState())
    val uiState: LiveData<UiState> get() = _uiState

    // La fuente de la verdad ahora es una pila (Stack/Deque) de Uris.
    // Usamos Deque que es la interfaz moderna para pilas en Java/Kotlin.
    private val _imageHistory = MutableLiveData<Deque<Imagen>>(LinkedList())
    val imageHistory: LiveData<Deque<Imagen>> get() = _imageHistory

    // Lista de filtros disponibles.
    val availableFilters: List<Filtro> = JavaRepository.getAvailableFilters()

    // Al seleccionar una imagen nueva, se reinicia el historial.
    fun onImageSelected(uri: Uri) {
        Log.d("ViewModel", "Imagen seleccionada: $uri")
        val newHistory: Deque<Imagen> = LinkedList()
        val image = Imagen(uri, null, null) // Creamos un objeto Imagen
        newHistory.add(image)
        _imageHistory.value = newHistory
    }

    // La lógica para deshacer el último cambio.
    fun undo() {
        val currentHistory = LinkedList(_imageHistory.value)
        if (currentHistory.size > 1) {
            currentHistory.removeLast() // Quita el último filtro aplicado
            _imageHistory.value = currentHistory // Notifica a los observadores del cambio
        }
    }

    fun onSomeFilterButtonClicked(context: Context, filter: Filtro) {
        val currentHistory = _imageHistory.value
        val currentImageUri = currentHistory?.peekLast()?.uri // El input es siempre la última imagen del historial

        if (currentImageUri == null) {
            Log.e("ViewModel", "El historial de imágenes está vacío.")
            /*_uiState.value = _uiState.value?.copy(error = "No hay imagen para procesar.")*/
            return
        }

        /*_uiState.value = _uiState.value?.copy(isLoading = true, error = null)*/

        // El nombre del archivo de entrada ahora puede ser dinámico
        val inputFile = copyUriToLocalFile(context, currentImageUri, "input_${System.currentTimeMillis()}.jpg")

        if (inputFile == null) {
            /*_uiState.value = _uiState.value?.copy(isLoading = false, error = "No se pudo acceder a la imagen.")*/
            return
        }

        val inputPath = inputFile.absolutePath
        val outputFile = File(context.cacheDir, "output_${System.currentTimeMillis()}_${filter}.jpg")
        val outputPath = outputFile.absolutePath // Le pasamos la ruta a Python

        viewModelScope.launch {
            Log.d("ViewModel", "Aplicando filtro '$filter' a '${inputPath}'...")

            // Pasamos la ruta de salida a Python
            val result = pythonRepository.applyFilter(inputPath, outputPath, filter.pythonFunctionName)

            result.onSuccess { pathResultado -> // pathResultado es el String que devuelve Python
                Log.d("ViewModel", "Filtro aplicado. Archivo de salida en $pathResultado")

                // Usamos el FileProvider para obtener una Uri segura para nuestro archivo de salida.
                val authority = "com.example.practicaadicionalpython.fileprovider"
                val newImageUri = FileProvider.getUriForFile(context, authority, outputFile)
                val newImage = Imagen(newImageUri, outputPath, filter)

                // El resto de la lógica sigue igual
                val currentHistory = LinkedList(_imageHistory.value)
                currentHistory.addLast(newImage)
                _imageHistory.value = currentHistory
                Log.d("ViewModel", "Nuevo historial de imágenes: $currentHistory")

                /*_uiState.value = _uiState.value?.copy(isLoading = false)*/

            }.onFailure { error ->
                Log.e("ViewModel", "No se pudo aplicar el filtro.", error)
                /*_uiState.value = _uiState.value?.copy(isLoading = false, error = error.message ?: "Error desconocido")*/
            }
        }
    }

    /**
     * Guarda la imagen actual en la galería del dispositivo.
     */
    fun saveCurrentImageToGallery(context: Context) {
        val currentImage = _imageHistory.value?.peekLast()
        if (currentImage == null) {
            _uiState.value = _uiState.value?.copy(userMessage = "No hay imagen para guardar.")
            return
        }

        _uiState.value = _uiState.value?.copy(/*isLoading = true,*/ userMessage = null)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val resolver = context.contentResolver
                val displayName = "Editada_${System.currentTimeMillis()}.jpg"

                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PracticaAdicional")
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }
                }

                val galleryUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw Exception("No se pudo crear el archivo en la galería.")

                resolver.openOutputStream(galleryUri).use { outputStream ->
                    if (outputStream == null) throw Exception("No se pudo abrir el stream de salida.")
                    resolver.openInputStream(currentImage.uri).use { inputStream ->
                        if (inputStream == null) throw Exception("No se pudo abrir el stream de la imagen cacheada.")
                        inputStream.copyTo(outputStream)
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(galleryUri, contentValues, null, null)
                }

                _uiState.postValue(_uiState.value?.copy(/*isLoading = false,*/ userMessage = "Imagen guardada en la galería."))
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.postValue(_uiState.value?.copy(/*isLoading = false,*/ userMessage = "Error al guardar: ${e.message}"))
            }
        }
    }

    /**
     * Resetea el mensaje para el usuario una vez que ha sido mostrado.
     */
    fun userMessageShown() {
        _uiState.value = _uiState.value?.copy(userMessage = null)
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
        //val isLoading: Boolean = false,
        //val error: String? = null,
        val userMessage: String? = null // Para mostrar mensajes tipo Toast
    )
}