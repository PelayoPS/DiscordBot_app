package bot.commands.modules.mod;

import bot.commands.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class Ban implements ICommand {

    private String name = "ban";

    /**
     * Banea a un usuario.
     * 
     * @param event El evento de interacción del comando.
     */
    public void execute(SlashCommandInteractionEvent event) {
        // TODO Implementar el baneo de un usuario
    }

    /**
     * Devuelve la información del comando de slash.
     * 
     * @return SlashCommandData La información del comando de slash.
     */
    public SlashCommandData getSlash() {
        SlashCommandData slash = Commands.slash("ban", "Banear a un usuario")
                .addOption(OptionType.USER, "usuario", "El usuario a banear", true)
                .addOption(OptionType.STRING, "razon", "La razón del baneo", false);
        return slash;
    }

    public String getName() {
        return name;
    }

}
