package bot.commands.modules;

import bot.api.Command;
import bot.commands.moderation.*;

/**
 * Clase que maneja los comandos de moderación del bot.
 */
public class ModCommands extends CommandManager {

    public ModCommands() {
        commands.add(new Ban());
        commands.add(new Kick());
        commands.add(new Mute());
    }

    @Override
    public boolean supportsCommand(Command command) {
        return command instanceof Ban ||
                command instanceof Kick ||
                command instanceof Mute;
    }
}