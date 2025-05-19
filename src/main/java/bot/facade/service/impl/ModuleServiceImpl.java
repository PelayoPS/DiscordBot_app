package bot.facade.service.impl;

import bot.commands.ModuleManager;
import bot.facade.dto.CommandDTO;
import bot.facade.dto.ModuleDTO;
import java.util.Map;
import bot.facade.service.ModuleService;
import bot.modules.CommandManager;
import bot.api.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ModuleServiceImpl implements ModuleService {
    private final ModuleManager moduleManager;
    // Descripciones legibles para cada módulo
    private static final Map<String, String> MODULE_DESCRIPTIONS = Map.of(
            "management", "Funciones de administración de roles y configuración",
            "moderation", "Herramientas de moderación de servidores",
            "user", "Funciones generales para todos los usuarios"
    );

    @Autowired
    public ModuleServiceImpl(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @Override
    public List<ModuleDTO> getAllModulesWithCommands() {
        List<ModuleDTO> moduleDTOs = new ArrayList<>();
        moduleManager.getModules().forEach((key, value) -> {
            if (value instanceof CommandManager manager) {
                List<CommandDTO> commandDTOs = new ArrayList<>();
                for (Command cmd : manager.getCommands()) {
                    commandDTOs.add(new CommandDTO(
                            cmd.getName(),
                            manager.isCommandEnabled()));
                }
                String descripcion = MODULE_DESCRIPTIONS.getOrDefault(key, manager.getClass().getSimpleName());
                ModuleDTO moduleDTO = new ModuleDTO(
                        key,
                        descripcion,
                        manager.isCommandEnabled(),
                        commandDTOs);
                moduleDTOs.add(moduleDTO);
            }
        });
        return moduleDTOs;
    }

    @Override
    public ModuleDTO enableModule(String nombre) {
        var module = moduleManager.getModules().get(nombre);
        if (module instanceof CommandManager manager) {
            manager.setCommandEnabled(true);
            List<CommandDTO> commandDTOs = new ArrayList<>();
            for (Command cmd : manager.getCommands()) {
                commandDTOs.add(new CommandDTO(cmd.getName(), manager.isCommandEnabled()));
            }
            String descripcion = MODULE_DESCRIPTIONS.getOrDefault(nombre, manager.getClass().getSimpleName());
            return new ModuleDTO(
                    nombre,
                    descripcion,
                    manager.isCommandEnabled(),
                    commandDTOs);
        }
        return null;
    }

    @Override
    public ModuleDTO disableModule(String nombre) {
        var module = moduleManager.getModules().get(nombre);
        if (module instanceof CommandManager manager) {
            manager.setCommandEnabled(false);
            List<CommandDTO> commandDTOs = new ArrayList<>();
            for (Command cmd : manager.getCommands()) {
                commandDTOs.add(new CommandDTO(cmd.getName(), manager.isCommandEnabled()));
            }
            String descripcion = MODULE_DESCRIPTIONS.getOrDefault(nombre, manager.getClass().getSimpleName());
            return new ModuleDTO(
                    nombre,
                    descripcion,
                    manager.isCommandEnabled(),
                    commandDTOs);
        }
        return null;
    }
}