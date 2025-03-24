package bot.modules.admin;

import bot.commands.admin.CreateRole;
import bot.commands.admin.DeleteRole;
import bot.events.EventListener;
import bot.modules.CommandManager;

/**
 * Clase que maneja los comandos administrativos del bot.
 * Extiende de {@link EventListener} para manejar eventos de interacción de
 * comandos.
 */
public class AdminCommands extends CommandManager {

    /**
     * Constructor de la clase AdminCommands.
     * 
     * Agrega los comandos administrativos a la lista de comandos.
     */
    public AdminCommands() {
        commands.add(new CreateRole());
        commands.add(new DeleteRole());
    }

}