package backend.repositories;

import java.util.Optional;

import backend.models.Usuario;

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