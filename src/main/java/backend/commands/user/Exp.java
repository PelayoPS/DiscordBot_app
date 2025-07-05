package backend.commands.user;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;
import java.util.Optional;

import backend.api.Command;
import backend.models.Usuario;
import backend.services.UsuarioService;

/**
 * Comando para mostrar el nivel y la experiencia de un usuario.
 * 
 * @author PelayoPS
 */
public class Exp implements Command {
    private final UsuarioService usuarioService;

    /**
     * Constructor de la clase Exp.
     * 
     * @param usuarioService Servicio de usuario para consultar datos
     */
    public Exp(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Ejecuta la lógica del comando cuando es invocado por un usuario.
     * Muestra el nivel y la experiencia actual del usuario.
     * 
     * @param event El evento de interacción del comando
     */
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        User targetUser = event.getOption("usuario", event.getUser(), OptionMapping::getAsUser);
        Long idUsuario = targetUser.getIdLong();

        Optional<Usuario> usuarioOpt = usuarioService.findById(idUsuario);

        MessageEmbed embed;
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            embed = buildExpEmbed(targetUser, usuario.getNivel(), usuario.getPuntosXp());
        } else {
            embed = buildExpEmbed(targetUser, 1, 0);
        }

        event.replyEmbeds(embed).queue();
    }

    /**
     * Construye el embed para mostrar el nivel y la experiencia.
     * 
     * @param user   Usuario de Discord
     * @param nivel  Nivel actual
     * @param xp     Puntos de experiencia actuales
     * @return MessageEmbed con la información de experiencia
     */
    private MessageEmbed buildExpEmbed(User user, int nivel, int xp) {
        return new EmbedBuilder()
                .setTitle("Experiencia de " + user.getAsTag())
                .setColor(Color.ORANGE)
                .setThumbnail(user.getEffectiveAvatarUrl())
                .addField("Nivel", String.valueOf(nivel), true)
                .addField("Puntos de experiencia", String.valueOf(xp), true)
                .setFooter("Sistema de experiencia global", null)
                .build();
    }

    /**
     * Devuelve la definición del comando slash para su registro en Discord.
     * 
     * @return Los datos del comando slash
     */
    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("exp", "Muestra el nivel y puntos de experiencia de un usuario (por defecto: tú)")
                .addOption(OptionType.USER, "usuario", "Usuario del que obtener la experiencia (por defecto: tú)", false);
    }

    /**
     * Devuelve el nombre del comando.
     * 
     * @return El nombre del comando
     */
    @Override
    public String getName() {
        return "exp";
    }
}
