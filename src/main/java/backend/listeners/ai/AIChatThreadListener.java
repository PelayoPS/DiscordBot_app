package backend.listeners.ai;

import java.util.Map;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import backend.commands.user.AIChat;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Listener que maneja los mensajes dentro de los hilos creados por el comando
 * AIChat.
 * Detecta mensajes en los hilos y envía las respuestas utilizando la API de
 * Gemini.
 * 
 * @author PelayoPS
 */
public class AIChatThreadListener extends ListenerAdapter {

    private static final Logger LOGGER = Logger.getLogger(AIChatThreadListener.class.getName());

    /**
     * Método que se ejecuta cuando se recibe un mensaje en un hilo.
     * 
     * @param event El evento de mensaje recibido
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Verificar si el mensaje fue enviado en un hilo
        if (!event.isFromThread()) {
            return;
        }

        // Ignorar mensajes enviados por bots (incluido nuestro bot)
        if (event.getAuthor().isBot()) {
            return;
        }

        ThreadChannel threadChannel = event.getChannel().asThreadChannel();
        String threadId = threadChannel.getId();

        // Verificar si el hilo tiene una sesión de chat asociada
        Map<String, List<Map<String, Object>>> chatSessions = AIChat.getChatSessions();
        if (!chatSessions.containsKey(threadId)) {
            return; // No es un hilo de chat de IA
        }

        // Obtener el mensaje del usuario
        String userMessage = event.getMessage().getContentDisplay();

        // Mostrar indicador de "escribiendo..." mientras la IA procesa
        threadChannel.sendTyping().queue(); // Procesar la respuesta de forma asíncrona
        CompletableFuture.runAsync(() -> {
            try {
                // Enviar el mensaje a la API de Gemini usando el método en AIChat
                LOGGER.info("Enviando mensaje al hilo " + threadId + ": " + userMessage);

                // Enviar mensaje directamente a través de AIChat
                String aiResponse = AIChat.sendMessageToThread(threadId, userMessage);

                // Enviar la respuesta al hilo usando el método para fragmentar mensajes largos
                enviarRespuestaFragmentada(threadChannel, aiResponse);

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error al procesar mensaje de IA", e);
                threadChannel.sendMessage("Error al procesar tu mensaje: " + e.getMessage()).queue();
            }
        });
    }

    /**
     * Envía un mensaje fragmentándolo en partes si supera el límite de caracteres
     * de Discord.
     * 
     * @param canal   El canal donde enviar el mensaje
     * @param mensaje El mensaje a enviar
     */
    private void enviarRespuestaFragmentada(ThreadChannel canal, String mensaje) {
        // Límite de Discord es 2000 caracteres
        final int LIMITE_DISCORD = 1900; // Un poco menos para estar seguros

        // Si el mensaje es más corto que el límite, enviarlo directamente
        if (mensaje.length() <= LIMITE_DISCORD) {
            canal.sendMessage(mensaje).queue();
            return;
        }

        // Dividir el mensaje en fragmentos
        int inicio = 0;
        while (inicio < mensaje.length()) {
            int fin = Math.min(inicio + LIMITE_DISCORD, mensaje.length());

            // Intentar dividir en un salto de línea para que sea más legible
            if (fin < mensaje.length()) {
                int ultimoSalto = mensaje.lastIndexOf("\n", fin);
                if (ultimoSalto > inicio) {
                    fin = ultimoSalto + 1; // Incluir el salto de línea
                }
            }

            String fragmento = mensaje.substring(inicio, fin);
            canal.sendMessage(fragmento).queue();

            inicio = fin;
        }
    }
}
