package bot.db.repositories;

import bot.db.models.Penalizacion;
import java.util.List;
import java.time.LocalDateTime;

public interface PenalizacionRepository extends Repository<Penalizacion, Long> {
    List<Penalizacion> findByIdUsuario(Long idUsuario);

    List<Penalizacion> findByIdAdminMod(Long idAdminMod);

    List<Penalizacion> findByTipo(String tipo);

    List<Penalizacion> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}