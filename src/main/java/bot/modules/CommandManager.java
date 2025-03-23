package bot.modules;

import java.util.ArrayList;
import java.util.List;

import bot.api.Command;
import bot.events.EventListener;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Clase abstracta base para todos los gestores de comandos.
 */
public abstract class CommandManager extends EventListener {
    protected List<Command> commands = new ArrayList<>();
    private boolean isCommandEnabled = true;

    public List<SlashCommandData> getSlash() {
        List<SlashCommandData> slashCommands = new ArrayList<>();
        for (Command command : commands) {
            slashCommands.add(command.getSlash());
        }
        return slashCommands;
    }

    public List<Command> getCommands() {
        return new ArrayList<>(commands);
    }

    public void addCommand(Command command) {
        commands.add(command);
    }

    @Override
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

    @Override
    public void setCommandEnabled(boolean enabled) {
        this.isCommandEnabled = enabled;
    }

    @Override
    public boolean isCommandEnabled() {
        return this.isCommandEnabled;
    }
}
