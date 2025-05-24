package backend.facade.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.api.Command;
import backend.commands.ModuleManager;
import backend.facade.dto.CommandDTO;
import backend.facade.dto.ModuleDTO;
import backend.facade.service.ModuleService;
import backend.modules.CommandManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del servicio de módulos para gestionar módulos y comandos.
 * 
 * @author PelayoPS
 */
@Service
public class ModuleServiceImpl implements ModuleService {
    private final ModuleManager moduleManager;
    private static final Map<String, String> MODULE_DESCRIPTIONS = Map.of(
            "management", "Funciones de administración de roles y configuración",
            "moderation", "Herramientas de moderación de servidores",
            "user", "Funciones generales para todos los usuarios");

    @Autowired
    public ModuleServiceImpl(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    /**
     * Obtiene todos los módulos con sus comandos asociados.
     *
     * @return Lista de módulos con comandos
     */
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

    /**
     * Habilita un módulo por nombre.
     *
     * @param nombre Nombre del módulo
     * @return DTO del módulo habilitado o null si no existe
     */
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

    /**
     * Deshabilita un módulo por nombre.
     *
     * @param nombre Nombre del módulo
     * @return DTO del módulo deshabilitado o null si no existe
     */
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

    /**
     * Habilita un comando específico dentro de un módulo.
     *
     * @param nombreModulo  Nombre del módulo
     * @param nombreComando Nombre del comando
     * @return true si se habilitó correctamente, false en caso contrario
     */
    @Override
    public boolean enableCommand(String nombreModulo, String nombreComando) {
        var module = moduleManager.getModules().get(nombreModulo);
        if (module instanceof CommandManager manager) {
            manager.setCommandEnabled(nombreComando, true);
            return true;
        }
        return false;
    }

    /**
     * Deshabilita un comando específico dentro de un módulo.
     *
     * @param nombreModulo  Nombre del módulo
     * @param nombreComando Nombre del comando
     * @return true si se deshabilitó correctamente, false en caso contrario
     */
    @Override
    public boolean disableCommand(String nombreModulo, String nombreComando) {
        var module = moduleManager.getModules().get(nombreModulo);
        if (module instanceof CommandManager manager) {
            manager.setCommandEnabled(nombreComando, false);
            return true;
        }
        return false;
    }
}