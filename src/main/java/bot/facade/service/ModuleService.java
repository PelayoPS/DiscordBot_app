
package bot.facade.service;

import bot.facade.dto.ModuleDTO;
import java.util.List;

public interface ModuleService {
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