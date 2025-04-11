package bot.commands.user;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import bot.api.Command;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class AIChat implements Command {
    private static final Logger LOGGER = Logger.getLogger(AIChat.class.getName());
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    // Usando el modelo Llama 3.3
    private static final String MODEL_NAME = "llama-3.3-70b-versatile";

    private static String apiKey = "";
    // Mapa para almacenar los historiales de chat por ID de hilo
    private static final Map<String, List<Map<String, Object>>> chatSessions = new HashMap<>();

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    /**
     * Constructor de AIChat.
     * Carga la configuración desde el archivo de propiedades y verifica la clave
     * API.
     */
    public AIChat() {
        // Cargar la configuración desde el archivo de propiedades
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                LOGGER.severe("No se pudo encontrar config.properties");
                throw new RuntimeException("No se pudo encontrar el archivo de configuración");
            }
            properties.load(input);

            AIChat.apiKey = properties.getProperty("gemini.api.key");

            if (AIChat.apiKey == null || AIChat.apiKey.isEmpty() ||
                    AIChat.apiKey.equals("tu_clave_api_de_gemini")) {
                LOGGER.warning("ADVERTENCIA: gemini.api.key no está configurada correctamente");
            } else {
                LOGGER.info("API key de Gemini configurada correctamente.");
            }

        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error al cargar el archivo de configuración", ex);
            throw new RuntimeException("Error al cargar la configuración", ex);
        }
    }

    /**
     * Método que se ejecuta al recibir el comando de chat IA.
     * 
     * @param event Evento de interacción del comando
     */
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Verificar que la API key esté configurada
        if (apiKey == null || apiKey.isEmpty()) {
            event.reply("Error: La clave API de Gemini no está configurada. Contacte al administrador del bot.")
                    .setEphemeral(true).queue();
            return;
        }
        // Responder inmediatamente para no dejar colgada la interacción
        event.deferReply().queue();

        String query = event.getOption("prompt") != null ? event.getOption("prompt").getAsString()
                : "Hola, soy un asistente IA. ¿En qué puedo ayudarte?";

        // Crear un nuevo hilo para la conversación
        String threadName = "Chat-IA-" + event.getUser().getName();
        event.getChannel().asTextChannel().createThreadChannel(threadName).queue(thread -> {
            try {
                // Crear una nueva sesión para este hilo
                List<Map<String, Object>> history = new ArrayList<>();
                chatSessions.put(thread.getId(), history);

                // Enviar primer mensaje y procesar respuesta
                CompletableFuture.runAsync(() -> {
                    try { // Mostrar indicador de "escribiendo..."
                        thread.sendTyping().queue();

                        // Enviar mensaje a la API de Gemini
                        String aiResponse = sendMessageToThread(thread.getId(), query);

                        // Enviar la respuesta al hilo usando el método para fragmentar mensajes largos
                        enviarRespuestaFragmentada(thread, aiResponse);

                        // Notificar al usuario que el chat está listo
                        event.getHook().editOriginal("¡Chat IA iniciado en " + thread.getAsMention() + "!").queue();

                        // Añadir el usuario al hilo
                        thread.addThreadMember(event.getUser()).queue();

                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error en la comunicación con Gemini", e);
                        thread.sendMessage("Error al comunicarse con la IA: " + e.getMessage()).queue();
                        event.getHook().editOriginal("Error al iniciar el chat IA.").queue();
                    }
                });

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error al inicializar la sesión de chat", e);
                event.getHook().editOriginal("Error al iniciar el chat IA: " + e.getMessage()).queue();
            }
        });
    }

    /**
     * Envía un mensaje a la API de Gemini y obtiene una respuesta.
     * 
     * @param threadId El ID del hilo de Discord
     * @param message  El mensaje a enviar
     * @return La respuesta de la IA
     * @throws IOException          Si hay un error de comunicación
     * @throws InterruptedException Si la operación es interrumpida
     */
    public static String sendMessageToThread(String threadId, String message) throws IOException, InterruptedException {
        LOGGER.info("Enviando mensaje a OpenRouter: " + message);

        // Obtener el historial asociado a este hilo
        List<Map<String, Object>> history = chatSessions.get(threadId);

        // Crear el cuerpo de la solicitud según el formato de OpenRouter (compatible
        // con OpenAI)
        Map<String, Object> requestBody = new HashMap<>();

        // Especificar el modelo a usar
        requestBody.put("model", MODEL_NAME);

        // Crear el nuevo mensaje del usuario
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", message);

        // Convertir el historial al formato de mensajes de OpenRouter/OpenAI
        List<Map<String, Object>> messages = new ArrayList<>();

        // Añadir mensajes del historial
        for (Map<String, Object> msg : history) {
            String role = (String) msg.get("role");
            String content = "";

            // Extraer el contenido del mensaje según la estructura
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
            } // Convertir al formato OpenRouter/Groq y añadir al historial de mensajes
            Map<String, Object> openRouterMsg = new HashMap<>();
            // Asegurarse de que el role sea uno de los valores permitidos: "user",
            // "assistant" o "system"
            if ("user".equals(role) || "assistant".equals(role) || "system".equals(role)) {
                openRouterMsg.put("role", role);
            } else if ("model".equals(role)) {
                // Convertir "model" a "assistant" que es el valor aceptado
                openRouterMsg.put("role", "assistant");
            } else {
                // Para cualquier otro valor, asumimos "user" por defecto
                openRouterMsg.put("role", "user");
                LOGGER.warning("Valor de role no reconocido '" + role + "' convertido a 'user'");
            }
            openRouterMsg.put("content", content);
            messages.add(openRouterMsg);
        }

        // Añadir el mensaje actual
        messages.add(userMessage);

        // Guardar el mensaje en el historial en formato OpenRouter
        Map<String, Object> historyEntry = new HashMap<>();
        historyEntry.put("role", "user");
        historyEntry.put("content", message);
        history.add(historyEntry);

        // Añadir los mensajes al cuerpo de la solicitud
        requestBody.put("messages", messages);

        // Parámetros adicionales para la generación
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 1024);
        requestBody.put("top_p", 0.95); // Convertir el cuerpo de la solicitud a JSON
        String requestBodyJson = gson.toJson(requestBody);
        LOGGER.fine("Cuerpo de la solicitud: " + requestBodyJson); // Construir la solicitud HTTP para Groq
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                // Groq no necesita encabezados adicionales como OpenRouter
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .build();

        // Enviar la solicitud y obtener la respuesta
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Verificar si hay error en la respuesta
        if (response.statusCode() != 200) {
            LOGGER.warning("Error en la respuesta de OpenRouter: " + response.statusCode());
            LOGGER.warning("Cuerpo de la respuesta: " + response.body());
            LOGGER.warning("Cuerpo de la solicitud: " + requestBodyJson);
            throw new IOException(
                    "Error en la respuesta de OpenRouter: " + response.statusCode() + " - " + response.body());
        } // Analizar la respuesta
        String responseBody = response.body();
        try {
            // Parsear la respuesta JSON (formato OpenAI/OpenRouter)
            Type responseType = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> jsonResponse = gson.fromJson(responseBody, responseType);

            // Mostrar las claves de la respuesta para depuración

            // Mostrar las claves de la respuesta para depuración
            LOGGER.info("Claves en la respuesta: " + jsonResponse.keySet());

            // Verificar si la estructura de la respuesta contiene 'choices'
            if (!jsonResponse.containsKey("choices") || jsonResponse.get("choices") == null) {
                LOGGER.severe("La respuesta no contiene la clave 'choices' o es nula");
                // Intentar buscar alternativas en el formato de respuesta
                if (jsonResponse.containsKey("error")) {
                    Type errorType = new TypeToken<Map<String, Object>>() {
                    }.getType();
                    Map<String, Object> error = gson.fromJson(gson.toJson(jsonResponse.get("error")), errorType);
                    throw new IOException("Error devuelto por OpenRouter: " + error.get("message"));
                }
                throw new IOException("Formato de respuesta desconocido: " + responseBody);
            }

            // Extraer y devolver el texto de la respuesta (formato OpenAI)
            Type choicesType = new TypeToken<List<Map<String, Object>>>() {
            }.getType();
            List<Map<String, Object>> choices = gson.fromJson(
                    gson.toJson(jsonResponse.get("choices")), choicesType);

            if (choices.isEmpty()) {
                throw new IOException("La lista de 'choices' está vacía");
            }

            Map<String, Object> firstChoice = choices.get(0);
            LOGGER.info("Claves en firstChoice: " + firstChoice.keySet());

            if (!firstChoice.containsKey("message") || firstChoice.get("message") == null) {
                throw new IOException("No se encontró la clave 'message' en la primera opción");
            }

            Type messageType = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> aiResponseMessage = gson.fromJson(gson.toJson(firstChoice.get("message")), messageType);
            String responseText = (String) aiResponseMessage.get("content");

            // Añadir la respuesta al historial
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
            LOGGER.log(Level.SEVERE, "Error al procesar la respuesta de Gemini", e);
            throw new IOException("Error al procesar la respuesta: " + e.getMessage(), e);
        }
    }

    /**
     * Método que se ejecuta al recibir el comando de chat IA.
     * 
     * @param event Evento de interacción del comando
     */
    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("aichat", "Inicia un chat con IA en un hilo")
                .addOption(OptionType.STRING, "prompt", "Mensaje inicial para la IA", false);
    }

    /**
     * Método que se ejecuta al recibir el comando de chat IA.
     * 
     * @param event Evento de interacción del comando
     */
    @Override
    public String getName() {
        return "aichat";
    }

    /**
     * Método que se ejecuta al recibir el comando de chat IA.
     * 
     * @param event Evento de interacción del comando
     */
    public static Map<String, List<Map<String, Object>>> getChatSessions() {
        return chatSessions;
    }

    /**
     * Envía un mensaje fragmentándolo en partes si supera el límite de caracteres
     * de Discord
     * 
     * @param canal   El canal donde enviar el mensaje
     * @param mensaje El mensaje a enviar
     */
    private static void enviarRespuestaFragmentada(ThreadChannel canal, String mensaje) {
        // Límite de Discord es 2000 caracteres
        final int LIMITE_DISCORD = 1900; // Un poco menos para estar seguros

        // Si el mensaje es más corto que el límite, enviarlo directamente
        if (mensaje.length() <= LIMITE_DISCORD) {
            canal.sendMessage(mensaje).queue();
            return;
        }

        // Dividir el mensaje en fragmentos
        int inicio = 0;
        while (inicio < mensaje.length()) {
            int fin = Math.min(inicio + LIMITE_DISCORD, mensaje.length());

            // Intentar dividir en un salto de línea para que sea más legible
            if (fin < mensaje.length()) {
                int ultimoSalto = mensaje.lastIndexOf("\n", fin);
                if (ultimoSalto > inicio) {
                    fin = ultimoSalto + 1; // Incluir el salto de línea
                }
            }

            String fragmento = mensaje.substring(inicio, fin);
            canal.sendMessage(fragmento).queue();

            inicio = fin;
        }
    }
}
