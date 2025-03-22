package bot.commands.modules;

import bot.api.Command;
import bot.commands.moderation.*;

public class ModerationModule extends CommandManager {
    public ModerationModule() {
        // Registrar los comandos de moderación
        addCommand(new Kick());
        addCommand(new Mute());
        addCommand(new Ban());
        addCommand(new Unban());
        addCommand(new TimeoutCommand());
        addCommand(new WarnCommand());
        addCommand(new HistoryCommand());
        addCommand(new PurgeCommand());

    }

    @Override
    public boolean supportsCommand(Command command) {
        return command.getClass().getPackage().getName().contains("moderation");
    }

    @Override
    public boolean isCommandEnabled() {
        return true;
    }
}
