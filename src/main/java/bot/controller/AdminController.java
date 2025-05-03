package bot.controller;

import bot.services.RoleService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

/**
 * Controlador de administración que orquesta la lógica de gestión de roles.
 * Permite crear y eliminar roles en el servidor de Discord a través del
 * servicio correspondiente.
 * 
 * @author PelayoPS
 */
public class AdminController {
    private final RoleService roleService;

    /**
     * Constructor de la clase AdminController.
     * 
     * @param roleService Servicio de roles para operaciones administrativas
     */
    public AdminController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Solicita la creación de un rol en el servidor.
     * 
     * @param guild  Servidor de Discord
     * @param nombre Nombre del rol
     * @param color  Color en formato hexadecimal
     * @return true si la solicitud es válida y se delega correctamente
     */
    public boolean crearRol(Guild guild, String nombre, String color) {
        return roleService.crearRol(guild, nombre, color);
    }

    /**
     * Solicita la eliminación de un rol en el servidor.
     * 
     * @param role Rol a eliminar
     * @return true si la solicitud es válida y se delega correctamente
     */
    public boolean eliminarRol(Role role) {
        return roleService.eliminarRol(role);
    }
}
