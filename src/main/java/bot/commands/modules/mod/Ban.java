package bot.commands.modules.mod;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class Ban {

    /**
     * Banea a un usuario.
     * 
     * @param event El evento de interacción del comando.
     */
    public static void banUser(SlashCommandInteractionEvent event) {
        // TODO Implementar el baneo de un usuario
    }

    /**
     * Devuelve la información del comando de slash.
     * 
     * @return SlashCommandData La información del comando de slash.
     */
    public static SlashCommandData getSlash() {
        SlashCommandData slash = Commands.slash("ban", "Banear a un usuario")
                .addOption(OptionType.USER, "usuario", "El usuario a banear", true)
                .addOption(OptionType.STRING, "razon", "La razón del baneo", false);
        return slash;
    }

}
