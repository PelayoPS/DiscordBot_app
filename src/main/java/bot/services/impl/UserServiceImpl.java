package bot.services.impl;

import bot.services.UserService;
import net.dv8tion.jda.api.entities.User;

/**
 * Implementación del servicio de usuario.
 * Permite obtener información básica del usuario de Discord.
 * 
 * @author PelayoPS
 */
public class UserServiceImpl implements UserService {
    /**
     * Obtiene la URL del avatar de un usuario de Discord.
     * 
     * @param user Usuario de Discord
     * @return URL del avatar
     */
    @Override
    public String obtenerAvatar(User user) {
        return user.getEffectiveAvatarUrl();
    }
}
