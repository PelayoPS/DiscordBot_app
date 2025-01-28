package bot;

import bot.commands.ModuleManager;
import bot.commands.modules.ModCommandsextends;
import bot.commands.modules.ManageCommands;
import bot.commands.modules.UserCommands;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class Main {
    public static JDA jda;
    public static ModuleManager moduleManager;

    public static void main(String[] args) {
        try {
            JDABuilder builder = JDABuilder.createDefault("TU_TOKEN_DE_DISCORD");
            jda = builder.build();

            moduleManager = new ModuleManager();
            moduleManager.registerModule("mod", new ModCommandsextends());
            moduleManager.registerModule("manage", new ManageCommands());
            moduleManager.registerModule("user", new UserCommands());

            // Registrar los slash commands
            jda.updateCommands()
               .addCommands(
                   Commands.slash("ban", "Banear a un usuario").addOption(OptionType.USER, "usuario", "El usuario a banear", true),
                   Commands.slash("kick", "Expulsar a un usuario").addOption(OptionType.USER, "usuario", "El usuario a expulsar", true),
                   Commands.slash("mute", "Silenciar a un usuario").addOption(OptionType.USER, "usuario", "El usuario a silenciar", true)
                   // Agrega aquí más comandos según sea necesario
               ).queue();

            // Activar módulos por defecto
            moduleManager.enableModule("mod");
            moduleManager.enableModule("manage");
            moduleManager.enableModule("user");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}