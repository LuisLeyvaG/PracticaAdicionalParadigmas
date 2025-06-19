# Reporte Final: Práctica Adicional de Programación de Paradigmas

## Descripción General del Proyecto

Este proyecto implementa un sistema básico de edición de imágenes en una aplicación de Android, adhiriéndose a los principios de la Programación Orientada a Objetos (POO). El sistema permite a los usuarios seleccionar una imagen de su galería, aplicar una variedad de filtros y ver los resultados en tiempo real. La arquitectura del proyecto es un ejemplo de la programación multi-paradigma, donde una interfaz de usuario reactiva en Kotlin/Jetpack Compose interactúa con una lógica de procesamiento de imágenes implementada en Python, todo ello dentro del entorno de Android.

El objetivo principal de la práctica es diseñar e implementar un sistema de edición de imágenes utilizando principios de POO. Los lenguajes especificados son Python, Java y C/C++. Este proyecto cumple con los requisitos al ofrecer una implementación en Python y Java.

## Arquitectura y Diseño

El proyecto está estructurado en dos componentes principales:

1.  **El *Frontend* (Interfaz de Usuario):** Una aplicación de Android nativa escrita en Kotlin usando Jetpack Compose. Esta capa es responsable de toda la interacción con el usuario, como la selección de imágenes, la visualización de estas y la presentación de los filtros disponibles.
2.  **El *Backend* (Lógica de Procesamiento):** Un conjunto de scripts de Python que se ejecutan en el dispositivo Android a través de la librería Chaquopy. Esta capa maneja toda la manipulación de imágenes, incluyendo la carga, la aplicación de filtros y el guardado de estas.

### Diseño Orientado a Objetos

El diseño del software sigue las directrices de la POO, tal como se especifica en los requisitos de la práctica[cite: 13, 14].

#### Capa de Python (Backend)

