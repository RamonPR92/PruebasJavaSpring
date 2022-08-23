package com.ramon.app.mockito.services;

import com.ramon.app.mockito.models.Examen;
import com.ramon.app.mockito.repositories.ExamenRepository;
import com.ramon.app.mockito.repositories.PreguntaRepository;

import java.util.List;
import java.util.Optional;

public class EscuelaExamenService implements ExamenService {

    private ExamenRepository examenRepository;
    private PreguntaRepository preguntaRepository;

    public EscuelaExamenService(ExamenRepository examenRepository, PreguntaRepository preguntaRepository) {
        this.examenRepository = examenRepository;
        this.preguntaRepository = preguntaRepository;
    }

    @Override
    public Optional<Examen> findExamenPorNombre(String nombre) {
        return examenRepository.findAll()
                .stream()
                .filter(nombreExamen -> nombreExamen.getNombre().contentEquals(nombre))
                .findFirst();
    }

    @Override
    public Examen findExamenPorNombreConPreguntas(String nombre) {
        Optional<Examen> examenOptional = findExamenPorNombre(nombre);
        Examen examen = null;
        if (examenOptional.isPresent()){
            examen = examenOptional.orElseThrow();
            List<String> preguntas = preguntaRepository.findPreguntasPorExamenId(examen.getId());
            examen.setPreguntas(preguntas);
        }
        return examen;
    }

    @Override
    public Examen guardarExamen(Examen examen) {
        if(!examen.getPreguntas().isEmpty()){
            preguntaRepository.savePreguntas(examen.getPreguntas());
        }
        return examenRepository.save(examen);
    }
}
