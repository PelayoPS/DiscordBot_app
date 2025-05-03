package bot.services.impl;

import bot.models.Experiencia;
import bot.repositories.ExperienciaRepository;
import bot.services.ExperienciaService;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de experiencia de usuario.
 * Permite gestionar la experiencia, niveles y puntos de los usuarios.
 * 
 * @author PelayoPS
 */
public class ExperienciaServiceImpl extends AbstractService<Experiencia, Long> implements ExperienciaService {
    private final ExperienciaRepository experienciaRepository;

    /**
     * Constructor de ExperienciaServiceImpl.
     * 
     * @param experienciaRepository Repositorio de experiencia
     */
    public ExperienciaServiceImpl(ExperienciaRepository experienciaRepository) {
        super(experienciaRepository);
        this.experienciaRepository = experienciaRepository;
    }

    /**
     * Busca la experiencia por el ID de usuario.
     * 
     * @param idUsuario ID del usuario
     * @return Optional con la experiencia encontrada o vacío si no existe
     */
    @Override
    public Optional<Experiencia> findByIdUsuario(Long idUsuario) {
        return experienciaRepository.findByIdUsuario(idUsuario);
    }

    /**
     * Busca todas las experiencias con nivel mayor que el indicado.
     * 
     * @param nivel Nivel mínimo (exclusivo)
     * @return Lista de experiencias encontradas
     */
    @Override
    public List<Experiencia> findByNivelGreaterThan(int nivel) {
        return experienciaRepository.findByNivelGreaterThan(nivel);
    }

    /**
     * Aumenta los puntos de experiencia de un usuario y sube de nivel si
     * corresponde.
     * 
     * @param idUsuario ID del usuario
     * @param puntos    Puntos a añadir
     */
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

    /**
     * Sube de nivel a un usuario.
     * 
     * @param idUsuario ID del usuario
     */
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