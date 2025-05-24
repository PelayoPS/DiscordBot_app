package backend.commands.moderation;

import backend.api.Command;
import backend.core.ServiceFactory;
import backend.facade.BotFacade;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * Comando para advertir a un usuario en el servidor de Discord.
 * Permite a los usuarios con permisos adecuados advertir miembros especificando
 * usuario y razón.
 * 
 * @author PelayoPS
 */
public class WarnCommand implements Command {
    private final BotFacade botFacade;
    private final ServiceFactory serviceFactory;

    /**
     * Constructor de la clase WarnCommand.
     * 
     * @param botFacade      Fachada para operaciones del bot
     * @param serviceFactory Fábrica de servicios para acceso a datos
     */
    public WarnCommand(BotFacade botFacade, ServiceFactory serviceFactory) {
        this.botFacade = botFacade;
        this.serviceFactory = serviceFactory;
    }

    /**
     * Devuelve el nombre del comando.
     * 
     * @return El nombre del comando
     */
    @Override
    public String getName() {
        return "warn";
    }

    /**
     * Devuelve la definición del comando slash para su registro en Discord.
     * 
     * @return Los datos del comando slash
     */
    @Override
    public SlashCommandData getSlash() {
        return Commands.slash(getName(), "Advierte a un usuario")
                .addOption(OptionType.USER, "usuario", "Usuario a advertir", true)
                .addOption(OptionType.STRING, "razon", "Razón de la advertencia", true);
    }

    /**
     * Ejecuta la lógica del comando cuando es invocado por un usuario.
     * Verifica permisos y registra la advertencia al usuario objetivo.
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
        var reason = event.getOption("razon").getAsString();

        Long idUsuario = Long.valueOf(target.getId());
        Long idAdminMod = Long.valueOf(event.getUser().getId());
        var usuarioService = serviceFactory.getUsuarioService();
        if (usuarioService.findById(idUsuario).isEmpty()) {
            usuarioService.save(new backend.models.Usuario(idUsuario, "MIEMBRO"));
        }
        if (usuarioService.findById(idAdminMod).isEmpty()) {
            usuarioService.save(new backend.models.Usuario(idAdminMod, "MOD"));
        }

        botFacade.warnUser(event.getGuild().getId(), target.getId(), reason);
        event.reply("Se ha advertido a " + target.getAsMention() + " por: " + reason).queue();

        serviceFactory.getPenalizacionService().registrarAdvertencia(idUsuario, reason, idAdminMod);
    }
}
