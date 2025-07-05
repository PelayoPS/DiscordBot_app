package backend.commands.moderation;

import backend.api.Command;
import backend.facade.BotFacade;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * Comando para desbanear a un usuario en el servidor de Discord.
 * Permite a los usuarios con permisos adecuados desbanear miembros
 * especificando el usuario.
 * 
 * @author PelayoPS
 */
public class Unban implements Command {
    private final BotFacade botFacade;

    /**
     * Constructor de la clase Unban.
     * 
     * @param botFacade Fachada para operaciones del bot
     */
    public Unban(BotFacade botFacade) {
        this.botFacade = botFacade;
    }

    /**
     * Ejecuta la lógica del comando cuando es invocado por un usuario.
     * Verifica permisos y realiza el desbaneo del usuario objetivo.
     * 
     * @param event El evento de interacción del comando
     */
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("No tienes permisos para desbanear usuarios").setEphemeral(true).queue();
            return;
        }
        var userId = event.getOption("usuario").getAsUser();
        botFacade.unbanUser(event.getGuild().getId(), userId.getId());
        event.getGuild().unban(userId).queue(
                success -> event.reply("Usuario desbaneado correctamente").setEphemeral(true).queue(),
                error -> event.reply("No se pudo desbanear al usuario: " + error.getMessage()).setEphemeral(true)
                        .queue());
    }

    /**
     * Devuelve la definición del comando slash para su registro en Discord.
     * 
     * @return Los datos del comando slash
     */
    @Override
    public SlashCommandData getSlash() {
        return Commands.slash(getName(), "Desbanea a un usuario usando su ID")
                .addOption(OptionType.USER, "usuario", "Usuario a desbanear", true).setGuildOnly(true);
    }

    /**
     * Devuelve el nombre del comando.
     * 
     * @return El nombre del comando
     */
    @Override
    public String getName() {
        return "unban";
    }
}
