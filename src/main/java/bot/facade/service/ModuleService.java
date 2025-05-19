package bot.facade.service;

import bot.facade.dto.ModuleDTO;
import java.util.List;

public interface ModuleService {
    List<ModuleDTO> getAllModulesWithCommands();

    /**
     * Activa el módulo especificado por nombre.
     * @param nombre Nombre del módulo
     * @return ModuleDTO actualizado, o null si no existe
     */
    ModuleDTO enableModule(String nombre);

    /**
     * Desactiva el módulo especificado por nombre.
     * @param nombre Nombre del módulo
     * @return ModuleDTO actualizado, o null si no existe
     */
    ModuleDTO disableModule(String nombre);
}