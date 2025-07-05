package backend.commands.moderation;

import backend.api.Command;
import backend.core.ServiceFactory;
import backend.facade.BotFacade;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Comando para expulsar a un usuario del servidor de Discord.
 * Permite a los usuarios con permisos adecuados expulsar miembros especificando
 * usuario y razón.
 * 
 * @author PelayoPS
 */
public class Kick implements Command {

    private final BotFacade botFacade;
    private final ServiceFactory serviceFactory;
    private String name = "kick";

    /**
     * Constructor de la clase Kick.
     * 
     * @param botFacade      Fachada para operaciones del bot
     * @param serviceFactory Fábrica de servicios para acceso a datos
     */
    public Kick(BotFacade botFacade, ServiceFactory serviceFactory) {
        this.botFacade = botFacade;
        this.serviceFactory = serviceFactory;
    }

    /**
     * Ejecuta la lógica del comando cuando es invocado por un usuario.
     * Verifica permisos y solicita la expulsión del usuario.
     * 
     * @param event El evento de interacción del comando
     */
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.KICK_MEMBERS)) {
            event.reply("No tienes permisos para expulsar usuarios").setEphemeral(true).queue();
            return;
        }

        User user = event.getOption("usuario").getAsUser();
        String razon = event.getOption("razon") != null ? event.getOption("razon").getAsString() : "No especificada";

        botFacade.kickUser(event.getGuild().getId(), user.getId(), razon);
        Long idUsuario = Long.valueOf(user.getId());
        Long idAdminMod = Long.valueOf(event.getUser().getId());
        var usuarioService = serviceFactory.getUsuarioService();
        if (usuarioService.findById(idUsuario).isEmpty()) {
            usuarioService.save(new backend.models.Usuario(idUsuario, "MIEMBRO"));
        }
        if (usuarioService.findById(idAdminMod).isEmpty()) {
            usuarioService.save(new backend.models.Usuario(idAdminMod, "MOD"));
        }
        event.getGuild().kick(user).reason(razon).queue(
                success -> {
                    event.reply("Usuario expulsado correctamente").setEphemeral(true).queue();
                    serviceFactory.getPenalizacionService().registrarExpulsion(idUsuario, razon, idAdminMod);
                });
    }

    /**
     * Devuelve la información del comando de slash.
     * 
     * @return SlashCommandData La información del comando de slash.
     */
    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("kick", "Expulsa a un usuario")
                .addOption(OptionType.USER, "usuario", "El usuario a expulsar", true)
                .addOption(OptionType.STRING, "razon", "La razón de la expulsión", false);
    }

    /**
     * Devuelve el nombre del comando.
     * 
     * @return El nombre del comando
     */
    @Override
    public String getName() {
        return name;
    }

}