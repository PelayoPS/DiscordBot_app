package backend.modules.mod;

import backend.commands.moderation.Ban;
import backend.commands.moderation.HistoryCommand;
import backend.commands.moderation.Kick;
import backend.commands.moderation.Mute;
import backend.commands.moderation.PurgeCommand;
import backend.commands.moderation.TimeoutCommand;
import backend.commands.moderation.Unban;
import backend.commands.moderation.WarnCommand;
import backend.core.ServiceFactory;
import backend.facade.BotFacade;
import backend.modules.CommandManager;

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