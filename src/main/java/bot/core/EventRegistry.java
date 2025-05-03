package bot.core;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.hooks.EventListener;

/**
 * Clase para gestionar el registro de eventos.
 * Permite registrar y obtener todos los listeners de eventos del bot.
 * 
 * @author PelayoPS
 */
public class EventRegistry {

    private final List<EventListener> eventListeners = new ArrayList<>();

    /**
     * Registra un listener de eventos.
     * 
     * @param listener El listener a registrar.
     */
    public void registerEventListener(EventListener listener) {
        eventListeners.add(listener);
    }

    /**
     * Obtiene todos los listeners registrados.
     * 
     * @return Lista de listeners registrados.
     */
    public List<EventListener> getEventListeners() {
        return eventListeners;
    }
}