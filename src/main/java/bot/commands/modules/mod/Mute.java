package bot.commands.modules.mod;

import bot.commands.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class Mute implements ICommand{

    private String name = "mute";

    /**
     * Silencia a un usuario.
     * 
     * @param event El evento de interacción del comando.
     */
    public void execute(SlashCommandInteractionEvent event) {
        // TODO Implementar el silenciamiento de un usuario
    }

    /**
     * Devuelve la información del comando de slash.
     * 
     * @return SlashCommandData La información del comando de slash.
     */
    public SlashCommandData getSlash() {
        SlashCommandData slash = Commands.slash("mute", "Silencia a un usuario")
                .addOption(OptionType.USER, "usuario", "El usuario a silenciar", true)
                .addOption(OptionType.STRING, "razon", "La razón del silencio", false);
        return slash;
    }

    public String getName() {
        return name;
    }

}
