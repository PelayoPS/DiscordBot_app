package bot.commands.modules;

import java.util.ArrayList;
import java.util.List;

import bot.commands.ICommand;
import bot.events.EventListener;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class CommandManager extends EventListener {

    List<ICommand> commands = new ArrayList<ICommand>();

    public List<SlashCommandData> getSlash(){
        List<SlashCommandData> slashCommands = new ArrayList<SlashCommandData>();
        for (ICommand command : commands) {
            slashCommands.add(command.getSlash());
        }
        return slashCommands;
    }

    /**
     * Maneja la interacción de comandos de barra.
     * 
     * @param event El evento de interacción del comando.
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!isCommandEnabled()) {
            event.reply("Los comandos están deshabilitados").setEphemeral(true).queue();
            return;
        } else {
            String name = event.getName();
            // Busca el comando en la lista de comandos
            commands.stream().filter(command -> command.getName().equals(name)).findFirst()
                    .ifPresentOrElse(command -> command.execute(event),
                            () -> event.reply("Comando no reconocido").setEphemeral(true).queue());

        }
    }

}