package bot.commands.user;

import bot.api.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Clase que implementa el comando para mostrar el avatar de un usuario.
 */
public class Avatar implements Command {

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Obtener el usuario (si no se especifica, usar el autor del comando)
        User targetUser = event.getOption("usuario", event.getUser(), OptionMapping::getAsUser);
        boolean showBanner = event.getOption("banner", false, OptionMapping::getAsBoolean);

        // Construir el embed del avatar
        MessageEmbed avatarEmbed = buildAvatarEmbed(
                targetUser.getEffectiveAvatarUrl() + "?size=1024",
                targetUser.getAsTag());

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

    private MessageEmbed buildAvatarEmbed(String avatarUrl, String username) {
        return new EmbedBuilder()
                .setTitle("Avatar de " + username)
                .setImage(avatarUrl)
                .build();
    }

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

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("avatar", "Muestra el avatar y opcionalmente el banner de un usuario")
                .addOption(OptionType.USER, "usuario", "Usuario del que obtener el avatar (por defecto: tú)", false)
                .addOption(OptionType.BOOLEAN, "banner", "Mostrar también el banner del usuario", false);
    }

    @Override
    public String getName() {
        return "avatar";
    }
}