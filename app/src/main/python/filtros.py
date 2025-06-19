# domain/filtros.py

from abc import ABC, abstractmethod
from PIL import Image, ImageOps, ImageFilter, ImageEnhance

# --------------------------------------------------------------------------
# CLASE BASE ABSTRACTA (Sin cambios)
# --------------------------------------------------------------------------
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

# --------------------------------------------------------------------------
# FILTROS DE TONO Y COLOR
# --------------------------------------------------------------------------

class FiltroGrises(Filtro):
    """
    Implementación de un filtro que convierte la imagen a escala de grises.
    """
    def aplicar(self, imagen: Image.Image) -> Image.Image:
        print("Python: Aplicando filtro de escala de grises...")
        # El modo 'L' convierte la imagen a 8-bit pixels, blanco y negro.
        return imagen.convert('L')

class FiltroInversion(Filtro):
    """
    Implementación de un filtro que invierte los colores de la imagen.
    """
    def aplicar(self, imagen: Image.Image) -> Image.Image:
        print("Python: Aplicando filtro de inversión de color...")
        # Se asegura de que la imagen sea RGB antes de invertirla para evitar errores con el canal alfa.
        if imagen.mode == 'RGBA':
            r, g, b, a = imagen.split()
            rgb_image = Image.merge('RGB', (r, g, b))
            inverted_image = ImageOps.invert(rgb_image)
            r_inv, g_inv, b_inv = inverted_image.split()
            return Image.merge('RGBA', (r_inv, g_inv, b_inv, a))
        else:
            return ImageOps.invert(imagen.convert('RGB'))

class FiltroSepia(Filtro):
    """
    Aplica un tono sepia a la imagen para un look antiguo.
    """
    def aplicar(self, imagen: Image.Image) -> Image.Image:
        print("Python: Aplicando filtro Sepia...")
        img = imagen.convert('RGB')
        # La "receta" clásica para el tono sepia
        sepia_pixels = img.copy()
        for x in range(sepia_pixels.width):
            for y in range(sepia_pixels.height):
                r, g, b = img.getpixel((x, y))
                tr = int(0.393 * r + 0.769 * g + 0.189 * b)
                tg = int(0.349 * r + 0.686 * g + 0.168 * b)
                tb = int(0.272 * r + 0.534 * g + 0.131 * b)
                sepia_pixels.putpixel((x, y), (min(255, tr), min(255, tg), min(255, tb)))
        return sepia_pixels

class FiltroPosterizar(Filtro):
    """
    Reduce el número de colores por canal, dando un efecto de póster.
    """
    def aplicar(self, imagen: Image.Image) -> Image.Image:
        print("Python: Aplicando filtro de Posterizar...")
        # Un valor entre 1 y 8. 4 es un buen punto de partida.
        return ImageOps.posterize(imagen.convert('RGB'), 4)

class FiltroSolarizar(Filtro):
    """
    Invierte los valores de los píxeles por encima de un umbral.
    """
    def aplicar(self, imagen: Image.Image) -> Image.Image:
        print("Python: Aplicando filtro de Solarizar...")
        # El umbral puede ser de 0 a 255. 128 es el punto medio.
        return ImageOps.solarize(imagen.convert('RGB'), threshold=128)

class FiltroContraste(Filtro):
    """
    Ajusta automáticamente el contraste de la imagen.
    """
    def aplicar(self, imagen: Image.Image) -> Image.Image:
        print("Python: Aplicando filtro de Contraste Automático...")
        return ImageOps.autocontrast(imagen.convert('RGB'))

# --------------------------------------------------------------------------
# FILTROS DE DETALLE Y ENFOQUE
# --------------------------------------------------------------------------

class FiltroDesenfoque(Filtro):
    """
    Aplica un desenfoque gaussiano suave a la imagen.
    """
    def aplicar(self, imagen: Image.Image) -> Image.Image:
        print("Python: Aplicando filtro de Desenfoque...")
        return imagen.filter(ImageFilter.GaussianBlur(radius=5))

class FiltroEnfocar(Filtro):
    """
    Aplica un filtro de enfoque para resaltar los detalles.
    """
    def aplicar(self, imagen: Image.Image) -> Image.Image:
        print("Python: Aplicando filtro de Enfoque...")
        return imagen.filter(ImageFilter.SHARPEN)

class FiltroDetectarBordes(Filtro):
    """
    Detecta y resalta los bordes en la imagen.
    """
    def aplicar(self, imagen: Image.Image) -> Image.Image:
        print("Python: Aplicando filtro de Detección de Bordes...")
        return imagen.filter(ImageFilter.FIND_EDGES)

class FiltroContorno(Filtro):
    """
    Crea un efecto de contorno, similar a un dibujo a lápiz.
    """
    def aplicar(self, imagen: Image.Image) -> Image.Image:
        print("Python: Aplicando filtro de Contorno...")
        return imagen.filter(ImageFilter.CONTOUR)

# --------------------------------------------------------------------------
# FILTROS ARTÍSTICOS Y GEOMÉTRICOS
# --------------------------------------------------------------------------

class FiltroRelieve(Filtro):
    """
    Aplica un efecto de relieve o grabado a la imagen.
    """
    def aplicar(self, imagen: Image.Image) -> Image.Image:
        print("Python: Aplicando filtro de Relieve...")
        return imagen.filter(ImageFilter.EMBOSS)

class FiltroEspejo(Filtro):
    """
    Invierte la imagen horizontalmente (efecto espejo).
    """
    def aplicar(self, imagen: Image.Image) -> Image.Image:
        print("Python: Aplicando filtro de Espejo...")
        return ImageOps.mirror(imagen)

class FiltroRotar90(Filtro):
    """
    Rota la imagen 90 grados en sentido horario.
    """
    def aplicar(self, imagen: Image.Image) -> Image.Image:
        print("Python: Aplicando filtro de Rotar 90°...")
        # expand=True evita que la imagen se corte al rotar
        return imagen.rotate(-90, expand=True)