package backend.services.impl;

import java.time.Duration;
import java.util.List;

import backend.models.Penalizacion;
import backend.services.ModerationService;
import backend.services.PenalizacionService;

/**
 * Implementación del servicio de moderación.
 * Permite gestionar penalizaciones y acciones de moderación sobre usuarios.
 * 
 * @author PelayoPS
 */
public class ModerationServiceImpl implements ModerationService {
    private final PenalizacionService penalizacionService;

    /**
     * Constructor de ModerationServiceImpl.
     * 
     * @param penalizacionService Servicio de penalizaciones
     */
    public ModerationServiceImpl(PenalizacionService penalizacionService) {
        this.penalizacionService = penalizacionService;
    }

    /**
     * Obtiene el historial de penalizaciones de un usuario.
     * 
     * @param idUsuario ID del usuario
     * @return Lista de penalizaciones
     */
    @Override
    public List<Penalizacion> obtenerHistorialUsuario(Long idUsuario) {
        return penalizacionService.findByIdUsuario(idUsuario);
    }

    /**
     * Banea a un usuario.
     * 
     * @param idUsuario  ID del usuario a banear
     * @param razon      Razón del baneo
     * @param idAdminMod ID del moderador que ejecuta el baneo
     * @return true si el registro fue exitoso, false en caso contrario
     */
    @Override
    public boolean banearUsuario(Long idUsuario, String razon, Long idAdminMod) {
        try {
            penalizacionService.registrarBaneo(idUsuario, razon, idAdminMod);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Advierte a un usuario.
     * 
     * @param idUsuario  ID del usuario advertido
     * @param razon      Razón de la advertencia
     * @param idAdminMod ID del moderador que advierte
     * @return true si el registro fue exitoso, false en caso contrario
     */
    @Override
    public boolean advertirUsuario(Long idUsuario, String razon, Long idAdminMod) {
        try {
            penalizacionService.registrarAdvertencia(idUsuario, razon, idAdminMod);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Silencia (mute) a un usuario.
     * 
     * @param idUsuario  ID del usuario silenciado
     * @param razon      Razón del silencio
     * @param duracion   Duración del silencio
     * @param idAdminMod ID del moderador que silencia
     * @return true si el registro fue exitoso, false en caso contrario
     */
    @Override
    public boolean silenciarUsuario(Long idUsuario, String razon, Duration duracion, Long idAdminMod) {
        try {
            penalizacionService.registrarMute(idUsuario, razon, duracion, idAdminMod);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Expulsa (kick) a un usuario.
     * 
     * @param idUsuario  ID del usuario expulsado
     * @param razon      Razón de la expulsión
     * @param idAdminMod ID del moderador que expulsa
     * @return true si el registro fue exitoso, false en caso contrario
     */
    @Override
    public boolean expulsarUsuario(Long idUsuario, String razon, Long idAdminMod) {
        try {
            penalizacionService.registrarExpulsion(idUsuario, razon, idAdminMod);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Desbanea a un usuario.
     * 
     * @param idUsuario  ID del usuario desbaneado
     * @param idAdminMod ID del moderador que desbanea
     * @return true si el registro fue exitoso, false en caso contrario
     */
    @Override
    public boolean desbanearUsuario(Long idUsuario, Long idAdminMod) {
        try {
            penalizacionService.registrarDesbaneo(idUsuario, idAdminMod);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Aplica timeout a un usuario.
     * 
     * @param idUsuario  ID del usuario sancionado
     * @param razon      Razón del timeout
     * @param duracion   Duración del timeout
     * @param idAdminMod ID del moderador que aplica el timeout
     * @return true si el registro fue exitoso, false en caso contrario
     */
    @Override
    public boolean timeoutUsuario(Long idUsuario, String razon, Duration duracion, Long idAdminMod) {
        try {
            penalizacionService.registrarTimeout(idUsuario, razon, duracion, idAdminMod);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Registra una purga de mensajes.
     * 
     * @param idAdminMod ID del moderador que purga
     * @param cantidad   Cantidad de mensajes eliminados
     * @param idCanal    ID del canal donde se realiza la purga
     */
    @Override
    public void registrarPurge(Long idAdminMod, int cantidad, Long idCanal) {
        penalizacionService.registrarPurge(idAdminMod, cantidad, idCanal);
    }
}
