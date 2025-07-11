package backend.commands.moderation;

import java.time.Duration;

import backend.api.Command;
import backend.core.ServiceFactory;
import backend.facade.BotFacade;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.entities.Member;

/**
 * Comando para silenciar a un usuario en el servidor de Discord.
 * Permite a los usuarios con permisos adecuados silenciar miembros
 * especificando usuario, tiempo y razón.
 * 
 * @author PelayoPS
 */
public class Mute implements Command {

    private final BotFacade botFacade;
    private final ServiceFactory serviceFactory;
    private String name = "mute";

    /**
     * Constructor de la clase Mute.
     * 
     * @param botFacade      Fachada para operaciones del bot
     * @param serviceFactory Fábrica de servicios para acceso a datos
     */
    public Mute(BotFacade botFacade, ServiceFactory serviceFactory) {
        this.botFacade = botFacade;
        this.serviceFactory = serviceFactory;
    }

    /**
     * Ejecuta la lógica del comando cuando es invocado por un usuario.
     * Verifica permisos y solicita el silencio del usuario.
     * 
     * @param event El evento de interacción del comando
     */
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("No tienes permisos para usar este comando").setEphemeral(true).queue();
            return;
        }

        Member member = event.getOption("usuario").getAsMember();
        if (member == null) {
            event.reply("El usuario no está en el servidor o no se puede encontrar.").setEphemeral(true).queue();
            return;
        }
        User user = member.getUser();
        String razon = event.getOption("razon") != null ? event.getOption("razon").getAsString() : "No especificada";
        Duration tiempo = parseTime(event.getOption("tiempo").getAsString());

        Long idUsuario = Long.valueOf(user.getId());
        Long idAdminMod = Long.valueOf(event.getUser().getId());
        var usuarioService = serviceFactory.getUsuarioService();
        if (usuarioService.findById(idUsuario).isEmpty()) {
            usuarioService.save(new backend.models.Usuario(idUsuario, "MIEMBRO"));
        }
        if (usuarioService.findById(idAdminMod).isEmpty()) {
            usuarioService.save(new backend.models.Usuario(idAdminMod, "MOD"));
        }
        botFacade.muteUser(event.getGuild().getId(), user.getId(), razon, tiempo);

        if (member.hasPermission(Permission.ADMINISTRATOR) || member.isOwner()) {
            event.reply("No puedes silenciar a un administrador o al dueño del servidor.").setEphemeral(true).queue();
            return;
        }
        if (!event.getGuild().getSelfMember().canInteract(member)) {
            event.reply("No puedo silenciar a este usuario porque su rol es igual o superior al mío.").setEphemeral(true).queue();
            return;
        }

        member.timeoutFor(tiempo).reason(razon).queue(
                success -> {
                    event.reply("Usuario silenciado correctamente").setEphemeral(true).queue();
                    serviceFactory.getPenalizacionService().registrarMute(idUsuario, razon, tiempo, idAdminMod);
                },
                error -> {
                    String errorMsg = error.getMessage();
                    event.reply("Error al silenciar al usuario: " + errorMsg).setEphemeral(true).queue();
                }
        );
    }

    /**
     * Devuelve la información del comando de slash.
     * 
     * @return SlashCommandData La información del comando de slash.
     */
    @Override
    public SlashCommandData getSlash() {
        SlashCommandData slash = Commands.slash("mute", "Silencia a un usuario")
                .addOption(OptionType.USER, "usuario", "El usuario a silenciar", true)
                .addOption(OptionType.STRING, "tiempo", "El tiempo del silencio(60s, 5m, 10m, 1h, 2h, 1d, 7d)", true)
                .addOption(OptionType.STRING, "razon", "La razón del silencio", false);
        return slash;
    }

    /**
     * Parsea el tiempo del silencio.
     * 
     * @param time Cadena de tiempo (ej: 60s, 5m, 1h, etc.)
     * @return Duration Duración del silencio
     */
    private Duration parseTime(String time) {
        switch (time) {
            case "60s":
                return Duration.ofSeconds(60);
            case "5m":
                return Duration.ofMinutes(5);
            case "10m":
                return Duration.ofMinutes(10);
            case "1h":
                return Duration.ofHours(1);
            case "2h":
                return Duration.ofHours(2);
            case "1d":
                return Duration.ofDays(1);
            case "7d":
                return Duration.ofDays(7);
            default:
                return Duration.ofSeconds(60);
        }
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