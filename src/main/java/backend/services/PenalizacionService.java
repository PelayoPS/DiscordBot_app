package backend.services;

import java.time.LocalDateTime;
import java.util.List;

import backend.models.Penalizacion;

/**
 * Servicio para gestionar penalizaciones de usuarios.
 * Permite registrar, consultar, revocar y buscar penalizaciones por distintos
 * criterios.
 * 
 * @author PelayoPS
 */
public interface PenalizacionService extends Service<Penalizacion, Long> {
    /**
     * Busca penalizaciones por ID de usuario.
     * 
     * @param idUsuario ID del usuario
     * @return Lista de penalizaciones encontradas
     */
    List<Penalizacion> findByIdUsuario(Long idUsuario);

    /**
     * Busca penalizaciones por ID de administrador/moderador.
     * 
     * @param idAdminMod ID del admin/mod
     * @return Lista de penalizaciones encontradas
     */
    List<Penalizacion> findByIdAdminMod(Long idAdminMod);

    /**
     * Busca penalizaciones por tipo.
     * 
     * @param tipo Tipo de penalización
     * @return Lista de penalizaciones encontradas
     */
    List<Penalizacion> findByTipo(String tipo);

    /**
     * Busca penalizaciones en un rango de fechas.
     * 
     * @param inicio Fecha de inicio
     * @param fin    Fecha de fin
     * @return Lista de penalizaciones encontradas
     */
    List<Penalizacion> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Verifica si un usuario tiene una penalización activa de un tipo específico.
     * 
     * @param idUsuario ID del usuario
     * @param tipo      Tipo de penalización
     * @return true si tiene penalización activa, false si no
     */
    boolean tienePenalizacionActiva(Long idUsuario, String tipo);

    /**
     * Revoca una penalización estableciendo su duración a cero.
     * 
     * @param idPenalizacion ID de la penalización
     * @param idAdminMod     ID del admin/mod que revoca
     */
    void revocarPenalizacion(Long idPenalizacion, Long idAdminMod);

    /**
     * Registra una penalización de tipo BAN para un usuario.
     * 
     * @param idUsuario  ID del usuario a banear
     * @param razon      Razón del baneo
     * @param idAdminMod ID del moderador que ejecuta el baneo
     */
    void registrarBaneo(Long idUsuario, String razon, Long idAdminMod);

    /**
     * Registra una advertencia para un usuario.
     * 
     * @param idUsuario  ID del usuario advertido
     * @param razon      Razón de la advertencia
     * @param idAdminMod ID del moderador que advierte
     */
    void registrarAdvertencia(Long idUsuario, String razon, Long idAdminMod);

    /**
     * Registra un silencio (mute) para un usuario.
     * 
     * @param idUsuario  ID del usuario silenciado
     * @param razon      Razón del silencio
     * @param duracion   Duración del silencio
     * @param idAdminMod ID del moderador que silencia
     */
    void registrarMute(Long idUsuario, String razon, java.time.Duration duracion, Long idAdminMod);

    /**
     * Registra una expulsión (kick) para un usuario.
     * 
     * @param idUsuario  ID del usuario expulsado
     * @param razon      Razón de la expulsión
     * @param idAdminMod ID del moderador que expulsa
     */
    void registrarExpulsion(Long idUsuario, String razon, Long idAdminMod);

    /**
     * Registra un desbaneo para un usuario.
     * 
     * @param idUsuario  ID del usuario desbaneado
     * @param idAdminMod ID del moderador que desbanea
     */
    void registrarDesbaneo(Long idUsuario, Long idAdminMod);

    /**
     * Registra un timeout para un usuario.
     * 
     * @param idUsuario  ID del usuario sancionado
     * @param razon      Razón del timeout
     * @param duracion   Duración del timeout
     * @param idAdminMod ID del moderador que aplica el timeout
     */
    void registrarTimeout(Long idUsuario, String razon, java.time.Duration duracion, Long idAdminMod);

    /**
     * Registra una acción de purga de mensajes.
     * 
     * @param idAdminMod ID del moderador que ejecuta la purga
     * @param cantidad   Cantidad de mensajes eliminados
     * @param idCanal    ID del canal donde se realiza la purga
     */
    void registrarPurge(Long idAdminMod, int cantidad, Long idCanal);
}