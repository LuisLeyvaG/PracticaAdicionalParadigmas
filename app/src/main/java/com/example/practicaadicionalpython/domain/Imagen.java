package com.example.practicaadicionalpython.domain;

import android.net.Uri;

/**
 * Representa una imagen dentro de la aplicación.
 * Esta clase es inmutable, lo que significa que una vez creada, sus atributos no pueden cambiar.
 * Esto la hace segura para usar en un entorno de UI reactiva.
 */
public class Imagen {

    // --- Atributos ---

    /**
     * La Uri que puede ser usada por componentes de Android (como Coil para mostrar la imagen).
     * Puede ser una 'content://' (de la galería o FileProvider) o 'file://'.
     */
    private final Uri uri;

    /**
     * La ruta absoluta del archivo en el sistema de ficheros del dispositivo.
     * Esencial para pasarla a Chaquopy/Python.
     */
    private final String localPath;

    /**
     * El nombre del filtro que se aplicó para generar esta imagen.
     * Será null si es la imagen original.
     */
    private final Filtro appliedFilter;

    /**
     * La fecha y hora (en milisegundos) en que se creó este objeto de imagen.
     * Útil para generar nombres únicos o para ordenar.
     */
    private final long creationTimestamp;

    // --- Constructor ---

    public Imagen(Uri uri, String localPath, Filtro appliedFilter) {
        this.uri = uri;
        this.localPath = localPath;
        this.appliedFilter = appliedFilter;
        this.creationTimestamp = System.currentTimeMillis();
    }

    // --- Métodos (Getters) ---

    public Uri getUri() {
        return uri;
    }

    public String getLocalPath() {
        return localPath;
    }

    public Filtro getAppliedFilter() {
        return appliedFilter;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    // --- Métodos adicionales ---

    /**
     * Devuelve 'true' si es la imagen original seleccionada de la galería.
     *
     * @return boolean
     */
    public boolean isOriginal() {
        return appliedFilter == null;
    }

    /**
     * Devuelve un nombre amigable para mostrar en la UI.
     * Por ejemplo: "Original" o "Desenfoque".
     *
     * @return El nombre a mostrar.
     */
    public String getDisplayName() {
        if (isOriginal()) {
            return "Original";
        } else {
            // Capitalizamos la primera letra para que se vea mejor
            return "Imagen editada";
        }
    }
}