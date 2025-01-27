package bot.commands;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

import bot.Main;

public class ModuleManager {
    private final Map<String, ListenerAdapter> modules = new HashMap<>();

    public void registerModule(String name, ListenerAdapter module) {
        modules.put(name, module);
    }

    public void enableModule(String name) {
        ListenerAdapter module = modules.get(name);
        if (module != null) {
            Main.jda.addEventListener(module);
        }
    }

    public void disableModule(String name) {
        ListenerAdapter module = modules.get(name);
        if (module != null) {
            Main.jda.removeEventListener(module);
        }
    }

    public boolean isModuleEnabled(String name) {
        ListenerAdapter module = modules.get(name);
        return module != null && Main.jda.getRegisteredListeners().contains(module);
    }
}