package bot.commands.modules;

import java.util.ArrayList;
import java.util.List;

import bot.events.EventListener;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class ICommandManager extends EventListener {

    List<Class> commands = new ArrayList<Class>();

    public List<SlashCommandData> getSlash(){
        List<SlashCommandData> slashCommands = new ArrayList<SlashCommandData>();
        for (Class command : commands) {
            try {
                slashCommands.add((SlashCommandData) command.getMethod("getSlash").invoke(null));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return slashCommands;
    }

}
