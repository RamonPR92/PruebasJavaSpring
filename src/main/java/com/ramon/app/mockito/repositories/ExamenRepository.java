package com.ramon.app.mockito.repositories;

import com.ramon.app.mockito.models.Examen;

import java.util.List;

public interface ExamenRepository {
    List<Examen> findAll();

    Examen save(Examen examenAGuardar);
}
