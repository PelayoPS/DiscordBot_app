package bot.controller;

import bot.services.UserService;
import bot.services.AIChatService;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

/**
 * Controlador de usuario que orquesta la lógica de gestión de usuarios.
 * Permite obtener información de usuarios y gestionar chats IA.
 * 
 * @author PelayoPS
 */
public class UserController {
    private final UserService userService;
    private final AIChatService aiChatService;

    /**
     * Constructor de la clase UserController.
     * 
     * @param userService   Servicio de usuarios
     * @param aiChatService Servicio de chat IA
     */
    public UserController(UserService userService, AIChatService aiChatService) {
        this.userService = userService;
        this.aiChatService = aiChatService;
    }

    /**
     * Obtiene la URL del avatar de un usuario.
     * 
     * @param user Usuario de Discord
     * @return URL del avatar
     */
    public String obtenerAvatar(User user) {
        return userService.obtenerAvatar(user);
    }

    /**
     * Inicia un chat IA en un canal de hilo.
     * 
     * @param thread Canal de hilo
     * @param user   Usuario de Discord
     * @param prompt Mensaje inicial para el chat IA
     */
    public void iniciarChatIA(ThreadChannel thread, User user, String prompt) {
        aiChatService.iniciarChatIA(thread, user, prompt);
    }
}
