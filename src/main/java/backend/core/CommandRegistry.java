package backend.core;

import java.util.ArrayList;
import java.util.List;

import backend.api.Command;

/**
 * Clase para gestionar el registro de comandos.
 * Permite registrar y obtener todos los comandos disponibles en el bot.
 * 
 * @author PelayoPS
 */
public class CommandRegistry {

    private final List<Command> commands = new ArrayList<>();

    /**
     * Registra un comando.
     *
     * @param command El comando a registrar.
     */
    public void registerCommand(Command command) {
        commands.add(command);
    }

    /**
     * Obtiene todos los comandos registrados.
     *
     * @return Lista de comandos registrados.
     */
    public List<Command> getCommands() {
        return new ArrayList<>(commands);
    }
}