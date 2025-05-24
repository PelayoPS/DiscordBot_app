package backend.facade.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador personalizado para manejar errores HTTP y devolver respuestas
 * JSON amigables.
 * 
 * @author PelayoPS
 */
@Controller
public class CustomErrorController implements ErrorController {
    /**
     * Maneja las rutas de error y devuelve una respuesta JSON personalizada.
     *
     * @param request HttpServletRequest con información del error
     * @return ErrorResponse con detalles del error
     */
    @RequestMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ErrorResponse handleError(HttpServletRequest request) {
        Object status = request.getAttribute("jakarta.servlet.error.status_code");
        int statusCode = status != null ? Integer.parseInt(status.toString()) : 500;
        String errorMsg;
        switch (statusCode) {
            case 404 -> errorMsg = "¡Oops! Página no encontrada";
            case 500 -> errorMsg = "Error interno del servidor";
            default -> errorMsg = "Ha ocurrido un error inesperado";
        }
        return new ErrorResponse(statusCode, errorMsg, request.getRequestURI());
    }

    /**
     * Clase interna para estructurar la respuesta de error.
     */
    public static class ErrorResponse {
        public int status;
        public String message;
        public String path;

        /**
         * Constructor de ErrorResponse.
         *
         * @param status  Código de estado HTTP
         * @param message Mensaje de error
         * @param path    Ruta solicitada
         */
        public ErrorResponse(int status, String message, String path) {
            this.status = status;
            this.message = message;
            this.path = path;
        }
    }
}
