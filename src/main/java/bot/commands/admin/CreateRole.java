package bot.commands.admin;

import bot.api.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Clase que implementa el comando para crear un rol en Discord.
 */
public class CreateRole implements Command {

    @Override
    public String getName() {
        return "createrole";
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("createrole", "Crea un nuevo rol en el servidor")
                .addOption(OptionType.STRING, "nombre", "Nombre del rol", true)
                .addOption(OptionType.STRING, "color", "Color del rol en formato hexadecimal", false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Verificar permisos
        if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.reply("No tienes permisos para crear roles").setEphemeral(true).queue();
            return;
        }

        String name = event.getOption("nombre").getAsString();
        String color = event.getOption("color") != null ? event.getOption("color").getAsString() : "000000";

        try {
            event.getGuild().createRole()
                    .setName(name)
                    .setColor(Integer.parseInt(color, 16))
                    .queue(
                            role -> event.reply("Rol creado correctamente: " + role.getAsMention()).setEphemeral(true)
                                    .queue(),
                            error -> event.reply("No se pudo crear el rol: " + error.getMessage()).setEphemeral(true)
                                    .queue());
        } catch (NumberFormatException e) {
            event.reply("El color debe estar en formato hexadecimal válido").setEphemeral(true).queue();
        }
    }
}