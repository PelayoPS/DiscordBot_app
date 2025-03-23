package bot.db.services.impl;

import bot.db.models.Experiencia;
import bot.db.repositories.ExperienciaRepository;
import bot.db.services.ExperienciaService;
import java.util.List;
import java.util.Optional;

public class ExperienciaServiceImpl extends AbstractService<Experiencia, Long> implements ExperienciaService {
    private final ExperienciaRepository experienciaRepository;

    public ExperienciaServiceImpl(ExperienciaRepository experienciaRepository) {
        super(experienciaRepository);
        this.experienciaRepository = experienciaRepository;
    }

    @Override
    public Optional<Experiencia> findByIdUsuario(Long idUsuario) {
        return experienciaRepository.findByIdUsuario(idUsuario);
    }

    @Override
    public List<Experiencia> findByNivelGreaterThan(int nivel) {
        return experienciaRepository.findByNivelGreaterThan(nivel);
    }

    @Override
    public void aumentarExperiencia(Long idUsuario, int puntos) {
        Optional<Experiencia> experienciaOpt = findByIdUsuario(idUsuario);
        if (experienciaOpt.isPresent()) {
            Experiencia experiencia = experienciaOpt.get();
            experiencia.setPuntosXp(experiencia.getPuntosXp() + puntos);

            // Verificar si debe subir de nivel
            if (experiencia.getPuntosXp() >= (experiencia.getNivel() + 1) * 100) {
                subirNivel(idUsuario);
            }

            experienciaRepository.save(experiencia);
        } else {
            // Crear nueva experiencia
            Experiencia nuevaExperiencia = new Experiencia(null, idUsuario, 1, puntos);
            experienciaRepository.save(nuevaExperiencia);
        }
    }

    @Override
    public void subirNivel(Long idUsuario) {
        Optional<Experiencia> experienciaOpt = findByIdUsuario(idUsuario);
        if (experienciaOpt.isPresent()) {
            Experiencia experiencia = experienciaOpt.get();
            experiencia.setNivel(experiencia.getNivel() + 1);
            experienciaRepository.save(experiencia);
        }
    }
}