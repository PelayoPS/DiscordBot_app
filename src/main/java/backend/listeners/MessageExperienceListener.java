package backend.listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backend.config.ConfigService;
import backend.models.Usuario;
import backend.services.UsuarioService;

/**
 * Listener para gestionar la experiencia de usuario basada en mensajes.
 * Otorga experiencia a los usuarios cuando envían mensajes, respetando el cooldown configurado.
 * 
 * @author PelayoPS
 */
public class MessageExperienceListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(MessageExperienceListener.class);
    private final UsuarioService usuarioService;
    private final ConfigService configService;

    /**
     * Constructor de MessageExperienceListener.
     *
     * @param usuarioService Servicio de usuarios
     * @param configService  Servicio de configuración
     */
    public MessageExperienceListener(UsuarioService usuarioService, ConfigService configService) {
        this.usuarioService = usuarioService;
        this.configService = configService;
    }

    /**
     * Evento que se ejecuta al recibir un mensaje.
     * Otorga experiencia al usuario si no está en cooldown.
     *
     * @param event Evento de mensaje recibido
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User author = event.getAuthor();
        if (author.isBot()) {
            return; // Ignorar mensajes de bots
        }

        Long idUsuario = author.getIdLong();

        try {
            int puntosBase = Integer.parseInt(configService.get("experiencia.puntos_base", "15"));
            int cooldownSegundos = Integer.parseInt(configService.get("experiencia.cooldown_segundos", "60"));

            // --- OBTENER O CREAR USUARIO ---
            // Intenta obtener el usuario por su ID. Si no existe, crea uno nuevo.
            Usuario usuario = usuarioService.findById(idUsuario)
                .orElseGet(() -> {
                    Usuario nuevoUsuario = new Usuario(idUsuario, "MIEMBRO");
                    try {
                        usuarioService.save(nuevoUsuario);
                        logger.info("Nuevo usuario {} creado.", idUsuario);
                        return nuevoUsuario;
                    } catch (Exception ex) {
                        logger.warn("No se pudo crear el usuario {}: {}", idUsuario, ex.getMessage());
                        // Devuelve un usuario temporal para evitar NullPointerException,
                        // aunque la experiencia no se guardará si la creación falla.
                        return new Usuario(idUsuario, "MIEMBRO");
                    }
                });

            // Si el usuario no pudo ser recuperado o creado (situación excepcional si save falla y devuelve el temporal)
            // y no tiene un ID válido en la base de datos (lo cual no debería pasar si findById o save funcionan),
            // podríamos añadir una comprobación adicional, pero por ahora asumimos que 'usuario' es válido.

            long ahora = System.currentTimeMillis();
            long ultimoMensaje = usuario.getTimestampUltimoMensaje();

            if (ahora - ultimoMensaje >= cooldownSegundos * 1000L) {
                usuarioService.actualizarExperiencia(idUsuario, puntosBase);
                // Para loguear la experiencia actualizada, volvemos a obtener el usuario
                Usuario usuarioActualizado = usuarioService.findById(idUsuario).orElse(usuario); //Fallback al usuario anterior si no se encuentra por alguna razón
                logger.info("Experiencia actualizada para usuario {}: Nivel {}, XP {}", 
                            idUsuario, usuarioActualizado.getNivel(), usuarioActualizado.getPuntosXp());
            } else {
                logger.info("Usuario {} en cooldown, no se otorga experiencia. Último mensaje hace {} ms, cooldown {} ms",
                            idUsuario, (ahora - ultimoMensaje), cooldownSegundos * 1000L);
            }

        } catch (NumberFormatException e) {
            logger.error("Error al parsear los valores de configuración para el sistema de experiencia.", e);
        } catch (Exception e) {
            logger.error("Error inesperado al procesar experiencia para usuario {}: {}", idUsuario, e.getMessage(), e);
        }
    }
}
