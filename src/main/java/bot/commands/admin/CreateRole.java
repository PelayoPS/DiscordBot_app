package bot.commands.admin;

import bot.api.Command;
import bot.controller.AdminController;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Comando para crear un nuevo rol en el servidor de Discord.
 * Permite a los usuarios con permisos adecuados crear roles especificando
 * nombre y color.
 * 
 * @author PelayoPS
 */
public class CreateRole implements Command {
    private final AdminController adminController;

    /**
     * Constructor de la clase CreateRole.
     * 
     * @param adminController Controlador de administración del bot
     */
    public CreateRole(AdminController adminController) {
        this.adminController = adminController;
    }

    /**
     * Devuelve el nombre del comando.
     * 
     * @return El nombre del comando
     */
    @Override
    public String getName() {
        return "createrole";
    }

    /**
     * Devuelve la definición del comando slash para su registro en Discord.
     * 
     * @return Los datos del comando slash
     */
    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("createrole", "Crea un nuevo rol en el servidor")
                .addOption(OptionType.STRING, "nombre", "Nombre del rol", true)
                .addOption(OptionType.STRING, "color", "Color del rol en formato hexadecimal", false);
    }

    /**
     * Ejecuta la lógica del comando cuando es invocado por un usuario.
     * Verifica permisos y solicita la creación del rol.
     * 
     * @param event El evento de interacción del comando
     */
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Verificar permisos
        if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.reply("No tienes permisos para crear roles").setEphemeral(true).queue();
            return;
        }

        String name = event.getOption("nombre").getAsString();
        String color = event.getOption("color") != null ? event.getOption("color").getAsString() : "000000";

        boolean exito = adminController.crearRol(event.getGuild(), name, color);
        if (exito) {
            event.reply("Solicitud de creación de rol enviada correctamente").setEphemeral(true).queue();
        } else {
            event.reply("No se pudo crear el rol. Verifica el color hexadecimal o los permisos.").setEphemeral(true)
                    .queue();
        }
    }
}