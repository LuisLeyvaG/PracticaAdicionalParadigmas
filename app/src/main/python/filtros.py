# domain/filtros.py

from abc import ABC, abstractmethod
from PIL import Image, ImageOps

# Creación de una clase abstracta Filtro con un método aplicar
class Filtro(ABC):
    """
    Clase abstracta que define la interfaz para cualquier filtro de imagen.
    Cada filtro debe implementar el método aplicar.
    """
    @abstractmethod
    def aplicar(self, imagen: Image.Image) -> Image.Image:
        """
        Aplica un filtro a una imagen y devuelve la imagen modificada.

        Args:
            imagen: Un objeto de imagen de la biblioteca Pillow.

        Returns:
            Un objeto de imagen de Pillow con el filtro aplicado.
        """
        pass

# Implementación de dos filtros como subclases de Filtro
class FiltroGrises(Filtro):
    """
    Implementación de un filtro que convierte la imagen a escala de grises.
    """
    def aplicar(self, imagen: Image.Image) -> Image.Image:
        print("Aplicando filtro de escala de grises...")
        # El modo 'L' convierte la imagen a 8-bit pixels, blanco y negro.
        return imagen.convert('L')

class FiltroInversion(Filtro):
    """
    Implementación de un filtro que invierte los colores de la imagen.
    """
    def aplicar(self, imagen: Image.Image) -> Image.Image:
        print("Aplicando filtro de inversión de color...")
        # Se asegura de que la imagen sea RGB antes de invertirla para evitar errores
        if imagen.mode == 'RGBA':
            r, g, b, a = imagen.split()
            rgb_image = Image.merge('RGB', (r, g, b))
            inverted_image = ImageOps.invert(rgb_image)
            r_inv, g_inv, b_inv = inverted_image.split()
            return Image.merge('RGBA', (r_inv, g_inv, b_inv, a))
        else:
            return ImageOps.invert(imagen.convert('RGB'))