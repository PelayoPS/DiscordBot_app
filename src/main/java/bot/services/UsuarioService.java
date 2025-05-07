package bot.services;

import bot.models.Usuario;
import bot.models.Experiencia;
import bot.models.Penalizacion;
import java.util.Optional;

/**
 * Servicio para la gestión de usuarios.
 * Permite buscar usuarios, actualizar experiencia y agregar penalizaciones.
 * 
 * @author PelayoPS
 */
public interface UsuarioService extends Service<Usuario, Long> {
    /**
     * Busca un usuario por tipo de usuario.
     * 
     * @param tipoUsuario Tipo de usuario
     * @return Optional con el usuario encontrado o vacío si no existe
     */
    Optional<Usuario> findByTipoUsuario(String tipoUsuario);

    /**
     * Actualiza la experiencia de un usuario, subiendo de nivel si corresponde.
     * 
     * @param idUsuario ID del usuario
     * @param puntosXp  Puntos de experiencia a añadir
     */
    void actualizarExperiencia(Long idUsuario, int puntosXp);

    /**
     * Agrega una penalización a un usuario.
     * 
     * @param idUsuario  ID del usuario penalizado
     * @param idAdminMod ID del admin/mod que aplica la penalización
     * @param tipo       Tipo de penalización
     * @param razon      Razón de la penalización
     * @param duracion   Duración de la penalización
     */
    void agregarPenalizacion(Long idUsuario, Long idAdminMod, String tipo, String razon, java.time.Duration duracion);

    /**
     * Guarda o actualiza una entidad Experiencia.
     *
     * @param experiencia La entidad Experiencia a guardar.
     * @return La entidad Experiencia guardada.
     */
    Experiencia saveExperiencia(Experiencia experiencia);

    /**
     * Guarda o actualiza una entidad Penalizacion.
     *
     * @param penalizacion La entidad Penalizacion a guardar.
     * @return La entidad Penalizacion guardada.
     */
    Penalizacion savePenalizacion(Penalizacion penalizacion);
}