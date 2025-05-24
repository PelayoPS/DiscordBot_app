package backend.services;

import net.dv8tion.jda.api.entities.User;

/**
 * Servicio para la gestión de usuarios.
 * Permite obtener información básica del usuario de Discord.
 * 
 * @author PelayoPS
 */
public interface UserService {
    /**
     * Obtiene la URL del avatar de un usuario.
     * 
     * @param user Usuario de Discord
     * @return URL del avatar
     */
    String obtenerAvatar(User user);
}
