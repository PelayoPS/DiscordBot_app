package bot.services;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import java.util.List;

/**
 * Servicio para gestionar chats de IA en hilos de Discord.
 * Permite iniciar chats y consultar el historial de mensajes.
 * 
 * @author PelayoPS
 */
public interface AIChatService {
    /**
     * Inicia un chat IA en un hilo, guardando el mensaje inicial.
     * 
     * @param thread Canal de hilo
     * @param user   Usuario de Discord
     * @param prompt Mensaje inicial para el chat IA
     */
    void iniciarChatIA(ThreadChannel thread, User user, String prompt);

    /**
     * Obtiene el historial de mensajes de un hilo.
     * 
     * @param threadId ID del hilo
     * @return Lista de mensajes del historial
     */
    List<String> obtenerHistorial(String threadId);
}
