package com.example.practicaadicionalpython.data

import android.util.Log
import com.chaquo.python.PyException
import com.chaquo.python.Python
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Una clase simple para conectar con el backend de Python.
 * Utiliza coroutines de Kotlin para realizar el trabajo en segundo plano.
 */
class PythonRepository {

    // Obtiene una referencia al módulo 'main.py' de Python.
    private val mainModule = Python.getInstance().getModule("main")

    /**
     * Aplica un filtro a una imagen llamando al script de Python.
     *
     * @param inputPath La ruta de la imagen original.
     * @param outputPath La ruta donde se guardará la nueva imagen.
     * @param filterName El nombre del filtro a aplicar (ej. "grises").
     * @return Un objeto [Result] que indica éxito o encapsula el error.
     */
    suspend fun applyFilter(
        inputPath: String,
        outputPath: String,
        filterName: String
    ): Result<Unit> {
        // withContext(Dispatchers.IO) mueve la ejecución a un hilo de fondo,
        // evitando que la app se congele. Es la forma moderna de hacer esto.
        return withContext(Dispatchers.IO) {
            try {
                // Llama a la función 'aplicar_filtro_a_imagen' en main.py
                mainModule.callAttr(
                    "aplicar_filtro_a_imagen",
                    inputPath,
                    outputPath,
                    filterName
                )

                // Si la llamada no falla, devuelve éxito.
                Log.d("PythonConnection", "Filtro '$filterName' aplicado exitosamente.")
                Result.success(Unit)

            } catch (e: PyException) {
                // Si Python lanza un error, lo capturamos y devolvemos como fallo.
                Log.e("PythonConnection", "Error en el script de Python: ${e.message}")
                Result.failure(e)
            }
        }
    }
}