package bot.modules.mod;

import java.util.ArrayList;
import java.util.List;

import bot.api.Command;
import bot.commands.moderation.Ban;
import bot.commands.moderation.HistoryCommand;
import bot.commands.moderation.Kick;
import bot.commands.moderation.Mute;
import bot.commands.moderation.PurgeCommand;
import bot.commands.moderation.TimeoutCommand;
import bot.commands.moderation.Unban;
import bot.commands.moderation.WarnCommand;
import bot.events.EventListener;
import bot.modules.CommandManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Clase que maneja los comandos de moderación del bot.
 * Extiende de {@link EventListener} para manejar eventos de interacción de
 * comandos.
 */
public class ModCommands extends CommandManager {

    /**
     * Constructor de la clase ModCommands.
     * 
     * Agrega los comandos de moderación a la lista de comandos.
     */
    public ModCommands() {
        commands.add(new Ban());
        commands.add(new HistoryCommand());
        commands.add(new Kick());
        commands.add(new Mute());
        commands.add(new PurgeCommand());
        commands.add(new TimeoutCommand());
        commands.add(new Unban());
        commands.add(new WarnCommand());
    }

}