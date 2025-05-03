package bot.repositories;

import bot.models.Usuario;
import java.util.Optional;

/**
 * Repositorio para operaciones sobre usuarios.
 * Permite buscar usuarios por tipo de usuario.
 * 
 * @author PelayoPS
 */
public interface UsuarioRepository extends Repository<Usuario, Long> {
    /**
     * Busca un usuario por tipo de usuario.
     * 
     * @param tipoUsuario Tipo de usuario
     * @return Optional con el usuario encontrado o vacío si no existe
     */
    Optional<Usuario> findByTipoUsuario(String tipoUsuario);
}