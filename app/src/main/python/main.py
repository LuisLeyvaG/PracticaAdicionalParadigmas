# main.py

from imagenes import Imagen # Importamos la nueva clase Imagen
from filtros import FiltroGrises, FiltroInversion, FiltroSepia, FiltroPosterizar, \
    FiltroSolarizar, FiltroContraste, FiltroDesenfoque, FiltroEnfocar, \
    FiltroDetectarBordes, FiltroContorno, FiltroRelieve, FiltroEspejo, \
    FiltroRotar90

# El diccionario de filtros
FILTROS_DISPONIBLES = {
    "grayscale": FiltroGrises(),
    "invert": FiltroInversion(),
    "sepia": FiltroSepia(),
    "posterize": FiltroPosterizar(),
    "solarize": FiltroSolarizar(),
    "autocontrast": FiltroContraste(),
    "blur": FiltroDesenfoque(),
    "sharpen": FiltroEnfocar(),
    "find_edges": FiltroDetectarBordes(),
    "contour": FiltroContorno(),
    "emboss": FiltroRelieve(),
    "mirror": FiltroEspejo(),
    "rotate90": FiltroRotar90()
}

def aplicar_filtro_a_imagen(ruta_entrada: str, ruta_salida: str, nombre_filtro: str):
    """
    Función principal que orquesta el proceso de edición de imágenes
    utilizando la clase Imagen.
    """
    print("--- Iniciando procesamiento de imagen en Python (versión orientada a objetos) ---")

    filtro_seleccionado = FILTROS_DISPONIBLES.get(nombre_filtro.lower())
    if not filtro_seleccionado:
        error_msg = f"Error: El filtro '{nombre_filtro}' no es válido. Disponibles: {list(FILTROS_DISPONIBLES.keys())}"
        print(error_msg)
        raise ValueError(error_msg)

    try:
        # 1. Crear una instancia de la clase Imagen.
        mi_imagen = Imagen(ruta_entrada)

        # 2. Cargar los datos de la imagen en el objeto.
        mi_imagen.cargar()

        # 3. Aplicar el filtro a los datos de la imagen del objeto.
        #    El filtro devuelve un nuevo objeto de imagen PIL.
        datos_imagen_filtrada = filtro_seleccionado.aplicar(mi_imagen.datos_imagen)

        # 4. Actualizar el atributo de datos en nuestro objeto Imagen.
        mi_imagen.datos_imagen = datos_imagen_filtrada

        # 5. Guardar la imagen modificada desde el objeto a un nuevo archivo.
        mi_imagen.guardar(ruta_salida)

        print(f"--- Proceso completado. Imagen guardada en {ruta_salida} ---")
        return "Proceso exitoso"

    except FileNotFoundError:
        print("El proceso ha fallado porque el archivo de entrada no fue encontrado.")
        raise
    except Exception as e:
        print(f"Ha ocurrido un error inesperado durante el procesamiento: {e}")
        raise