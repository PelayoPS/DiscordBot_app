package bot.modules.mod;

import bot.commands.moderation.Ban;
import bot.commands.moderation.HistoryCommand;
import bot.commands.moderation.Kick;
import bot.commands.moderation.Mute;
import bot.commands.moderation.PurgeCommand;
import bot.commands.moderation.TimeoutCommand;
import bot.commands.moderation.Unban;
import bot.commands.moderation.WarnCommand;
import bot.modules.CommandManager;
import bot.facade.BotFacade;
import bot.core.ServiceFactory;

/**
 * Clase que maneja los comandos de moderación del bot.
 * Extiende de {@link CommandManager} para manejar eventos de interacción de
 * comandos.
 * 
 * @author PelayoPS
 */
public class ModCommands extends CommandManager {

    /**
     * Constructor de la clase ModCommands.
     * 
     * Agrega los comandos de moderación a la lista de comandos.
     * 
     * @param botFacade      Fachada principal del bot
     * @param serviceFactory Fábrica de servicios para los comandos
     */
    public ModCommands(BotFacade botFacade, ServiceFactory serviceFactory) {
        commands.add(new Ban(botFacade, serviceFactory));
        commands.add(new HistoryCommand(botFacade));
        commands.add(new Kick(botFacade, serviceFactory));
        commands.add(new Mute(botFacade, serviceFactory));
        commands.add(new PurgeCommand(botFacade));
        commands.add(new TimeoutCommand(botFacade, serviceFactory));
        commands.add(new Unban(botFacade));
        commands.add(new WarnCommand(botFacade, serviceFactory));
    }

}