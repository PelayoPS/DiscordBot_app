package bot.modules.user;

import bot.commands.user.*;
import bot.events.EventListener;
import bot.modules.CommandManager;

/**
 * Clase que maneja los comandos de usuario del bot.
 * Extiende de {@link EventListener} para manejar eventos de interacción de
 * comandos.
 */
public class UserCommands extends CommandManager {

    /**
     * Constructor de la clase UserCommands.
     * 
     * Agrega los comandos de usuario a la lista de comandos.
     */
    public UserCommands() {
        commands.add(new Avatar());
        // commands.add(new HelpCommand());
        // commands.add(new ProfileCommand());
    }
}