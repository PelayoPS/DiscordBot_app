package bot.db.services.impl;

import bot.db.models.Penalizacion;
import bot.db.repositories.PenalizacionRepository;
import bot.db.services.PenalizacionService;
import java.time.LocalDateTime;
import java.util.List;

public class PenalizacionServiceImpl extends AbstractService<Penalizacion, Long> implements PenalizacionService {
    private final PenalizacionRepository penalizacionRepository;

    public PenalizacionServiceImpl(PenalizacionRepository penalizacionRepository) {
        super(penalizacionRepository);
        this.penalizacionRepository = penalizacionRepository;
    }

    @Override
    public List<Penalizacion> findByIdUsuario(Long idUsuario) {
        return penalizacionRepository.findByIdUsuario(idUsuario);
    }

    @Override
    public List<Penalizacion> findByIdAdminMod(Long idAdminMod) {
        return penalizacionRepository.findByIdAdminMod(idAdminMod);
    }

    @Override
    public List<Penalizacion> findByTipo(String tipo) {
        return penalizacionRepository.findByTipo(tipo);
    }

    @Override
    public List<Penalizacion> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin) {
        return penalizacionRepository.findByFechaBetween(inicio, fin);
    }

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
}