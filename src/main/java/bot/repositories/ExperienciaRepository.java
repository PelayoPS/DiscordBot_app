package bot.repositories;

import bot.models.Experiencia;
import java.util.Optional;
import java.util.List;

/**
 * Repositorio para operaciones sobre la experiencia de usuario.
 * Permite buscar experiencias por usuario y por nivel.
 * 
 * @author PelayoPS
 */
public interface ExperienciaRepository extends Repository<Experiencia, Long> {
    /**
     * Busca la experiencia por el ID de usuario.
     * 
     * @param idUsuario ID del usuario
     * @return Optional con la experiencia encontrada o vacío si no existe
     */
    Optional<Experiencia> findByIdUsuario(Long idUsuario);

    /**
     * Busca todas las experiencias con nivel mayor que el indicado.
     * 
     * @param nivel Nivel mínimo (exclusivo)
     * @return Lista de experiencias encontradas
     */
    List<Experiencia> findByNivelGreaterThan(int nivel);
}