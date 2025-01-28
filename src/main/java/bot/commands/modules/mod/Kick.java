package bot.commands.modules.mod;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class Kick {

    /**
     * Expulsa a un usuario.
     * 
     * @param event El evento de interacción del comando.
     */
    public static void kickUser(SlashCommandInteractionEvent event) {
        // TODO Implementar la expulsión de un usuario
    }

    /**
     * Devuelve la información del comando de slash.
     * 
     * @return SlashCommandData La información del comando de slash.
     */
    public static SlashCommandData getSlash() {
        SlashCommandData slash = Commands.slash("kick", "Expulsa a un usuario")
                .addOption(OptionType.USER, "usuario", "El usuario a expulsar", true)
                .addOption(OptionType.STRING, "razon", "La razón de la expulsión", false);
        return slash;
    }

}
