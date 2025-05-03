package bot.commands.moderation;

import bot.api.Command;
import bot.facade.BotFacade;
import bot.core.ServiceFactory;
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
     * Expulsa a un usuario.
     * 
     * @param event El evento de interacción del comando.
     */
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.KICK_MEMBERS)) {
            event.reply("No tienes permisos para expulsar usuarios").setEphemeral(true).queue();
            return;
        }

        // Obtiene el usuario a expulsar
        User user = event.getOption("usuario").getAsUser();

        // Obtiene la razón de la expulsión
        String razon = event.getOption("razon") != null ? event.getOption("razon").getAsString() : "No especificada";

        // Usar la fachada para expulsar
        botFacade.kickUser(event.getGuild().getId(), user.getId(), razon);
        Long idUsuario = Long.valueOf(user.getId());
        Long idAdminMod = Long.valueOf(event.getUser().getId());
        // Registrar usuario si no existe
        var usuarioService = serviceFactory.getUsuarioService();
        if (usuarioService.findById(idUsuario).isEmpty()) {
            usuarioService.save(new bot.models.Usuario(idUsuario, "MIEMBRO"));
        }
        // Registrar admin/mod si no existe
        if (usuarioService.findById(idAdminMod).isEmpty()) {
            usuarioService.save(new bot.models.Usuario(idAdminMod, "MOD"));
        }
        event.getGuild().kick(user).reason(razon).queue(
                success -> {
                    event.reply("Usuario expulsado correctamente").setEphemeral(true).queue();
                    // Guardar penalización en la base de datos
                    serviceFactory.getPenalizacionService().registrarExpulsion(idUsuario, razon, idAdminMod);
                });
    }

    /**
     * Devuelve la información del comando de slash.
     * 
     * @return SlashCommandData La información del comando de slash.
     */
    public SlashCommandData getSlash() {
        SlashCommandData slash = Commands.slash("kick", "Expulsa a un usuario")
                .addOption(OptionType.USER, "usuario", "El usuario a expulsar", true)
                .addOption(OptionType.STRING, "razon", "La razón de la expulsión", false);
        return slash;
    }

    /**
     * Devuelve el nombre del comando.
     * 
     * @return El nombre del comando
     */
    public String getName() {
        return name;
    }

}