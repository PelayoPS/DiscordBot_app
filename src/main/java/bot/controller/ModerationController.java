package bot.controller;

import bot.models.Penalizacion;
import bot.services.PenalizacionService;
import java.util.List;

/**
 * Controlador de moderación que orquesta la lógica de penalizaciones.
 * Recibe peticiones de adaptadores/comandos y delega en los servicios.
 * 
 * @author PelayoPS
 */
public class ModerationController {
    private final PenalizacionService penalizacionService;

    /**
     * Constructor de la clase ModerationController.
     *
     * @param penalizacionService Servicio de penalizaciones
     */
    public ModerationController(PenalizacionService penalizacionService) {
        this.penalizacionService = penalizacionService;
    }

    /**
     * Obtiene el historial de penalizaciones de un usuario.
     *
     * @param idUsuario ID del usuario
     * @return Lista de penalizaciones
     */
    public List<Penalizacion> obtenerHistorialUsuario(Long idUsuario) {
        return penalizacionService.findByIdUsuario(idUsuario);
    }

    /**
     * Registra y ejecuta el baneo de un usuario.
     *
     * @param idUsuario  ID del usuario a banear
     * @param razon      Razón del baneo
     * @param idAdminMod ID del moderador que ejecuta el baneo
     * @return true si el registro fue exitoso, false en caso contrario
     */
    public boolean banearUsuario(Long idUsuario, String razon, Long idAdminMod) {
        try {
            penalizacionService.registrarBaneo(idUsuario, razon, idAdminMod);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Registra una advertencia para un usuario.
     *
     * @param idUsuario  ID del usuario advertido
     * @param razon      Razón de la advertencia
     * @param idAdminMod ID del moderador que advierte
     * @return true si el registro fue exitoso, false en caso contrario
     */
    public boolean advertirUsuario(Long idUsuario, String razon, Long idAdminMod) {
        try {
            penalizacionService.registrarAdvertencia(idUsuario, razon, idAdminMod);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Registra un silencio (mute) para un usuario.
     *
     * @param idUsuario  ID del usuario silenciado
     * @param razon      Razón del silencio
     * @param duracion   Duración del silencio
     * @param idAdminMod ID del moderador que silencia
     * @return true si el registro fue exitoso, false en caso contrario
     */
    public boolean silenciarUsuario(Long idUsuario, String razon, java.time.Duration duracion, Long idAdminMod) {
        try {
            penalizacionService.registrarMute(idUsuario, razon, duracion, idAdminMod);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Registra una expulsión (kick) para un usuario.
     *
     * @param idUsuario  ID del usuario expulsado
     * @param razon      Razón de la expulsión
     * @param idAdminMod ID del moderador que expulsa
     * @return true si el registro fue exitoso, false en caso contrario
     */
    public boolean expulsarUsuario(Long idUsuario, String razon, Long idAdminMod) {
        try {
            penalizacionService.registrarExpulsion(idUsuario, razon, idAdminMod);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Registra un desbaneo para un usuario.
     *
     * @param idUsuario  ID del usuario desbaneado
     * @param idAdminMod ID del moderador que desbanea
     * @return true si el registro fue exitoso, false en caso contrario
     */
    public boolean desbanearUsuario(Long idUsuario, Long idAdminMod) {
        try {
            penalizacionService.registrarDesbaneo(idUsuario, idAdminMod);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Registra un timeout para un usuario.
     *
     * @param idUsuario  ID del usuario sancionado
     * @param razon      Razón del timeout
     * @param duracion   Duración del timeout
     * @param idAdminMod ID del moderador que aplica el timeout
     * @return true si el registro fue exitoso, false en caso contrario
     */
    public boolean timeoutUsuario(Long idUsuario, String razon, java.time.Duration duracion, Long idAdminMod) {
        try {
            penalizacionService.registrarTimeout(idUsuario, razon, duracion, idAdminMod);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Registra una acción de purga de mensajes.
     *
     * @param idAdminMod ID del moderador que ejecuta la purga
     * @param cantidad   Cantidad de mensajes eliminados
     * @param idCanal    ID del canal donde se realiza la purga
     */
    public void registrarPurge(Long idAdminMod, int cantidad, Long idCanal) {
        penalizacionService.registrarPurge(idAdminMod, cantidad, idCanal);
    }
}
