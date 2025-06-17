package com.example.practicaadicionalpython.domain;

public class Filtro {
    // Atributos privados
    private String displayName; // El nombre que verá el usuario en el botón
    private String pythonFunctionName; // El nombre técnico para pasar a Chaquopy

    private String description; // Descripción opcional del filtro

    // Constructor público
    public Filtro(String displayName, String pythonFunctionName, String description) {
        this.displayName = displayName;
        this.pythonFunctionName = pythonFunctionName;
        this.description = description;

    }

    // Getters públicos para acceder a los atributos
    public String getDisplayName() {
        return displayName;
    }

    public String getPythonFunctionName() {
        return pythonFunctionName;
    }

    public String getDescription() {
        return description;
    }
}