package bot.commands.user;

import bot.api.Command;
import bot.controller.UserController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Comando para mostrar el avatar y opcionalmente el banner de un usuario en
 * Discord.
 * Permite obtener el avatar y el banner de cualquier usuario del servidor.
 * 
 * @author PelayoPS
 */
public class Avatar implements Command {
    private final UserController userController;

    /**
     * Constructor de la clase Avatar.
     * 
     * @param userController Controlador de usuario para obtener información de
     *                       usuarios
     */
    public Avatar(UserController userController) {
        this.userController = userController;
    }

    /**
     * Ejecuta la lógica del comando cuando es invocado por un usuario.
     * Muestra el avatar y opcionalmente el banner del usuario objetivo.
     * 
     * @param event El evento de interacción del comando
     */
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Obtener el usuario (si no se especifica, usar el autor del comando)
        User targetUser = event.getOption("usuario", event.getUser(), OptionMapping::getAsUser);
        boolean showBanner = event.getOption("banner", false, OptionMapping::getAsBoolean);

        // Usar el controlador para obtener la URL del avatar
        String avatarUrl = userController.obtenerAvatar(targetUser) + "?size=1024";
        MessageEmbed avatarEmbed = buildAvatarEmbed(avatarUrl, targetUser.getAsTag());

        if (showBanner) {
            // Realizar la obtención del banner de forma asíncrona
            event.deferReply().queue();
            targetUser.retrieveProfile().queue(
                    profile -> {
                        String bannerUrl = profile.getBannerUrl();
                        if (bannerUrl != null) {
                            MessageEmbed bannerEmbed = buildBannerEmbed(
                                    bannerUrl + "?size=2048",
                                    targetUser.getAsTag());
                            event.getHook().sendMessageEmbeds(avatarEmbed, bannerEmbed).queue();
                        } else {
                            MessageEmbed noBannerEmbed = buildBannerEmbed(
                                    "No tiene banner",
                                    targetUser.getAsTag());
                            event.getHook().sendMessageEmbeds(avatarEmbed, noBannerEmbed).queue();
                        }
                    },
                    error -> event.getHook().sendMessageEmbeds(avatarEmbed)
                            .addContent("No se pudo obtener el banner del usuario.")
                            .queue());
        } else {
            event.replyEmbeds(avatarEmbed).queue();
        }
    }

    /**
     * Construye el embed para mostrar el avatar.
     * 
     * @param avatarUrl URL del avatar
     * @param username  Nombre de usuario
     * @return MessageEmbed con el avatar
     */
    private MessageEmbed buildAvatarEmbed(String avatarUrl, String username) {
        return new EmbedBuilder()
                .setTitle("Avatar de " + username)
                .setImage(avatarUrl)
                .build();
    }

    /**
     * Construye el embed para mostrar el banner.
     * 
     * @param bannerUrl URL del banner o mensaje si no tiene banner
     * @param username  Nombre de usuario
     * @return MessageEmbed con el banner o mensaje alternativo
     */
    private MessageEmbed buildBannerEmbed(String bannerUrl, String username) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Banner de " + username);

        if (!"No tiene banner".equals(bannerUrl)) {
            builder.setImage(bannerUrl);
        } else {
            builder.setDescription("Este usuario no tiene banner");
        }

        return builder.build();
    }

    /**
     * Devuelve la definición del comando slash para su registro en Discord.
     * 
     * @return Los datos del comando slash
     */
    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("avatar", "Muestra el avatar y opcionalmente el banner de un usuario")
                .addOption(OptionType.USER, "usuario", "Usuario del que obtener el avatar (por defecto: tú)", false)
                .addOption(OptionType.BOOLEAN, "banner", "Mostrar también el banner del usuario", false);
    }

    /**
     * Devuelve el nombre del comando.
     * 
     * @return El nombre del comando
     */
    @Override
    public String getName() {
        return "avatar";
    }
}