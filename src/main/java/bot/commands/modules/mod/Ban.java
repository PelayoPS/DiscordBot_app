package bot.commands.modules.mod;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import bot.commands.ICommand;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class Ban implements ICommand {

    private String name = "ban";

    /**
     * Banea a un usuario.
     * 
     * @param event El evento de interacción del comando.
     */
    public void execute(SlashCommandInteractionEvent event) {
        // Obtiene el usuario a banear
        User user = event.getOption("usuario").getAsUser();

        // Obtiene la razón del baneo
        String razon = event.getOption("razon") != null ? event.getOption("razon").getAsString() : "No especificada";

        // Obtiene los días de historial a borrar	
        int tiempo = Integer.parseInt(event.getOption("borrar_mensajes") != null ? event.getOption("borrar_mensajes").getAsString() : "0");

        // Banea al usuario
        event.getGuild().ban(user, tiempo, TimeUnit.HOURS).reason(razon).queue();

        // Responde al usuario
        event.reply("Usuario baneado correctamente").setEphemeral(true).queue();
    }

    /**
     * Devuelve la información del comando de slash.
     *
     * @return SlashCommandData La información del comando de slash.
     */
    public SlashCommandData getSlash() {
        SlashCommandData slash = Commands.slash("ban", "Banear a un usuario")
                .addOption(OptionType.USER, "usuario", "El usuario a banear", true)
                .addOption(OptionType.STRING, "borrar_mensajes", "Selecciona cuánto historial borrar", false, true)
                .addOption(OptionType.STRING, "razon", "La razón del baneo", false);
        return slash;
    }

    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        // Verifica que el comando sea "ban" y que la opción en foco sea
        // "borrar_mensajes"
        if (event.getName().equals("ban") && event.getFocusedOption().getName().equals("borrar_mensajes")) {
            // Lista de opciones predefinidas
            Map<String, String> opciones = Map.of(
                    "No borrar mensajes", "0",
                    "Última hora", "1",
                    "Últimas 6 horas", "6",
                    "Últimas 12 horas", "12",
                    "Últimas 24 horas", "24",
                    "Últimos 3 días", "72",
                    "Últimos 7 días", "168");

            // Obtén lo que el usuario ha escrito
            String userInput = event.getFocusedOption().getValue();

            // Filtrar opciones que coincidan con el texto ingresado
            List<Command.Choice> suggestions = opciones.entrySet().stream()
                    .filter(entry -> entry.getKey().toLowerCase().startsWith(userInput.toLowerCase()))
                    .map(entry -> new Command.Choice(entry.getKey(), entry.getValue()))
                    .toList();

            // Responder con las opciones filtradas
            event.replyChoices(suggestions).queue();
        }
    }

    public String getName() {
        return name;
    }

}
