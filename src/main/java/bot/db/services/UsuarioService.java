package bot.db.services;

import bot.db.models.Usuario;
import java.util.Optional;

public interface UsuarioService extends Service<Usuario, Long> {
    Optional<Usuario> findByTipoUsuario(String tipoUsuario);

    void actualizarExperiencia(Long idUsuario, int puntosXp);

    void agregarPenalizacion(Long idUsuario, Long idAdminMod, String tipo, String razon, java.time.Duration duracion);
}