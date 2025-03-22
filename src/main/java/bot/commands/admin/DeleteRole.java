package bot.commands.admin;

import bot.api.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Clase que implementa el comando para eliminar un rol en Discord.
 */
public class DeleteRole implements Command {

    @Override
    public String getName() {
        return "deleterole";
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("deleterole", "Elimina un rol del servidor")
                .addOption(OptionType.ROLE, "role", "El rol a eliminar", true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Verificar permisos
        if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.reply("No tienes permisos para eliminar roles").setEphemeral(true).queue();
            return;
        }

        Role role = event.getOption("role").getAsRole();

        role.delete().queue(
                success -> event.reply("Rol eliminado correctamente").setEphemeral(true).queue(),
                error -> event.reply("No se pudo eliminar el rol: " + error.getMessage()).setEphemeral(true).queue());
    }
}