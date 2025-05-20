package bot.modules.user;

import bot.commands.user.*;
import bot.modules.CommandManager;
import bot.controller.UserController;

/**
 * Clase que maneja los comandos de usuario del bot.
 * Extiende de {@link CommandManager} para manejar eventos de interacción de
 * comandos.
 * 
 * @author PelayoPS
 */
public class UserCommands extends CommandManager {

    /**
     * Constructor de la clase UserCommands.
     * 
     * Agrega los comandos de usuario a la lista de comandos.
     * 
     * @param userController Controlador de usuario para los comandos
     * @param usuarioService Servicio de usuario para el comando /exp
     */
    public UserCommands(UserController userController, bot.services.UsuarioService usuarioService) {
        commands.add(new Avatar(userController));
        commands.add(new AIChat(userController));
        commands.add(new Exp(usuarioService));
        // commands.add(new HelpCommand(userController));
        // commands.add(new ProfileCommand(userController));
    }
}