package bot.facade.service.impl;

import bot.commands.ModuleManager;
import bot.facade.dto.CommandDTO;
import bot.facade.dto.ModuleDTO;
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

    @Autowired
    public ModuleServiceImpl(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @Override
    public List<ModuleDTO> getAllModulesWithCommands() {
        List<ModuleDTO> moduleDTOs = new ArrayList<>();
        // map to list of modules
        moduleManager.getModules().forEach((key, value) -> {
            if (value instanceof CommandManager) {
                CommandManager manager = (CommandManager) value;
                List<CommandDTO> commandDTOs = new ArrayList<>();
                for (Command cmd : manager.getCommands()) {
                    commandDTOs.add(new CommandDTO(
                            cmd.getName(),
                            manager.isCommandEnabled()));
                }
                ModuleDTO moduleDTO = new ModuleDTO(
                        key,
                        manager.getClass().getSimpleName(),
                        manager.isCommandEnabled(),
                        commandDTOs);
                moduleDTOs.add(moduleDTO);
            }
        });
        return moduleDTOs;
    }
}