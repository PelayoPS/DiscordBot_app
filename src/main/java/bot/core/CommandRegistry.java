package bot.core;

import java.util.ArrayList;
import java.util.List;

import bot.api.Command;

/**
 * Clase para gestionar el registro de comandos.
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