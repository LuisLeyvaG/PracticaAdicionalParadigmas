# data/imagen.py

from PIL import Image

class Imagen:
    """
    Clase que representa una imagen, con atributos y métodos
    para cargarla desde un archivo y guardarla en otro.
    """
    def __init__(self, ruta: str):
        """
        Constructor de la clase Imagen.

        Args:
            ruta (str): La ruta del archivo de la imagen original.
        """
        self.ruta_origen = ruta
        self.datos_imagen = None  # Atributo para almacenar los datos de la imagen (objeto PIL)

    def cargar(self):
        """
        Método para cargar los datos de la imagen desde la ruta de origen.
        Los datos se almacenan en el atributo 'self.datos_imagen'.
        """
        print(f"Cargando imagen desde: {self.ruta_origen}")
        try:
            self.datos_imagen = Image.open(self.ruta_origen)
            print("Imagen cargada exitosamente.")
        except FileNotFoundError:
            print(f"Error: No se encontró el archivo en {self.ruta_origen}")
            # relanzamos la excepción para que sea manejada por el código que llama
            raise
        except Exception as e:
            print(f"Ocurrió un error al cargar la imagen: {e}")
            raise

    def guardar(self, ruta_salida: str):
        """
        Método para guardar los datos de la imagen actualmente en memoria
        en un nuevo archivo.

        Args:
            ruta_salida (str): La ruta completa donde se guardará la nueva imagen.
        """
        if self.datos_imagen:
            print(f"Guardando imagen en: {ruta_salida}")
            try:
                self.datos_imagen.save(ruta_salida)
                print("Imagen guardada exitosamente.")
            except Exception as e:
                print(f"Ocurrió un error al guardar la imagen: {e}")
                raise
        else:
            print("Error: No hay datos de imagen para guardar. ¿Se cargó la imagen primero?")