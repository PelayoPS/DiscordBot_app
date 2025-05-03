package bot.services.impl;

import bot.models.Usuario;
import bot.models.Experiencia;
import bot.models.Penalizacion;
import bot.repositories.UsuarioRepository;
import bot.repositories.ExperienciaRepository;
import bot.repositories.PenalizacionRepository;
import bot.services.UsuarioService;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Optional;

/**
 * Implementación del servicio de usuario.
 * Permite gestionar usuarios, experiencia y penalizaciones.
 * 
 * @author PelayoPS
 */
public class UsuarioServiceImpl extends AbstractService<Usuario, Long> implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final ExperienciaRepository experienciaRepository;
    private final PenalizacionRepository penalizacionRepository;

    /**
     * Constructor de UsuarioServiceImpl.
     * 
     * @param usuarioRepository      Repositorio de usuarios
     * @param experienciaRepository  Repositorio de experiencia
     * @param penalizacionRepository Repositorio de penalizaciones
     */
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
            ExperienciaRepository experienciaRepository,
            PenalizacionRepository penalizacionRepository) {
        super(usuarioRepository);
        this.usuarioRepository = usuarioRepository;
        this.experienciaRepository = experienciaRepository;
        this.penalizacionRepository = penalizacionRepository;
    }

    /**
     * Busca un usuario por tipo de usuario.
     * 
     * @param tipoUsuario Tipo de usuario
     * @return Optional con el usuario encontrado o vacío si no existe
     */
    @Override
    public Optional<Usuario> findByTipoUsuario(String tipoUsuario) {
        return usuarioRepository.findByTipoUsuario(tipoUsuario);
    }

    /**
     * Actualiza la experiencia de un usuario, subiendo de nivel si corresponde.
     * 
     * @param idUsuario ID del usuario
     * @param puntosXp  Puntos de experiencia a añadir
     */
    @Override
    public void actualizarExperiencia(Long idUsuario, int puntosXp) {
        Optional<Experiencia> experienciaOpt = experienciaRepository.findByIdUsuario(idUsuario);
        if (experienciaOpt.isPresent()) {
            Experiencia experiencia = experienciaOpt.get();
            experiencia.setPuntosXp(experiencia.getPuntosXp() + puntosXp);

            // Verificar si debe subir de nivel (cada 100 puntos)
            if (experiencia.getPuntosXp() >= (experiencia.getNivel() + 1) * 100) {
                experiencia.setNivel(experiencia.getNivel() + 1);
            }

            experienciaRepository.save(experiencia);
        } else {
            // Crear nueva experiencia si no existe
            Experiencia nuevaExperiencia = new Experiencia(null, idUsuario, 1, puntosXp);
            experienciaRepository.save(nuevaExperiencia);
        }
    }

    /**
     * Agrega una penalización a un usuario.
     * 
     * @param idUsuario  ID del usuario penalizado
     * @param idAdminMod ID del admin/mod que aplica la penalización
     * @param tipo       Tipo de penalización
     * @param razon      Razón de la penalización
     * @param duracion   Duración de la penalización
     */
    @Override
    public void agregarPenalizacion(Long idUsuario, Long idAdminMod, String tipo, String razon, Duration duracion) {
        Penalizacion penalizacion = new Penalizacion(
                null,
                idUsuario,
                idAdminMod,
                tipo,
                LocalDateTime.now(),
                razon,
                duracion);
        penalizacionRepository.save(penalizacion);
    }
}