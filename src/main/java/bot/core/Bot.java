package bot.core;

import bot.commands.ModuleManager;
import bot.commands.modules.CommandManager;
import bot.commands.modules.ManageCommands;
import bot.commands.modules.ModCommands;
import bot.commands.modules.UserCommands;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal del bot que delega responsabilidades a CommandRegistry y
 * EventRegistry.
 */
public class Bot {
    private final JDA jda;
    private final ModuleManager moduleManager;
    private final CommandRegistry commandRegistry;
    private final EventRegistry eventRegistry;

    public Bot(String token) {
        // Inicializar registros y managers
        this.commandRegistry = new CommandRegistry();
        this.eventRegistry = new EventRegistry();
        this.moduleManager = new ModuleManager();

        // Inicializar JDA
        try {
            this.jda = JDABuilder.build(token);
            // Inicializar y registrar módulos
            initializeModules();
            // Registrar comandos en Discord
            updateCommands();
        } catch (Exception e) {
            throw new RuntimeException("Error initializing bot", e);
        }
    }

    private void initializeModules() {
        // Registrar módulos básicos
        moduleManager.registerModule("management", new ManageCommands());
        moduleManager.registerModule("moderation", new ModCommands());
        moduleManager.registerModule("user", new UserCommands());

        // Registrar los módulos como event listeners en JDA
        for (var module : moduleManager.getModules().values()) {
            jda.addEventListener(module);
            eventRegistry.registerEventListener(module);
        }
    }

    private void updateCommands() {
        List<SlashCommandData> allCommands = new ArrayList<>();

        // Recolectar todos los comandos slash de todos los módulos
        moduleManager.getModules().values().stream()
                .filter(module -> module instanceof CommandManager)
                .map(module -> (CommandManager) module)
                .forEach(manager -> allCommands.addAll(manager.getSlash()));

        // Actualizar los comandos en Discord
        jda.updateCommands().addCommands(allCommands).queue(
                success -> System.out.println("Comandos registrados correctamente"),
                error -> System.err.println("Error al registrar los comandos: " + error.getMessage()));
    }

    public JDA getJda() {
        return jda;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public EventRegistry getEventRegistry() {
        return eventRegistry;
    }

    public void shutdown() {
        // Desconectar JDA
        jda.shutdown();
    }
}