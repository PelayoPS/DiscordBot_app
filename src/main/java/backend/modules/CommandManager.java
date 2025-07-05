package backend.modules;

import java.util.ArrayList;
import java.util.List;

import backend.api.Command;
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
    // Estado individual de cada comando (por nombre)
    private final java.util.Map<String, Boolean> commandStates = new java.util.HashMap<>();

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
        // Por defecto, cada comando está activo al añadirse
        commandStates.put(command.getName(), true);
    }

    /**
     * Maneja la interacción de un comando slash.
     * 
     * @param event Evento de interacción de comando slash
     */
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // Verificar si los comandos del módulo están habilitados
        if (!isCommandEnabled()) {
            event.reply("Los comandos están deshabilitados").setEphemeral(true).queue();
            return;
        }

        // Buscar el comando y comprobar si está habilitado individualmente
        commands.stream()
                .filter(cmd -> event.getName().equals(cmd.getName()))
                .findFirst().ifPresent(cmd -> {
                    if (!isCommandEnabled(cmd.getName())) {
                        event.reply("Este comando está desactivado").setEphemeral(true).queue();
                        return;
                    }
                    cmd.execute(event);
                });
    }

    /**
     * Habilita o deshabilita un comando individual por nombre.
     * @param commandName Nombre del comando
     * @param enabled true para habilitar, false para deshabilitar
     */
    public void setCommandEnabled(String commandName, boolean enabled) {
        // Siempre actualiza el estado, aunque el comando no esté en el mapa 
        commandStates.put(commandName, enabled);
    }

    /**
     * Indica si un comando individual está habilitado.
     * @param commandName Nombre del comando
     * @return true si está habilitado, false si no (o si no existe)
     */
    public boolean isCommandEnabled(String commandName) {
        return commandStates.getOrDefault(commandName, true);
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
    }
}
