package bot.modules.admin;

import bot.commands.admin.CreateRole;
import bot.commands.admin.DeleteRole;
import bot.modules.CommandManager;
import bot.controller.AdminController;

/**
 * Clase que maneja los comandos administrativos del bot.
 * Extiende de {@link CommandManager} para manejar eventos de interacción de
 * comandos.
 * 
 * @author PelayoPS
 */
public class AdminCommands extends CommandManager {

    /**
     * Constructor de la clase AdminCommands.
     * 
     * Agrega los comandos administrativos a la lista de comandos.
     * 
     * @param adminController Controlador de administración para los comandos
     */
    public AdminCommands(AdminController adminController) {
        commands.add(new CreateRole(adminController));
        commands.add(new DeleteRole(adminController));
    }

}