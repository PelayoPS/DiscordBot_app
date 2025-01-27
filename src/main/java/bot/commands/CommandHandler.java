package bot.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler extends ListenerAdapter {
    private final Map<String, Command> commands = new HashMap<>();

    public CommandHandler() {
        registerCommands();
    }

    private void registerCommands() {
        // Aquí se pueden registrar los comandos
        // Ejemplo: commands.put("comando", new MiComando());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" ");
        if (args.length > 0) {
            Command command = commands.get(args[0]);
            if (command != null) {
                //command.execute();
            }
        }
    }
}