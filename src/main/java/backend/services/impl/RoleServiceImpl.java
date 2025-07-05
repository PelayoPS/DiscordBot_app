package backend.services.impl;

import backend.services.RoleService;
import net.dv8tion.jda.api.entities.Guild;

/**
 * Implementación del servicio de roles.
 * Permite crear y eliminar roles en un servidor de Discord.
 * 
 * @author PelayoPS
 */
public class RoleServiceImpl implements RoleService {
    /**
     * Crea un rol en el servidor especificado.
     * 
     * @param guild  Servidor de Discord
     * @param nombre Nombre del rol
     * @param color  Color en formato hexadecimal
     * @return true si la creación fue exitosa, false en caso contrario
     */
    @Override
    public boolean crearRol(Guild guild, String nombre, String color) {
        try {
            guild.createRole()
                    .setName(nombre)
                    .setColor(Integer.parseInt(color, 16))
                    .queue();
            return true;
        } catch (Exception e) {
            // Aquí se podría loggear el error
            return false;
        }
    }

    /**
     * Elimina un rol del servidor.
     * 
     * @param role Rol a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    @Override
    public boolean eliminarRol(net.dv8tion.jda.api.entities.Role role) {
        try {
            role.delete().queue();
            return true;
        } catch (Exception e) {
            // Aquí se podría loggear el error
            return false;
        }
    }
}
