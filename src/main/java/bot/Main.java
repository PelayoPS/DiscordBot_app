package bot;

import bot.events.EventListener;
import net.dv8tion.jda.api.JDABuilder;


public class Main {
    public static void main(String[] args) {
        try {
            JDABuilder builder = JDABuilder.createDefault("TU_TOKEN_DE_DISCORD");
            builder.addEventListeners(new EventListener());
            builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}