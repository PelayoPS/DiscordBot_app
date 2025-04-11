package bot.listeners.ai;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ChatSession;

import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchivedEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AIChatListener extends ListenerAdapter {

    private final Map<String, ChatSession> chatSessions;
    private static final Logger LOGGER = Logger.getLogger(AIChatListener.class.getName());

    /**
     * Constructor de AIChatListener.
     * 
     * @param chatSessions Mapa de sesiones de chat activas
     */
    public AIChatListener(Map<String, ChatSession> chatSessions) {
        this.chatSessions = chatSessions;
        LOGGER.info("AIChatListener inicializado con " + chatSessions.size() + " sesiones activas.");
    }

    /**
     * Manejar la recepción de mensajes en hilos de chat IA.
     * 
     * @param event Evento de mensaje recibido
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Ignorar bots para evitar bucles
        if (event.getAuthor().isBot())
            return;

        // Verificar que el canal es un hilo (thread)
        if (!event.getChannelType().isThread())
            return;

        // Comprobar si el mensaje es de un hilo que tenemos registrado
        String threadId = event.getChannel().getId();
        ChatSession chatSession = chatSessions.get(threadId);

        if (chatSession != null) {
            // Es un mensaje en un hilo de chat IA
            String userMessage = event.getMessage().getContentRaw();
            LOGGER.info("Mensaje recibido en hilo de IA: " + threadId + " - " + userMessage);

            // Indicar que el bot está escribiendo
            event.getChannel().sendTyping().queue();

            // Procesar mensaje asíncronamente
            CompletableFuture.runAsync(() -> {
                try {
                    // Enviar mensaje a Gemini y obtener respuesta
                    GenerateContentResponse response = chatSession.sendMessage(userMessage);

                    if (response != null && !response.getCandidatesList().isEmpty()) {
                        String aiResponse = response.getCandidatesList().get(0).getContent().getPartsList()
                                .get(0).getText();

                        // Enviar respuesta al hilo
                        event.getChannel().sendMessage(aiResponse).queue(
                                success -> LOGGER.info("Respuesta enviada correctamente al hilo " + threadId),
                                error -> LOGGER.log(Level.SEVERE, "Error al enviar respuesta: " + error.getMessage(),
                                        error));
                    } else {
                        event.getChannel().sendMessage(
                                "Lo siento, no pude generar una respuesta. Por favor intenta reformular tu pregunta.")
                                .queue();
                        LOGGER.warning("Respuesta vacía de la API de Gemini");
                    }
                } catch (Exception e) {
                    event.getChannel().sendMessage("Error al procesar tu mensaje: " + e.getMessage()).queue();
                    LOGGER.log(Level.SEVERE, "Error procesando mensaje en hilo " + threadId, e);
                }
            });
        }
    }

    /**
     * Manejar la actualización de un canal (hilo) y cerrar la sesión de chat
     * correspondiente si se archiva.
     * 
     * @param event Evento de actualización de canal
     */
    @Override
    public void onChannelUpdateArchived(ChannelUpdateArchivedEvent event) {
        if (event.getChannelType().isThread() && event.getNewValue()) {
            String threadId = event.getChannel().getId();
            closeSession(threadId, "hilo archivado");
        }
    }

    /**
     * Manejar la eliminación de canales (hilos) y cerrar la sesión de chat
     * correspondiente.
     * 
     * @param event Evento de eliminación de canal
     */
    @Override
    public void onChannelDelete(ChannelDeleteEvent event) {
        if (event.getChannelType().isThread()) {
            String threadId = event.getChannel().getId();
            closeSession(threadId, "hilo eliminado");
        }
    }

    /**
     * Cierra la sesión de chat para un hilo específico.
     * 
     * @param threadId ID del hilo
     * @param reason   Razón para cerrar la sesión
     */
    public void closeSession(String threadId, String reason) {
        ChatSession session = chatSessions.remove(threadId);
        if (session != null) {
            LOGGER.info("Sesión de chat cerrada para el hilo: " + threadId + " (Razón: " + reason + ")");
        }
    }
}
