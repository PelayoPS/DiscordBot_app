package bot.commands.moderation;

import bot.api.Command;
import bot.facade.BotFacade;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * Comando para mostrar el historial de moderación de un usuario en el servidor
 * de Discord.
 * Permite a los moderadores consultar las penalizaciones de un usuario.
 * 
 * @author PelayoPS
 */
public class HistoryCommand implements Command {
    private final BotFacade botFacade;

    /**
     * Constructor de la clase HistoryCommand.
     * 
     * @param botFacade Fachada para operaciones del bot
     */
    public HistoryCommand(BotFacade botFacade) {
        this.botFacade = botFacade;
    }

    /**
     * Devuelve el nombre del comando.
     * 
     * @return El nombre del comando
     */
    @Override
    public String getName() {
        return "history";
    }

    /**
     * Devuelve la definición del comando slash para su registro en Discord.
     * 
     * @return Los datos del comando slash
     */
    @Override
    public SlashCommandData getSlash() {
        return Commands.slash(getName(), "Muestra el historial de moderación de un usuario")
                .addOption(OptionType.USER, "usuario", "Usuario a consultar", true);
    }

    /**
     * Ejecuta la lógica del comando cuando es invocado por un usuario.
     * Verifica permisos y muestra el historial de penalizaciones del usuario
     * objetivo.
     * 
     * @param event El evento de interacción del comando
     */
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

        var historyRecords = botFacade.getUserHistory(target.getId());
        if (historyRecords instanceof java.util.List<?> list && !list.isEmpty()) {
            var embed = new net.dv8tion.jda.api.EmbedBuilder()
                    .setTitle("Historial de " + target.getUser().getName())
                    .setColor(0x00FF00);
            for (Object record : list) {
                if (record instanceof bot.models.Penalizacion penalizacion) {
                    embed.addField("Tipo: " + penalizacion.getTipo(), "Razón: " + penalizacion.getRazon(), false);
                    embed.addField("Fecha: " + penalizacion.getFecha().toString(),
                            "Duración: " + penalizacion.getDuracion().toString(), false);
                }
            }
            event.replyEmbeds(embed.build()).queue();
        } else {
            var embed = new net.dv8tion.jda.api.EmbedBuilder()
                    .setTitle("Historial de " + target.getUser().getName())
                    .setDescription("No hay registros disponibles")
                    .setColor(0x00FF00);
            event.replyEmbeds(embed.build()).queue();
        }
    }
}
