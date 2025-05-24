package backend.services;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

/**
 * Servicio para la gestión de roles en Discord.
 * Permite crear y eliminar roles en servidores de Discord.
 * 
 * @author PelayoPS
 */
public interface RoleService {
    /**
     * Crea un rol en el servidor especificado.
     * 
     * @param guild  Servidor de Discord
     * @param nombre Nombre del rol
     * @param color  Color en formato hexadecimal
     * @return true si la solicitud es válida y se delega correctamente
     */
    boolean crearRol(Guild guild, String nombre, String color);

    /**
     * Elimina un rol del servidor especificado.
     * 
     * @param role Rol a eliminar
     * @return true si la solicitud es válida y se delega correctamente
     */
    boolean eliminarRol(Role role);
}
