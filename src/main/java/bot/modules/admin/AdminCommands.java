package bot.modules.admin;

import java.util.ArrayList;
import java.util.List;

import bot.api.Command;
import bot.commands.admin.CreateRole;
import bot.commands.admin.DeleteRole;
import bot.events.EventListener;
import bot.modules.CommandManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Clase que maneja los comandos administrativos del bot.
 * Extiende de {@link EventListener} para manejar eventos de interacción de
 * comandos.
 */
public class AdminCommands extends CommandManager {

    /**
     * Constructor de la clase AdminCommands.
     * 
     * Agrega los comandos administrativos a la lista de comandos.
     */
    public AdminCommands() {
        commands.add(new CreateRole());
        commands.add(new DeleteRole());
    }

}