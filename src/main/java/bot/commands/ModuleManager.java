package bot.commands;

import java.util.HashMap;
import java.util.Map;
import bot.events.EventListener;

/**
 * Clase que maneja los módulos del bot.
 * Un módulo es un conjunto de comandos relacionados.
 */
public class ModuleManager {
    private final Map<String, EventListener> modules = new HashMap<>();

    /**
     * Registra un módulo en el gestor.
     * 
     * @param name   Nombre del módulo
     * @param module Instancia del módulo
     */
    public void registerModule(String name, EventListener module) {
        modules.put(name, module);
    }

    /**
     * Activa un módulo.
     * 
     * @param name Nombre del módulo a activar
     */
    public void enableModule(String name) {
        EventListener module = modules.get(name);
        if (module != null) {
            module.setCommandEnabled(true);
        }
    }

    /**
     * Desactiva un módulo.
     * 
     * @param name Nombre del módulo a desactivar
     */
    public void disableModule(String name) {
        EventListener module = modules.get(name);
        if (module != null) {
            module.setCommandEnabled(false);
        }
    }

    /**
     * Comprueba si un módulo está activado.
     * 
     * @param name Nombre del módulo
     * @return true si el módulo está activado, false en caso contrario
     */
    public boolean isModuleEnabled(String name) {
        EventListener module = modules.get(name);
        return module != null && module.isCommandEnabled();
    }

    /**
     * Devuelve todos los módulos registrados.
     * 
     * @return Mapa con los módulos
     */
    public Map<String, EventListener> getModules() {
        return modules;
    }
}