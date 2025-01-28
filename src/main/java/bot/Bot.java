package bot;

import java.util.ArrayList;
import java.util.List;

import bot.commands.ModuleManager;
import bot.commands.modules.CommandManager;
import bot.commands.modules.ManageCommands;
import bot.commands.modules.ModCommands;
import bot.commands.modules.UserCommands;
import bot.events.EventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class Bot {

    public static JDA jda;
    public static ModuleManager moduleManager;

    /**
     * Inicia el bot.
     * 
     * Registra los módulos y los comandos de slash.
     * 
     * Activa los módulos por defecto.
     */
    public void start() {
        try {
            JDABuilder builder = JDABuilder.createDefault("TU_TOKEN_DE_DISCORD");
            jda = builder.build();

            moduleManager = new ModuleManager();
            moduleManager.registerModule("mod", new ModCommands());
            moduleManager.registerModule("manage", new ManageCommands());
            moduleManager.registerModule("user", new UserCommands());

            List<EventListener> modules = moduleManager.getModules().values().stream().toList();

            // Registrar los módulos como EventListener
            for (EventListener module : modules) {
                jda.addEventListener(module);
            }

            // Lista de SlashCommndData
            List<SlashCommandData> slashCommands = new ArrayList<SlashCommandData>();

            // Obtener los comandos de los módulos
            for (EventListener module : modules) {
                if (module instanceof CommandManager) {
                    slashCommands.addAll(((CommandManager) module).getSlash());
                }
            }

            // Registrar los slash commands
            jda.updateCommands()
                    .addCommands(
                            slashCommands)
                    .queue();

            // Activar módulos por defecto
            moduleManager.enableModule("mod");
            moduleManager.enableModule("manage");
            moduleManager.enableModule("user");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
