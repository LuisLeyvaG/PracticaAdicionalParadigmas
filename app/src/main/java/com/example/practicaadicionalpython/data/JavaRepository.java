package com.example.practicaadicionalpython.data;

import com.example.practicaadicionalpython.domain.Filtro;

import java.util.ArrayList;
import java.util.List;

public class JavaRepository {

    public static List<Filtro> getAvailableFilters() {
        ArrayList<Filtro> filters = new ArrayList<>();

        // Filtros de Tono y Color
        filters.add(new Filtro("Grises", "grayscale", "Convierte la imagen a escala de grises"));
        filters.add(new Filtro("Invertir", "invert", "Invierte los colores de la imagen"));
        filters.add(new Filtro("Sepia", "sepia", "Aplica un tono sepia a la imagen"));
        filters.add(new Filtro("Póster", "posterize", "Reduce el número de colores para un efecto de póster"));
        filters.add(new Filtro("Solarizar", "solarize", "Aplica un efecto de solarización a la imagen"));
        filters.add(new Filtro("Contraste", "autocontrast", "Ajusta el contraste de la imagen automáticamente"));

        // Filtros de Detalle
        filters.add(new Filtro("Desenfoque", "blur", "Aplica un desenfoque a la imagen"));
        filters.add(new Filtro("Enfocar", "sharpen", "Aumenta la nitidez de la imagen"));
        filters.add(new Filtro("Bordes", "find_edges", "Detecta los bordes en la imagen"));
        filters.add(new Filtro("Contorno", "contour", "Aplica un efecto de contorno a la imagen"));

        // Filtros Artísticos
        filters.add(new Filtro("Relieve", "emboss", "Aplica un efecto de relieve a la imagen"));
        filters.add(new Filtro("Espejo", "mirror", "Crea un efecto espejo en la imagen"));
        filters.add(new Filtro("Rotar 90°", "rotate90", "Rota la imagen 90 grados en sentido horario"));

        return filters;
    }

}
