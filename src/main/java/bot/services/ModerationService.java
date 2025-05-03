package bot.services;

import bot.models.Penalizacion;
import java.time.Duration;
import java.util.List;

/**
 * Servicio para gestionar acciones de moderación y penalizaciones sobre
 * usuarios.
 * Permite banear, advertir, silenciar, expulsar, desbanear, aplicar timeout y
 * registrar purgas.
 * 
 * @author PelayoPS
 */
public interface ModerationService {
    /**
     * Obtiene el historial de penalizaciones de un usuario.
     * 
     * @param idUsuario ID del usuario
     * @return Lista de penalizaciones
     */
    List<Penalizacion> obtenerHistorialUsuario(Long idUsuario);

    /**
     * Banea a un usuario.
     * 
     * @param idUsuario  ID del usuario a banear
     * @param razon      Razón del baneo
     * @param idAdminMod ID del moderador que ejecuta el baneo
     * @return true si el registro fue exitoso, false en caso contrario
     */
    boolean banearUsuario(Long idUsuario, String razon, Long idAdminMod);

    /**
     * Advierte a un usuario.
     * 
     * @param idUsuario  ID del usuario advertido
     * @param razon      Razón de la advertencia
     * @param idAdminMod ID del moderador que advierte
     * @return true si el registro fue exitoso, false en caso contrario
     */
    boolean advertirUsuario(Long idUsuario, String razon, Long idAdminMod);

    /**
     * Silencia (mute) a un usuario.
     * 
     * @param idUsuario  ID del usuario silenciado
     * @param razon      Razón del silencio
     * @param duracion   Duración del silencio
     * @param idAdminMod ID del moderador que silencia
     * @return true si el registro fue exitoso, false en caso contrario
     */
    boolean silenciarUsuario(Long idUsuario, String razon, Duration duracion, Long idAdminMod);

    /**
     * Expulsa (kick) a un usuario.
     * 
     * @param idUsuario  ID del usuario expulsado
     * @param razon      Razón de la expulsión
     * @param idAdminMod ID del moderador que expulsa
     * @return true si el registro fue exitoso, false en caso contrario
     */
    boolean expulsarUsuario(Long idUsuario, String razon, Long idAdminMod);

    /**
     * Desbanea a un usuario.
     * 
     * @param idUsuario  ID del usuario desbaneado
     * @param idAdminMod ID del moderador que desbanea
     * @return true si el registro fue exitoso, false en caso contrario
     */
    boolean desbanearUsuario(Long idUsuario, Long idAdminMod);

    /**
     * Aplica timeout a un usuario.
     * 
     * @param idUsuario  ID del usuario sancionado
     * @param razon      Razón del timeout
     * @param duracion   Duración del timeout
     * @param idAdminMod ID del moderador que aplica el timeout
     * @return true si el registro fue exitoso, false en caso contrario
     */
    boolean timeoutUsuario(Long idUsuario, String razon, Duration duracion, Long idAdminMod);

    /**
     * Registra una purga de mensajes.
     * 
     * @param idAdminMod ID del moderador que purga
     * @param cantidad   Cantidad de mensajes eliminados
     * @param idCanal    ID del canal donde se realiza la purga
     */
    void registrarPurge(Long idAdminMod, int cantidad, Long idCanal);
}
