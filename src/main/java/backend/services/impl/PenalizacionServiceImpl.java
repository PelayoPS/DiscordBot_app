package backend.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import backend.models.Penalizacion;
import backend.repositories.PenalizacionRepository;
import backend.services.PenalizacionService;

/**
 * Implementación del servicio de penalizaciones.
 * Permite registrar, consultar y revocar penalizaciones de usuarios.
 * 
 * @author PelayoPS
 */
public class PenalizacionServiceImpl extends AbstractService<Penalizacion, Long> implements PenalizacionService {
    private final PenalizacionRepository penalizacionRepository;

    /**
     * Constructor de PenalizacionServiceImpl.
     * 
     * @param penalizacionRepository Repositorio de penalizaciones
     */
    public PenalizacionServiceImpl(PenalizacionRepository penalizacionRepository) {
        super(penalizacionRepository);
        this.penalizacionRepository = penalizacionRepository;
    }

    /**
     * Busca penalizaciones por ID de usuario.
     * 
     * @param idUsuario ID del usuario
     * @return Lista de penalizaciones encontradas
     */
    @Override
    public List<Penalizacion> findByIdUsuario(Long idUsuario) {
        return penalizacionRepository.findByIdUsuario(idUsuario);
    }

    /**
     * Busca penalizaciones por ID de administrador/moderador.
     * 
     * @param idAdminMod ID del admin/mod
     * @return Lista de penalizaciones encontradas
     */
    @Override
    public List<Penalizacion> findByIdAdminMod(Long idAdminMod) {
        return penalizacionRepository.findByIdAdminMod(idAdminMod);
    }

    /**
     * Busca penalizaciones por tipo.
     * 
     * @param tipo Tipo de penalización
     * @return Lista de penalizaciones encontradas
     */
    @Override
    public List<Penalizacion> findByTipo(String tipo) {
        return penalizacionRepository.findByTipo(tipo);
    }

