package bot.commands.moderation;

import bot.api.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class TimeoutCommand implements Command {
    @Override
    public String getName() {
        return "timeout";
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash(getName(), "Aplica timeout a un usuario")
                .addOption(OptionType.USER, "usuario", "Usuario a quien aplicar timeout", true)
                .addOption(OptionType.INTEGER, "duracion", "Duración en minutos", true)
                .addOption(OptionType.STRING, "razon", "Razón del timeout", false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("No tienes permisos para usar este comando").setEphemeral(true).queue();
            return;
        }

        var target = event.getOption("usuario").getAsMember();
        var duration = event.getOption("duracion").getAsInt();
        var reason = event.getOption("razon") != null ? event.getOption("razon").getAsString()
                : "No se proporcionó razón";

        target.timeoutFor(java.time.Duration.ofMinutes(duration)).reason(reason).queue(
                success -> event.reply("Timeout aplicado a " + target.getAsMention() + " por " + duration + " minutos")
                        .queue(),
                error -> event.reply("No se pudo aplicar el timeout").setEphemeral(true).queue());
    }
}
