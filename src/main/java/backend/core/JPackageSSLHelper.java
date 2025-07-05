package backend.core;

import backend.log.LoggingManager;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.security.SecureRandom;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

/**
 * Clase de ayuda para configurar SSL específicamente para jpackage.
 * Proporciona configuraciones SSL más permisivas cuando se ejecuta desde un JRE empaquetado.
 * También incluye utilidades para compatibilidad con jpackage.
 * 
 * @author PelayoPS
 */
public class JPackageSSLHelper {
    
    private static final LoggingManager logger = new LoggingManager();
    private static boolean sslConfigured = false;
    
    /**
     * Configura SSL de manera más permisiva para jpackage.
     * Solo se ejecuta una vez para evitar configuraciones duplicadas.
     */
    public static void configureSSLForJPackage() {
        if (sslConfigured) {
            return;
        }
        
        try {
            logger.logInfo("Aplicando configuración SSL específica para jpackage...");
            
            // Crear un TrustManager que acepta todos los certificados
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        // Confiar en todos los certificados de cliente
                    }
                    
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        // Confiar en todos los certificados de servidor
                    }
                }
            };
            
            // Crear y configurar el contexto SSL
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            
            // Establecer el contexto SSL por defecto
            SSLContext.setDefault(sslContext);
            
            // Configurar el HostnameVerifier para que acepte todos los hostnames
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true; // Aceptar todos los hostnames
                }
            });
            
            // Establecer el socket factory por defecto
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            
            sslConfigured = true;
            logger.logInfo("Configuración SSL para jpackage aplicada correctamente");
            
        } catch (Exception e) {
            logger.logError("Error al configurar SSL para jpackage: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica si estamos ejecutándose desde un JRE empaquetado con jpackage.
     * 
     * @return true si se está ejecutando desde jpackage
     */
    public static boolean isRunningFromJPackage() {
        String javaHome = System.getProperty("java.home");
        String executablePath = System.getProperty("java.class.path");
        
        // Verificar si estamos en un directorio de aplicación jpackage
        return javaHome != null && (
            javaHome.contains("runtime") || 
            javaHome.contains("jre") ||
            executablePath.contains(".exe") ||
            executablePath.isEmpty() // jpackage no establece classpath
        );
    }

    /**
     * Aplica configuración SSL solo si se está ejecutando desde jpackage.
     */
    public static void configureIfJPackage() {
        if (isRunningFromJPackage()) {
            logger.logInfo("Detectado entorno jpackage, aplicando configuraciones SSL específicas");
            configureSSLForJPackage();
        } else {
            logger.logInfo("Entorno estándar detectado, usando configuraciones SSL por defecto");
        }
    }
    
    /**
     * Obtiene información de CPU de manera compatible con jpackage.
     * Utiliza alternativas cuando com.sun.management.OperatingSystemMXBean no está disponible.
     * 
     * @return String con información de CPU o "-" si no está disponible
     */
    public static String getCompatibleCpuInfo() {
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            
            // Intentar usar com.sun.management.OperatingSystemMXBean si está disponible
            if (!isRunningFromJPackage()) {
                try {
                    // Solo intentar esto en entornos no empaquetados
                    if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                        com.sun.management.OperatingSystemMXBean sunOsBean = 
                            (com.sun.management.OperatingSystemMXBean) osBean;
                        double cpuLoad = sunOsBean.getProcessCpuLoad();
                        if (cpuLoad >= 0) {
                            return String.format("%.2f%%", cpuLoad * 100);
                        }
                    }                } catch (NoClassDefFoundError e) {
                    logger.logWarn("com.sun.management.OperatingSystemMXBean no disponible: " + e.getMessage());
                }
            }
            
            // Alternativa para jpackage: usar getSystemLoadAverage
            double loadAverage = osBean.getSystemLoadAverage();
            if (loadAverage >= 0) {
                return String.format("%.2f", loadAverage);
            }
            
            // Alternativa básica: usar información de procesadores disponibles
            int processors = osBean.getAvailableProcessors();
            return processors + " cores";
            
        } catch (Exception e) {
            logger.logError("Error al obtener información de CPU: " + e.getMessage(), e);
            return "-";
        }
    }
    
    /**
     * Obtiene información de memoria de manera compatible con jpackage.
     * 
     * @return String con información de memoria
     */
    public static String getCompatibleMemoryInfo() {
        try {
            Runtime runtime = Runtime.getRuntime();
            long usedMem = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
            long maxMem = runtime.maxMemory() / (1024 * 1024);
            return usedMem + " MB / " + maxMem + " MB";
        } catch (Exception e) {
            logger.logError("Error al obtener información de memoria: " + e.getMessage(), e);
            return "-";
        }
    }
    
    /**
     * Obtiene información extendida del sistema de manera compatible con jpackage.
     * 
     * @return Array con [memoria, cpu] o valores por defecto si hay errores
     */
    public static String[] getCompatibleSystemInfo() {
        String memory = getCompatibleMemoryInfo();
        String cpu = getCompatibleCpuInfo();
        return new String[]{memory, cpu};
    }
}
