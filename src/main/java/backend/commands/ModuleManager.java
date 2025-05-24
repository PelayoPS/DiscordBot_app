package backend.commands;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.hooks.EventListener;

/**
 * Clase que maneja los módulos del bot.
 * Un módulo es un conjunto de comandos relacionados.
 * 
 * @author PelayoPS
 */
@Component
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
     * Devuelve todos los módulos registrados.
     * 
     * @return Mapa con los módulos
     */
    public Map<String, EventListener> getModules() {
        return modules;
    }

}