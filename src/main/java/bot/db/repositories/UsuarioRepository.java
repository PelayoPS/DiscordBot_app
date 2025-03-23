package bot.db.repositories;

import bot.db.models.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends Repository<Usuario, Long> {
    Optional<Usuario> findByTipoUsuario(String tipoUsuario);
}