    /**
     * Busca penalizaciones en un rango de fechas.
     * 
     * @param inicio Fecha de inicio
     * @param fin    Fecha de fin
     * @return Lista de penalizaciones encontradas
     */
    @Override
    public List<Penalizacion> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin) {
        return penalizacionRepository.findByFechaBetween(inicio, fin);
    }

    /**
     * Verifica si un usuario tiene una penalización activa de un tipo específico.
     * 
     * @param idUsuario ID del usuario
     * @param tipo      Tipo de penalización
     * @return true si tiene penalización activa, false si no
     */
    @Override
    public boolean tienePenalizacionActiva(Long idUsuario, String tipo) {
        List<Penalizacion> penalizaciones = findByIdUsuario(idUsuario);
        LocalDateTime ahora = LocalDateTime.now();

        return penalizaciones.stream()
                .filter(p -> p.getTipo().equals(tipo))
                .anyMatch(p -> {
                    LocalDateTime finPenalizacion = p.getFecha().plus(p.getDuracion());
                    return ahora.isBefore(finPenalizacion);
                });
    }

    /**
     * Revoca una penalización estableciendo su duración a cero.
     * 
     * @param idPenalizacion ID de la penalización
     * @param idAdminMod     ID del admin/mod que revoca
     */
    @Override
    public void revocarPenalizacion(Long idPenalizacion, Long idAdminMod) {
        findById(idPenalizacion).ifPresent(penalizacion -> {
            // Establecer la duración a 0 para indicar que ha sido revocada
            penalizacion = new Penalizacion(
                    penalizacion.getIdPenalizacion(),
                    penalizacion.getIdUsuario(),
                    idAdminMod, // Actualizar con el admin que revoca
                    penalizacion.getTipo(),
                    penalizacion.getFecha(),
                    penalizacion.getRazon() + " [REVOCADA]",
                    java.time.Duration.ZERO);
            save(penalizacion);
        });
    }

    /**
     * Registra un baneo para un usuario.
     * 
     * @param idUsuario  ID del usuario
     * @param razon      Razón del baneo
     * @param idAdminMod ID del admin/mod que banea
     */
    @Override
    public void registrarBaneo(Long idUsuario, String razon, Long idAdminMod) {
        Penalizacion penalizacion = new Penalizacion(
                null, // ID autogenerado
                idUsuario,
                idAdminMod,
                "BAN",
                LocalDateTime.now(),
                razon,
                java.time.Duration.ofDays(0) // Baneo permanente por defecto, ajustar si se requiere duración
        );
        penalizacionRepository.save(penalizacion);
    }

    /**
     * Registra una advertencia para un usuario.
     * 
     * @param idUsuario  ID del usuario
     * @param razon      Razón de la advertencia
     * @param idAdminMod ID del admin/mod que advierte
     */
    @Override
    public void registrarAdvertencia(Long idUsuario, String razon, Long idAdminMod) {
        Penalizacion penalizacion = new Penalizacion(
                null, // ID autogenerado
                idUsuario,
                idAdminMod,
                "WARN",
                LocalDateTime.now(),
                razon,
                java.time.Duration.ZERO // Advertencia sin duración
        );
        penalizacionRepository.save(penalizacion);
    }

    /**
     * Registra una expulsión para un usuario.
     * 
     * @param idUsuario  ID del usuario
     * @param razon      Razón de la expulsión
     * @param idAdminMod ID del admin/mod que expulsa
     */
    @Override
    public void registrarExpulsion(Long idUsuario, String razon, Long idAdminMod) {
        Penalizacion penalizacion = new Penalizacion(
                null, // ID autogenerado
                idUsuario,
                idAdminMod,
                "KICK",
                LocalDateTime.now(),
                razon,
                java.time.Duration.ZERO // Expulsión sin duración
        );
        penalizacionRepository.save(penalizacion);
    }

    /**
     * Registra un desbaneo para un usuario.
     * 
     * @param idUsuario  ID del usuario
     * @param idAdminMod ID del admin/mod que desbanea
     */
    @Override
    public void registrarDesbaneo(Long idUsuario, Long idAdminMod) {
        Penalizacion penalizacion = new Penalizacion(
                null, // ID autogenerado
                idUsuario,
                idAdminMod,
                "UNBAN",
                LocalDateTime.now(),
                "Desbaneo realizado",
                java.time.Duration.ZERO);
        penalizacionRepository.save(penalizacion);
    }

    /**
     * Registra un timeout para un usuario.
     * 
     * @param idUsuario  ID del usuario
     * @param razon      Razón del timeout
     * @param duracion   Duración del timeout
     * @param idAdminMod ID del admin/mod que aplica el timeout
     */
    @Override
    public void registrarTimeout(Long idUsuario, String razon, java.time.Duration duracion, Long idAdminMod) {
        Penalizacion penalizacion = new Penalizacion(
                null, // ID autogenerado
                idUsuario,
                idAdminMod,
                "TIMEOUT",
                LocalDateTime.now(),
                razon,
                duracion);
        penalizacionRepository.save(penalizacion);
    }

    /**
     * Registra una purga de mensajes.
     * 
     * @param idAdminMod ID del admin/mod que purga
     * @param cantidad   Cantidad de mensajes eliminados
     * @param idCanal    ID del canal donde se realiza la purga
     */
    @Override
    public void registrarPurge(Long idAdminMod, int cantidad, Long idCanal) {
        Penalizacion penalizacion = new Penalizacion(
                null, // ID autogenerado
                null, // No aplica a un usuario específico
                idAdminMod,
                "PURGE",
                LocalDateTime.now(),
                "Purge de " + cantidad + " mensajes en canal " + idCanal,
                java.time.Duration.ZERO);
        penalizacionRepository.save(penalizacion);
    }

    /**
     * Registra un mute para un usuario.
     * 
     * @param idUsuario  ID del usuario
     * @param razon      Razón del mute
     * @param duracion   Duración del mute
     * @param idAdminMod ID del admin/mod que silencia
     */
    @Override
    public void registrarMute(Long idUsuario, String razon, java.time.Duration duracion, Long idAdminMod) {
        Penalizacion penalizacion = new Penalizacion(
                null, // ID autogenerado
                idUsuario,
                idAdminMod,
                "MUTE",
                LocalDateTime.now(),
                razon,
                duracion);
        penalizacionRepository.save(penalizacion);
    }
}