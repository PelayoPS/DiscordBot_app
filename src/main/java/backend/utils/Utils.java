package backend.utils;

/**
 * Utilidades generales para validaciones y formateo de mensajes.
 * 
 * @author PelayoPS
 */
public class Utils {
    /**
     * Valida si un ID de usuario es válido (17 a 19 dígitos).
     * 
     * @param userId ID de usuario a validar
     * @return true si es válido, false si no
     */
    public static boolean isValidUserId(String userId) {
        return userId != null && userId.matches("\\d{17,19}");
    }

    /**
     * Formatea un mensaje de penalización para un usuario.
     * 
     * @param userName Nombre del usuario
     * @param action   Acción realizada
     * @return Mensaje formateado
     */
    public static String formatPenaltyMessage(String userName, String action) {
        return String.format("El usuario %s ha sido %s.", userName, action);
    }

    /**
     * Formatea una duración en milisegundos a minutos y segundos.
     * 
     * @param duration Duración en milisegundos
     * @return Cadena formateada en minutos y segundos
     */
    public static String formatDuration(long duration) {
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d minutos y %d segundos", minutes, seconds);
    }
}