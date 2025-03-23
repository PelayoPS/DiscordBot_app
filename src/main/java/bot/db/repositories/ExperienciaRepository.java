package bot.db.repositories;

import bot.db.models.Experiencia;
import java.util.Optional;
import java.util.List;

public interface ExperienciaRepository extends Repository<Experiencia, Long> {
    Optional<Experiencia> findByIdUsuario(Long idUsuario);

    List<Experiencia> findByNivelGreaterThan(int nivel);
}