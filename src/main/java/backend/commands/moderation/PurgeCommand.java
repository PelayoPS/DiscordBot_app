package backend.commands.moderation;

import backend.api.Command;
import backend.facade.BotFacade;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * Comando para eliminar múltiples mensajes en un canal del servidor de Discord.
 * Permite a los usuarios con permisos adecuados eliminar mensajes en bloque.
 * 
 * @author PelayoPS
 */
public class PurgeCommand implements Command {
    private final BotFacade botFacade;

    /**
     * Constructor de la clase PurgeCommand.
     * 
     * @param botFacade Fachada para operaciones del bot
     */
    public PurgeCommand(BotFacade botFacade) {
        this.botFacade = botFacade;
    }

    /**
     * Devuelve el nombre del comando.
     * 
     * @return El nombre del comando
     */
    @Override
    public String getName() {
        return "purge";
    }

    /**
     * Devuelve la definición del comando slash para su registro en Discord.
     * 
     * @return Los datos del comando slash
     */
    @Override
    public SlashCommandData getSlash() {
        return Commands.slash(getName(), "Elimina múltiples mensajes")
                .addOption(OptionType.INTEGER, "cantidad", "Cantidad de mensajes a eliminar (2-100)", true);
    }

    /**
     * Ejecuta la lógica del comando cuando es invocado por un usuario.
     * Verifica permisos y elimina la cantidad de mensajes indicada.
     * 
     * @param event El evento de interacción del comando
     */
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            event.reply("No tienes permisos para usar este comando").setEphemeral(true).queue();
            return;
        }

        int amount = event.getOption("cantidad").getAsInt();
        if (amount < 2 || amount > 100) {
            event.reply("La cantidad debe estar entre 2 y 100 mensajes").setEphemeral(true).queue();
            return;
        }

        event.getChannel().asTextChannel().getHistory().retrievePast(amount)
                .queue(messages -> {
                    event.getChannel().asTextChannel().purgeMessages(messages);
                    botFacade.purgeMessages(event.getGuild().getId(), event.getChannel().getId(),
                            event.getUser().getId(), amount);
                    event.reply("Se han eliminado " + messages.size() + " mensajes").setEphemeral(true).queue();
                });
    }
}
