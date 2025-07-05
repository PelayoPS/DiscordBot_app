package backend.modules.admin;

import backend.commands.admin.CreateRole;
import backend.commands.admin.DeleteRole;
import backend.controller.AdminController;
import backend.modules.CommandManager;

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