package bot.commands.moderation;

import bot.api.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class WarnCommand implements Command {
    @Override
    public String getName() {
        return "warn";
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash(getName(), "Advierte a un usuario")
                .addOption(OptionType.USER, "usuario", "Usuario a advertir", true)
                .addOption(OptionType.STRING, "razon", "Razón de la advertencia", true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("No tienes permisos para usar este comando").setEphemeral(true).queue();
            return;
        }

        var target = event.getOption("usuario").getAsMember();
        var reason = event.getOption("razon").getAsString();

        // Aquí se podría implementar la lógica para almacenar la advertencia en una
        // base de datos
        event.reply("Se ha advertido a " + target.getAsMention() + " por: " + reason).queue();
    }
}
