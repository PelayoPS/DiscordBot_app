package backend.services.impl;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Optional;

import backend.models.Penalizacion;
import backend.models.Usuario;
import backend.repositories.PenalizacionRepository;
import backend.repositories.UsuarioRepository;
import backend.services.UsuarioService;

/**
 * Implementación del servicio de usuario.
 * Permite gestionar usuarios, experiencia y penalizaciones.
 * 
 * @author PelayoPS
 */
public class UsuarioServiceImpl extends AbstractService<Usuario, Long> implements UsuarioService {
    /**
     * Guarda o actualiza un usuario, asignando valores por defecto a los campos obligatorios si están vacíos o nulos.
     *
     * @param usuario Usuario a guardar
     * @return Usuario guardado
     */
    @Override
    public Usuario save(Usuario usuario) {
        // Asignar valores por defecto si están a null o inválidos
        if (usuario.getTipoUsuario() == null || usuario.getTipoUsuario().isBlank()) {
            usuario.setTipoUsuario("MIEMBRO");
        }
        if (usuario.getNivel() <= 0) {
            usuario.setNivel(1);
        }
        if (usuario.getPuntosXp() < 0) {
            usuario.setPuntosXp(0);
        }
        // timestamp_ultimo_mensaje opcional, no forzamos
        return usuarioRepository.save(usuario);
    }
    private final UsuarioRepository usuarioRepository;
    private final PenalizacionRepository penalizacionRepository;

    /**
     * Constructor de UsuarioServiceImpl.
     * 
     * @param usuarioRepository      Repositorio de usuarios
     * @param experienciaRepository  Repositorio de experiencia
     * @param penalizacionRepository Repositorio de penalizaciones
     */
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
            PenalizacionRepository penalizacionRepository) {
        super(usuarioRepository);
        this.usuarioRepository = usuarioRepository;
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
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        Usuario usuario;
        if (usuarioOpt.isPresent()) {
            usuario = usuarioOpt.get();
        } else {
            usuario = new Usuario(idUsuario, "MIEMBRO");
        }
        usuario.setPuntosXp(usuario.getPuntosXp() + puntosXp);
        // Verificar si debe subir de nivel (cada 100 puntos)
        if (usuario.getPuntosXp() >= (usuario.getNivel() + 1) * 100) {
            usuario.setNivel(usuario.getNivel() + 1);
        }
        usuario.setTimestampUltimoMensaje(System.currentTimeMillis());
        usuarioRepository.save(usuario);
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

    // Método saveExperiencia eliminado, ahora la experiencia se gestiona en Usuario

    /**
     * Guarda o actualiza una entidad Penalizacion.
     *
     * @param penalizacion La entidad Penalizacion a guardar.
     * @return La entidad Penalizacion guardada.
     */
    @Override
    public Penalizacion savePenalizacion(Penalizacion penalizacion) {
        return penalizacionRepository.save(penalizacion);
    }
}