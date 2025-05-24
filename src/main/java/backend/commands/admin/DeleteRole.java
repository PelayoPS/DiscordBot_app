package backend.commands.admin;

import backend.api.Command;
import backend.controller.AdminController;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Comando para eliminar un rol existente en el servidor de Discord.
 * Permite a los usuarios con permisos adecuados eliminar roles seleccionados.
 * 
 * @author PelayoPS
 */
public class DeleteRole implements Command {
    private final AdminController adminController;

    /**
     * Constructor de la clase DeleteRole.
     * 
     * @param adminController Controlador de administración del bot
     */
    public DeleteRole(AdminController adminController) {
        this.adminController = adminController;
    }

    /**
     * Devuelve el nombre del comando.
     * 
     * @return El nombre del comando
     */
    @Override
    public String getName() {
        return "deleterole";
    }

    /**
     * Devuelve la definición del comando slash para su registro en Discord.
     * 
     * @return Los datos del comando slash
     */
    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("deleterole", "Elimina un rol del servidor")
                .addOption(OptionType.ROLE, "role", "El rol a eliminar", true);
    }

    /**
     * Ejecuta la lógica del comando cuando es invocado por un usuario.
     * Verifica permisos y solicita la eliminación del rol.
     * 
     * @param event El evento de interacción del comando
     */
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.reply("No tienes permisos para eliminar roles").setEphemeral(true).queue();
            return;
        }

        Role role = event.getOption("role").getAsRole();
        boolean exito = adminController.eliminarRol(role);
        if (exito) {
            event.reply("Rol eliminado correctamente").setEphemeral(true).queue();
        } else {
            event.reply("No se pudo eliminar el rol. Verifica los permisos o si el rol es válido.").setEphemeral(true)
                    .queue();
        }
    }
}