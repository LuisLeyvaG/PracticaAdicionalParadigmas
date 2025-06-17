package com.example.practicaadicionalpython.data;

import com.example.practicaadicionalpython.domain.Filtro;

import java.util.ArrayList;
import java.util.List;

public class JavaRepository {

    public static List<Filtro> getAvailableFilters() {

        ArrayList<Filtro> filters = new ArrayList<>();

        // Aquí instanciamos nuestros objetos Filter
        filters.add(new Filtro("Blanco y Negro", "grises", "Convierte la imagen a escala de grises."));
        filters.add(new Filtro("Inversion", "inversion", "Invierte los colores de la imagen."));

        return filters;

    }

}
