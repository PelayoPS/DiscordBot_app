package bot.modules;

import java.util.ArrayList;
import java.util.List;

import bot.api.Command;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Clase abstracta base para todos los gestores de comandos.
 * Proporciona métodos para registrar, obtener y ejecutar comandos, así como
 * gestionar su estado.
 * 
 * @author PelayoPS
 */
public abstract class CommandManager implements EventListener {
    protected List<Command> commands = new ArrayList<>();
    private boolean isCommandEnabled = true;

    /**
     * Obtiene la lista de comandos slash registrados.
     * 
     * @return Lista de SlashCommandData
     */
    public List<SlashCommandData> getSlash() {
        List<SlashCommandData> slashCommands = new ArrayList<>();
        for (Command command : commands) {
            slashCommands.add(command.getSlash());
        }
        return slashCommands;
    }

    /**
     * Obtiene la lista de comandos registrados.
     * 
     * @return Lista de comandos
     */
    public List<Command> getCommands() {
        return new ArrayList<>(commands);
    }

    /**
     * Añade un comando al gestor.
     * 
     * @param command Comando a añadir
     */
    public void addCommand(Command command) {
        commands.add(command);
    }

    /**
     * Maneja la interacción de un comando slash.
     * 
     * @param event Evento de interacción de comando slash
     */
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // Verificar si los comandos están habilitados
        if (!isCommandEnabled()) {
            event.reply("Los comandos están deshabilitados").setEphemeral(true).queue();
            return;
        }

        // Buscar y ejecutar el comando
        commands.stream()
                .filter(cmd -> event.getName().equals(cmd.getName()))
                .findFirst().ifPresent(cmd -> {
                    cmd.execute(event);
                });
    }

    /**
     * Habilita o deshabilita todos los comandos gestionados.
     * 
     * @param enabled true para habilitar, false para deshabilitar
     */
    public void setCommandEnabled(boolean enabled) {
        this.isCommandEnabled = enabled;
    }

    /**
     * Indica si los comandos están habilitados.
     * 
     * @return true si están habilitados, false si no
     */
    public boolean isCommandEnabled() {
        return this.isCommandEnabled;
    }

    /**
     * Maneja eventos genéricos de JDA y enruta los eventos de slash command.
     * 
     * @param event Evento genérico de JDA
     */
    @Override
    public void onEvent(GenericEvent event) {
        if (event instanceof SlashCommandInteractionEvent slashEvent) {
            onSlashCommandInteraction(slashEvent);
        }
        // Puedes dejarlo vacío o enrutar eventos según tu lógica
    }
}
