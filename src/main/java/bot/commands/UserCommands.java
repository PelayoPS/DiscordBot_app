package bot.commands;

import java.util.ArrayList;
import java.util.List;

import bot.api.Command;
import bot.commands.user.Avatar;
import bot.events.EventListener;
import bot.modules.CommandManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Clase que maneja los comandos de usuario del bot.
 * Extiende de {@link EventListener} para manejar eventos de interacción de
 * comandos.
 */
public class UserCommands extends CommandManager {

    private List<Command> commands = new ArrayList<>();
    private boolean isCommandEnabled = true;

    /**
     * Constructor de la clase UserCommands.
     * 
     * Agrega los comandos de usuario a la lista de comandos.
     */
    public UserCommands() {
        commands.add(new Avatar());
    }

    /**
     * Obtiene los datos de comandos slash para todos los comandos gestionados.
     * 
     * @return Lista de SlashCommandData para cada comando
     */
    public List<SlashCommandData> getSlash() {
        List<SlashCommandData> slashCommands = new ArrayList<SlashCommandData>();
        for (Command command : commands) {
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
            for (Command command : commands) {
                if (event.getName().equals(command.getName())) {
                    command.execute(event);
                    return;
                }
            }
        }
    }

    @Override
    public void setCommandEnabled(boolean enabled) {
        this.isCommandEnabled = enabled;
    }

    @Override
    public boolean isCommandEnabled() {
        return this.isCommandEnabled;
    }

}