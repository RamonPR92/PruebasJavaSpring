package com.ramon.app.mockito.services;

import com.ramon.app.mockito.models.Examen;

import java.util.Optional;

public interface ExamenService {

    Optional<Examen> findExamenPorNombre(String nombre);

    Examen findExamenPorNombreConPreguntas(String nombre);

    Examen guardarExamen(Examen examen);
}
