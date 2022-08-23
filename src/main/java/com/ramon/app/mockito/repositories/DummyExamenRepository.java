package com.ramon.app.mockito.repositories;

import com.ramon.app.mockito.models.Examen;
import net.bytebuddy.asm.Advice;

import java.util.Arrays;
import java.util.List;

public class DummyExamenRepository implements ExamenRepository{

    @Override
    public List<Examen> findAll() {
        return Arrays.asList(
                new Examen(5L, "Matematicas"),
                new Examen(4L, "Programacion"),
                new Examen(56L, "Historia"),
                new Examen(578L, "Redes"));
    }

    @Override
    public Examen save(Examen examenAGuardar) {
        return examenAGuardar;
    }
}
