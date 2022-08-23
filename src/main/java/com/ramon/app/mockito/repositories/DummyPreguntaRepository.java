package com.ramon.app.mockito.repositories;

import com.ramon.app.mockito.models.Examen;

import java.util.Arrays;
import java.util.List;

public class DummyPreguntaRepository implements PreguntaRepository{

    @Override
    public List<String> findPreguntasPorExamenId(Long id) {
        System.out.println("findPreguntasPorExamenId - REAL");
        return Arrays.asList(
                "aritmetica",
                "integrales",
                "trigonometria"
        );

    }

    @Override
    public void savePreguntas(List<String> preguntas) {
        System.out.println("savePreguntas - REAL");
    }
}
