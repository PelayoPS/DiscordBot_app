package bot.db.services;

import bot.db.models.Experiencia;
import java.util.List;
import java.util.Optional;

public interface ExperienciaService extends Service<Experiencia, Long> {
    Optional<Experiencia> findByIdUsuario(Long idUsuario);

    List<Experiencia> findByNivelGreaterThan(int nivel);

    void aumentarExperiencia(Long idUsuario, int puntos);

    void subirNivel(Long idUsuario);
}