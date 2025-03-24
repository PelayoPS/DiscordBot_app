package bot.commands.moderation;

import java.util.ArrayList;
import java.util.List;

import bot.api.Command;
import bot.db.models.Penalizacion;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
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

        if (target == null) {
            event.reply("Usuario no encontrado").setEphemeral(true).queue();
            return;
        }

        var historyRecords = getHistoryRecords(target);
        if (historyRecords.isEmpty()) {
            var embed = new EmbedBuilder()
                    .setTitle("Historial de " + target.getUser().getName())
                    .setDescription("No hay registros disponibles")
                    .setColor(0x00FF00);
            event.replyEmbeds(embed.build()).queue();
            return;
        }

        var embed = new EmbedBuilder()
                .setTitle("Historial de " + target.getUser().getName())
                .setColor(0x00FF00);
        for (Penalizacion record : historyRecords) {
            embed.addField("Tipo: " + record.getTipo(), "Razón: " + record.getRazon(), false);
            embed.addField("Fecha: " + record.getFecha().toString(), "Duración: " + record.getDuracion().toString(),
                    false);
        }
        event.replyEmbeds(embed.build()).queue();
    }

    private List<Penalizacion> getHistoryRecords(Member target) {
        // !TODO: Implementar la lógica para obtener el historial de penalizaciones de
        // la base de datos

        return new ArrayList<Penalizacion>();
    }
}
