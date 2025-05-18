package bot.facade.service;

import bot.facade.dto.ModuleDTO;
import java.util.List;

public interface ModuleService {
    List<ModuleDTO> getAllModulesWithCommands();
}