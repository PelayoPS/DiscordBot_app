package bot.events;


import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Manejar el evento de mensaje recibido
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        // Manejar el evento de reacción añadida
    }
}