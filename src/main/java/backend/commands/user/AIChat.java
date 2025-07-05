package backend.commands.user;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import backend.api.Command;
import backend.controller.UserController;
import backend.log.LoggingManager;

import java.lang.reflect.Type;

import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Comando para iniciar un chat con IA en un hilo de Discord.
 * Permite a los usuarios interactuar con un modelo de lenguaje a través de un
 * hilo dedicado.
 * 
 * @author PelayoPS
 */
public class AIChat implements Command {
    private static final LoggingManager logger = new LoggingManager();
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL_NAME = "llama-3.3-70b-versatile";

    private static String apiKey = "";
    private static final Map<String, List<Map<String, Object>>> chatSessions = new HashMap<>();

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    private final UserController userController;

    /**
     * Constructor de la clase AIChat.
     * 
     * @param userController Controlador de usuario para gestionar la sesión de chat IA
     */
    public AIChat(UserController userController) {
        this.userController = userController;

        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                logger.logError("No se pudo encontrar config.properties",
                        new RuntimeException("No se pudo encontrar el archivo de configuración"));
                throw new RuntimeException("No se pudo encontrar el archivo de configuración");
            }
            properties.load(input);

            AIChat.apiKey = properties.getProperty("gemini.api.key");

            if (AIChat.apiKey == null || AIChat.apiKey.isEmpty() ||
                    AIChat.apiKey.equals("tu_clave_api_de_gemini")) {
                logger.logInfo("ADVERTENCIA: gemini.api.key no está configurada correctamente");
            } else {
                logger.logInfo("API key de Gemini configurada correctamente.");
            }

        } catch (IOException ex) {
            logger.logError("Error al cargar el archivo de configuración", ex);
            throw new RuntimeException("Error al cargar la configuración", ex);
        }
    }

    /**
     * Ejecuta la lógica del comando cuando es invocado por un usuario.
     * Inicia un hilo de chat con IA y envía la primera respuesta.
     * 
     * @param event El evento de interacción del comando
     */
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (apiKey == null || apiKey.isEmpty()) {
            event.reply("Error: La clave API de Gemini no está configurada. Contacte al administrador del bot.")
                    .setEphemeral(true).queue();
            return;
        }
        event.deferReply().queue();
        String query = event.getOption("prompt") != null ? event.getOption("prompt").getAsString()
                : "Hola, soy un asistente IA. ¿En qué puedo ayudarte?";
        String threadName = "Chat-IA-" + event.getUser().getName();
        event.getChannel().asTextChannel().createThreadChannel(threadName).queue(thread -> {
            // Inicializar la sesión en chatSessions para el nuevo hilo
            List<Map<String, Object>> history = new ArrayList<>();
            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", query);
            history.add(userMessage);
            chatSessions.put(thread.getId(), history);

            // Enviar la primera respuesta de la IA automáticamente
            CompletableFuture.runAsync(() -> {
                try {
                    String aiResponse = sendMessageToThread(thread.getId(), query);
                    enviarRespuestaFragmentada(thread, aiResponse);
                } catch (Exception e) {
                    thread.sendMessage("Error al procesar tu mensaje: " + e.getMessage()).queue();
                }
            });

            userController.iniciarChatIA(thread, event.getUser(), query);
            event.getHook().editOriginal("¡Chat IA iniciado en " + thread.getAsMention() + "!").queue();
            thread.addThreadMember(event.getUser()).queue();
        });
    }

    /**
     * Envía un mensaje al hilo de chat y obtiene la respuesta de la IA.
     * 
     * @param threadId ID del hilo de Discord
     * @param message  Mensaje del usuario
     * @return Respuesta generada por la IA
     * @throws IOException          Si ocurre un error de red o de API
     * @throws InterruptedException Si la petición es interrumpida
     */
    public static String sendMessageToThread(String threadId, String message) throws IOException, InterruptedException {
        logger.logInfo("Enviando mensaje a OpenRouter: " + message);

        List<Map<String, Object>> history = chatSessions.get(threadId);

        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put("model", MODEL_NAME);

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", message);

        List<Map<String, Object>> messages = new ArrayList<>();

        for (Map<String, Object> msg : history) {
            String role = (String) msg.get("role");
            String content = "";

            if (msg.containsKey("content")) {
                content = (String) msg.get("content");
            } else if (msg.containsKey("parts")) {
                Object partsObj = msg.get("parts");
                if (partsObj instanceof List<?>) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) partsObj;
                    if (!parts.isEmpty() && parts.get(0) instanceof Map) {
                        Map<String, Object> part = parts.get(0);
                        if (part.containsKey("text")) {
                            content = (String) part.get("text");
                        }
                    }
                }
            }
            Map<String, Object> openRouterMsg = new HashMap<>();
            if ("user".equals(role) || "assistant".equals(role) || "system".equals(role)) {
                openRouterMsg.put("role", role);
            } else if ("model".equals(role)) {
                openRouterMsg.put("role", "assistant");
            } else {
                openRouterMsg.put("role", "user");
                logger.logInfo("Valor de role no reconocido '" + role + "' convertido a 'user'");
            }
            openRouterMsg.put("content", content);
            messages.add(openRouterMsg);
        }

        messages.add(userMessage);

        Map<String, Object> historyEntry = new HashMap<>();
        historyEntry.put("role", "user");
        historyEntry.put("content", message);
        history.add(historyEntry);

        requestBody.put("messages", messages);

        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 1024);
        requestBody.put("top_p", 0.95);
        String requestBodyJson = gson.toJson(requestBody);
        logger.logInfo("Cuerpo de la solicitud: " + requestBodyJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            logger.logInfo("Error en la respuesta de OpenRouter: " + response.statusCode());
            logger.logInfo("Cuerpo de la respuesta: " + response.body());
            logger.logInfo("Cuerpo de la solicitud: " + requestBodyJson);
            throw new IOException(
                    "Error en la respuesta de OpenRouter: " + response.statusCode() + " - " + response.body());
        }
        String responseBody = response.body();
        try {
            Type responseType = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> jsonResponse = gson.fromJson(responseBody, responseType);

            logger.logInfo("Claves en la respuesta: " + jsonResponse.keySet());

            if (!jsonResponse.containsKey("choices") || jsonResponse.get("choices") == null) {
                logger.logError("La respuesta no contiene la clave 'choices' o es nula", null);
                if (jsonResponse.containsKey("error")) {
                    Type errorType = new TypeToken<Map<String, Object>>() {
                    }.getType();
                    Map<String, Object> error = gson.fromJson(gson.toJson(jsonResponse.get("error")), errorType);
                    throw new IOException("Error devuelto por OpenRouter: " + error.get("message"));
                }
                throw new IOException("Formato de respuesta desconocido: " + responseBody);
            }

            Type choicesType = new TypeToken<List<Map<String, Object>>>() {
            }.getType();
            List<Map<String, Object>> choices = gson.fromJson(
                    gson.toJson(jsonResponse.get("choices")), choicesType);

            if (choices.isEmpty()) {
                throw new IOException("La lista de 'choices' está vacía");
            }

            Map<String, Object> firstChoice = choices.get(0);
            logger.logInfo("Claves en firstChoice: " + firstChoice.keySet());

            if (!firstChoice.containsKey("message") || firstChoice.get("message") == null) {
                throw new IOException("No se encontró la clave 'message' en la primera opción");
            }

            Type messageType = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> aiResponseMessage = gson.fromJson(gson.toJson(firstChoice.get("message")), messageType);
            String responseText = (String) aiResponseMessage.get("content");

            Map<String, Object> aiMessage = new HashMap<>();
            List<Map<String, Object>> aiParts = new ArrayList<>();
            Map<String, Object> aiTextPart = new HashMap<>();
            aiTextPart.put("text", responseText);
            aiParts.add(aiTextPart);
            aiMessage.put("role", "assistant");
            aiMessage.put("parts", aiParts);
            history.add(aiMessage);

            return responseText;

        } catch (Exception e) {
            logger.logError("Error al procesar la respuesta de Gemini", e);
            throw new IOException("Error al procesar la respuesta: " + e.getMessage(), e);
        }
    }

    /**
     * Devuelve la definición del comando slash para su registro en Discord.
     * 
     * @return Los datos del comando slash
     */
    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("aichat", "Inicia un chat con IA en un hilo")
                .addOption(OptionType.STRING, "prompt", "Mensaje inicial para la IA", false);
    }

    /**
     * Devuelve el nombre del comando.
     * 
     * @return El nombre del comando
     */
    @Override
    public String getName() {
        return "aichat";
    }

    /**
     * Devuelve el mapa de sesiones de chat activas.
     * 
     * @return Mapa de sesiones de chat por ID de hilo
     */
    public static Map<String, List<Map<String, Object>>> getChatSessions() {
        return chatSessions;
    }

    /**
     * Envía una respuesta fragmentada al canal si excede el límite de caracteres de Discord.
     * 
     * @param canal   Canal de hilo de Discord
     * @param mensaje Mensaje a enviar
     */
    private static void enviarRespuestaFragmentada(ThreadChannel canal, String mensaje) {
        final int LIMITE_DISCORD = 1900;

        if (mensaje.length() <= LIMITE_DISCORD) {
            canal.sendMessage(mensaje).queue();
            return;
        }

        int inicio = 0;
        while (inicio < mensaje.length()) {
            int fin = Math.min(inicio + LIMITE_DISCORD, mensaje.length());

            if (fin < mensaje.length()) {
                int ultimoSalto = mensaje.lastIndexOf("\n", fin);
                if (ultimoSalto > inicio) {
                    fin = ultimoSalto + 1;
                }
            }

            String fragmento = mensaje.substring(inicio, fin);
            canal.sendMessage(fragmento).queue();

            inicio = fin;
        }
    }
}
