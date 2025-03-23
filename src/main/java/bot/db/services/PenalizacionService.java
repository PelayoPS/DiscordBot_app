package bot.db.services;

import bot.db.models.Penalizacion;
import java.time.LocalDateTime;
import java.util.List;

public interface PenalizacionService extends Service<Penalizacion, Long> {
    List<Penalizacion> findByIdUsuario(Long idUsuario);

    List<Penalizacion> findByIdAdminMod(Long idAdminMod);

    List<Penalizacion> findByTipo(String tipo);

    List<Penalizacion> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    boolean tienePenalizacionActiva(Long idUsuario, String tipo);

    void revocarPenalizacion(Long idPenalizacion, Long idAdminMod);
}