package bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración global de CORS para la aplicación web.
 * Permite definir los orígenes, métodos y cabeceras permitidas para las
 * peticiones HTTP.
 * 
 * @author PelayoPS
 */
@Configuration
public class WebConfig {
    /**
     * Bean que configura las reglas de CORS para la aplicación.
     * 
     * @return WebMvcConfigurer con la configuración de CORS
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            /**
             * Configura las reglas de mapeo de CORS.
             * 
             * @param registry CorsRegistry para definir las reglas de CORS
             */
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*") // Puedes poner aquí la IP de tu móvil o PC para más seguridad
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "FETCH")
                        .allowedHeaders("*");
            }
        };
    }
}
