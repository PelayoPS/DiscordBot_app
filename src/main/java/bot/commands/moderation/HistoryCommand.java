package bot.commands.moderation;

import bot.api.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class HistoryCommand implements Command {
    @Override
    public String getName() {
        return "history";
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash(getName(), "Muestra el historial de moderación de un usuario")
                .addOption(OptionType.USER, "usuario", "Usuario a consultar", true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("No tienes permisos para usar este comando").setEphemeral(true).queue();
            return;
        }

        var target = event.getOption("usuario").getAsMember();

        // Aquí se implementaría la lógica para obtener el historial desde una base de
        // datos
        // !TODO
        var embed = new EmbedBuilder()
                .setTitle("Historial de " + target.getUser().getName())
                .setDescription("No hay registros disponibles")
                .setColor(0x00FF00)
                .build();

        event.replyEmbeds(embed).queue();
    }
}
