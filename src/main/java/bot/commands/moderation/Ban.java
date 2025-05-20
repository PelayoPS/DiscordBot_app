package bot.commands.moderation;

import java.util.concurrent.TimeUnit;
import bot.api.Command;
import bot.facade.BotFacade;
import bot.core.ServiceFactory;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Comando para banear a un usuario del servidor de Discord.
 * Permite a los usuarios con permisos adecuados banear miembros especificando
 * usuario y razón.
 * 
 * @author PelayoPS
 */
public class Ban implements Command {
    private final BotFacade botFacade;
    private final ServiceFactory serviceFactory;

    /**
     * Constructor de la clase Ban.
     * 
     * @param botFacade      Fachada para operaciones del bot
     * @param serviceFactory Fábrica de servicios para acceso a datos
     */
    public Ban(BotFacade botFacade, ServiceFactory serviceFactory) {
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
        return "ban";
    }

    /**
     * Devuelve la definición del comando slash para su registro en Discord.
     * 
     * @return Los datos del comando slash
     */
    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("ban", "Banea a un usuario del servidor")
                .addOption(OptionType.USER, "usuario", "El usuario a banear", true)
                .addOption(OptionType.STRING, "razon", "Razón del baneo", false)
                .setGuildOnly(true);
    }

    /**
     * Ejecuta la lógica del comando cuando es invocado por un usuario.
     * Verifica permisos y solicita el baneo del usuario.
     * 
     * @param event El evento de interacción del comando
     */
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("No tienes permisos para banear usuarios").setEphemeral(true).queue();
            return;
        }

        Member target = event.getOption("usuario").getAsMember();
        String reason = event.getOption("razon") != null ? event.getOption("razon").getAsString()
                : "No se proporcionó razón";

        if (target != null) {
            botFacade.banUser(event.getGuild().getId(), target.getId(), reason);
            Long idUsuario = Long.valueOf(target.getId());
            Long idAdminMod = Long.valueOf(event.getUser().getId());
            var usuarioService = serviceFactory.getUsuarioService();
            if (usuarioService.findById(idUsuario).isEmpty()) {
                usuarioService.save(new bot.models.Usuario(idUsuario, "MIEMBRO"));
            }
            if (usuarioService.findById(idAdminMod).isEmpty()) {
                usuarioService.save(new bot.models.Usuario(idAdminMod, "MOD"));
            }
            target.ban(0, TimeUnit.DAYS).reason(reason).queue(
                    success -> {
                        event.reply("Usuario baneado correctamente").setEphemeral(true).queue();
                        serviceFactory.getPenalizacionService().registrarBaneo(idUsuario, reason, idAdminMod);
                    },
                    error -> event.reply("No se pudo banear al usuario: " + error.getMessage()).setEphemeral(true)
                            .queue());
        } else {
            event.reply("No se encontró al usuario").setEphemeral(true).queue();
        }
    }
}