package bot.services.impl;

import bot.services.AIChatService;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación simple del servicio de chat IA.
 * Gestiona el historial de mensajes en hilos y simula la integración con IA.
 * 
 * @author PelayoPS
 */
public class AIChatServiceImpl implements AIChatService {
    private final Map<String, List<String>> historial = new HashMap<>();

    /**
     * Inicia un chat IA en un hilo, guardando el mensaje inicial en el historial.
     * 
     * @param thread Canal de hilo
     * @param user   Usuario de Discord
     * @param prompt Mensaje inicial para el chat IA
     */
    @Override
    public void iniciarChatIA(ThreadChannel thread, User user, String prompt) {
        historial.computeIfAbsent(thread.getId(), k -> new ArrayList<>()).add(user.getName() + ": " + prompt);
        // Aquí iría la integración real con IA si se requiere
    }

    /**
     * Obtiene el historial de mensajes de un hilo.
     * 
     * @param threadId ID del hilo
     * @return Lista de mensajes del historial
     */
    @Override
    public List<String> obtenerHistorial(String threadId) {
        return historial.getOrDefault(threadId, new ArrayList<>());
    }
}
