package backend.facade.service;

import java.util.List;

import backend.facade.dto.ModuleDTO;

/**
 * Servicio para la gestión de módulos y comandos del bot.
 * 
 * @author PelayoPS
 */
public interface ModuleService {
    /**
     * Obtiene todos los módulos con sus comandos asociados.
     *
     * @return Lista de módulos con comandos
     */
    List<ModuleDTO> getAllModulesWithCommands();

    /**
     * Activa el módulo especificado por nombre.
     *
     * @param nombre Nombre del módulo
     * @return ModuleDTO actualizado, o null si no existe
     */
    ModuleDTO enableModule(String nombre);

    /**
     * Desactiva el módulo especificado por nombre.
     *
     * @param nombre Nombre del módulo
     * @return ModuleDTO actualizado, o null si no existe
     */
    ModuleDTO disableModule(String nombre);

    /**
     * Activa un comando individual de un módulo.
     *
     * @param nombreModulo  Nombre del módulo
     * @param nombreComando Nombre del comando
     * @return true si se activó correctamente, false si no existe
     */
    boolean enableCommand(String nombreModulo, String nombreComando);

    /**
     * Desactiva un comando individual de un módulo.
     *
     * @param nombreModulo  Nombre del módulo
     * @param nombreComando Nombre del comando
     * @return true si se desactivó correctamente, false si no existe
     */
    boolean disableCommand(String nombreModulo, String nombreComando);
}