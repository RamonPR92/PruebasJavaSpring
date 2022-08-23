package com.ramon.app.mockito.repositories;

import java.util.List;

public interface PreguntaRepository {

    List<String> findPreguntasPorExamenId(Long id);

    void savePreguntas(List<String> preguntas);
}