En el núcleo del backend de Python, encontramos dos clases principales que modelan el dominio del problema: `Imagen` y `Filtro`.

  * **Clase `Imagen`**: Esta clase, definida en [`imagenes.py`](https://www.github.com/luisleyvag/practicaadicionalparadigmas/blob/main/app/src/main/python/imagenes.py), representa una imagen con atributos para su ruta de origen y sus datos. Proporciona métodos para `cargar()` la imagen desde un archivo y `guardar()` la imagen modificada en una nueva ubicación.
  * **Clase Abstracta `Filtro`**: [`filtros.py`](https://www.github.com/luisleyvag/practicaadicionalparadigmas/blob/main/app/src/main/python/filtros.py) define una clase base abstracta `Filtro` con un método abstracto `aplicar(imagen)`. Este diseño obliga a que cualquier filtro concreto implemente su propia lógica de aplicación, promoviendo la extensibilidad y el polimorfismo.
  * **Subclases de `Filtro`**: Se han implementado múltiples filtros como subclases de `Filtro`, incluyendo `FiltroGrises` y `FiltroInversion`, además de otros como Desenfoque, Relieve y Rotación, superando así los requisitos de la práctica.

#### Capa de Java/Kotlin (Frontend)

La capa del frontend también emplea principios de POO para gestionar el estado de la interfaz de usuario y la interacción con el backend de Python.

  * **Clase `Imagen` (Java)**: En el archivo [`domain/Imagen.java`](https://www.github.com/luisleyvag/practicaadicionalparadigmas/blob/main/app/src/main/java/com/example/practicaadicionalpython/domain/Imagen.java), se define una clase `Imagen` que representa una imagen dentro de la aplicación. Esta clase es inmutable y contiene la `Uri` para la visualización, la ruta local para Python, el filtro aplicado y una marca de tiempo.
  * **Clase `Filtro` (Java)**: La clase [`domain/Filtro.java`](https://www.github.com/luisleyvag/practicaadicionalparadigmas/blob/main/app/src/main/java/com/example/practicaadicionalpython/domain/Filtro.java) modela un filtro en la capa de la aplicación, con atributos para el nombre que se muestra al usuario, el nombre de la función en Python y una descripción.
  * **Repositorios**: El [`JavaRepository.java`](https://www.github.com/luisleyvag/practicaadicionalparadigmas/blob/main/app/src/main/java/com/example/practicaadicionalpython/data/JavaRepository.java) se encarga de proporcionar la lista de filtros disponibles, mientras que el [`PythonRepository.kt`](https://www.github.com/luisleyvag/practicaadicionalparadigmas/blob/main/app/src/main/java/com/example/practicaadicionalpython/data/PythonRepository.kt) actúa como un intermediario que invoca los scripts de Python de forma asíncrona mediante coroutines.

## Funcionalidad Implementada

El proyecto cumple con todos los requisitos básicos y algunos de los opcionales de la práctica.

  * **Carga y Guardado de Imágenes**: La aplicación permite a los usuarios cargar una imagen desde la galería de su dispositivo y, después de aplicar los filtros, las imágenes modificadas se guardan en el almacenamiento en caché de la aplicación.
  * **Aplicación de Filtros**: Los usuarios pueden aplicar una amplia variedad de filtros. La implementación inicial se realizó en Python y se integró con éxito en la aplicación de Android.
  * **Interfaz Gráfica e Interactiva**: Se ha diseñado una interfaz de usuario con Jetpack Compose que permite una experiencia de usuario fluida e interactiva.
  * **Múltiples Filtros en Cadena**: La aplicación permite la aplicación de múltiples filtros de forma secuencial. Un historial de imágenes (`imageHistory`) realiza un seguimiento de cada filtro aplicado, y una función de "deshacer" permite a los usuarios revertir al estado anterior.
  * **Filtros Adicionales**: Además de los filtros de escala de grises e inversión, se han añadido muchos otros, como sepia, posterizar, solarizar, desenfoque, enfocar, detectar bordes, contorno, relieve, espejo y rotación.

## Integración de Python en Android con Chaquopy

Una de las características más destacadas de este proyecto es el uso de **Chaquopy** para integrar y ejecutar código de Python dentro de una aplicación de Android.

  * **Configuración**: El plugin de Chaquopy se configura en los archivos [`build.gradle.kts`](https://www.github.com/luisleyvag/practicaadicionalparadigmas/blob/main/app/build.gradle.kts) del proyecto. En esta configuración, se especifica la versión de Python a utilizar y se declaran las dependencias de Python, como `Pillow` y `numpy`.
  * **Invocación**: El `PythonRepository.kt` demuestra cómo se puede obtener una instancia de un módulo de Python ([`main.py`](https://www.github.com/luisleyvag/practicaadicionalparadigmas/blob/main/app/src/main/python/main.py)) y llamar a sus funciones (`aplicar_filtro_a_imagen`), pasando datos primitivos como las rutas de los archivos y los nombres de los filtros.
  * **Manejo de Hilos**: Todas las llamadas a Python se realizan desde un hilo de fondo utilizando las coroutines de Kotlin (`viewModelScope.launch` y `withContext(Dispatchers.IO)`), lo que garantiza que la interfaz de usuario no se bloquee durante el procesamiento de las imágenes.

## Cómo Ejecutar el Proyecto

1.  **Requisitos Previos**:

      * Android Studio con el SDK de Android.
      * Un dispositivo o emulador de Android con una arquitectura compatible (se han especificado `arm64-v8a` y `x86_64`).
      * Una instalación de Python en el equipo de desarrollo.

2.  **Configuración**:

      * Clonar el repositorio.
      * Abrir el proyecto en Android Studio.
      * Asegurarse de que la ruta al ejecutable de Python esté correctamente configurada en el archivo `app/build.gradle.kts`.
      * Sincronizar el proyecto con los archivos de Gradle.

3.  **Ejecución**:

      * Seleccionar un dispositivo o emulador de Android.
      * Ejecutar la aplicación desde Android Studio.
      * Utilizar el botón "Seleccionar Imagen de la Galería" para cargar una imagen.
      * Pulsar el botón "Aplicar Filtro" para abrir el diálogo de selección de filtros.
      * Seleccionar un filtro para aplicarlo a la imagen.
      * Utilizar el botón de deshacer para revertir el último filtro aplicado.

## Conclusión

Este proyecto demuestra con éxito la implementación de un sistema de edición de imágenes aplicando los principios de la Programación Orientada a Objetos en un entorno multiplataforma. La separación de la lógica de procesamiento de imágenes en Python y la interfaz de usuario en Kotlin/Java permite un desarrollo modular y limpio. El uso de Chaquopy para unir estos dos mundos abre un abanico de posibilidades para los desarrolladores de Android que deseen aprovechar el vasto ecosistema de librerías de Python. El proyecto cumple con todos los requisitos fundamentales de la práctica y explora varias de las ampliaciones opcionales, lo que resulta en una aplicación funcional y bien diseñada.