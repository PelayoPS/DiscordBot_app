package bot.commands.moderation;

import bot.api.Command;
import bot.facade.BotFacade;
import bot.core.ServiceFactory;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * Comando para aplicar un timeout a un usuario en el servidor de Discord.
 * Permite a los usuarios con permisos adecuados aplicar un timeout
 * especificando usuario, duración y razón.
 * 
 * @author PelayoPS
 */
public class TimeoutCommand implements Command {
    private final BotFacade botFacade;
    private final ServiceFactory serviceFactory;

    /**
     * Constructor de la clase TimeoutCommand.
     * 
     * @param botFacade      Fachada para operaciones del bot
     * @param serviceFactory Fábrica de servicios para acceso a datos
     */
    public TimeoutCommand(BotFacade botFacade, ServiceFactory serviceFactory) {
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
        return "timeout";
    }

    /**
     * Devuelve la definición del comando slash para su registro en Discord.
     * 
     * @return Los datos del comando slash
     */
    @Override
    public SlashCommandData getSlash() {
        return Commands.slash(getName(), "Aplica timeout a un usuario")
                .addOption(OptionType.USER, "usuario", "Usuario a quien aplicar timeout", true)
                .addOption(OptionType.INTEGER, "duracion", "Duración en minutos", true)
                .addOption(OptionType.STRING, "razon", "Razón del timeout", false);
    }

    /**
     * Ejecuta la lógica del comando cuando es invocado por un usuario.
     * Verifica permisos y aplica el timeout al usuario objetivo.
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
        var duration = event.getOption("duracion").getAsInt();
        var reason = event.getOption("razon") != null ? event.getOption("razon").getAsString()
                : "No se proporcionó razón";
        // Registrar usuario si no existe
        Long idUsuario = Long.valueOf(target.getId());
        Long idAdminMod = Long.valueOf(event.getUser().getId());
        var usuarioService = serviceFactory.getUsuarioService();
        if (usuarioService.findById(idUsuario).isEmpty()) {
            usuarioService.save(new bot.models.Usuario(idUsuario, "MIEMBRO"));
        }
        // Registrar admin/mod si no existe
        if (usuarioService.findById(idAdminMod).isEmpty()) {
            usuarioService.save(new bot.models.Usuario(idAdminMod, "MOD"));
        }
        botFacade.timeoutUser(event.getGuild().getId(), target.getId(), reason, java.time.Duration.ofMinutes(duration));
        target.timeoutFor(java.time.Duration.ofMinutes(duration)).reason(reason).queue(
                success -> {
                    event.reply("Timeout aplicado a " + target.getAsMention() + " por " + duration + " minutos")
                            .queue();
                    java.time.Duration duracion = java.time.Duration.ofMinutes(duration);
                    serviceFactory.getPenalizacionService().registrarTimeout(idUsuario, reason, duracion, idAdminMod);
                },
                error -> event.reply("No se pudo aplicar el timeout").setEphemeral(true).queue());
    }
}
