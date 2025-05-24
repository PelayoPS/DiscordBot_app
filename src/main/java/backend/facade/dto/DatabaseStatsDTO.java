package backend.facade.dto;

/**
 * DTO para transportar las estadísticas resumidas de la base de datos.
 * Incluye contadores de usuarios, experiencia, penalizaciones y disponibilidad.
 * 
 * @author PelayoPS
 */
public class DatabaseStatsDTO {

    private long userCount;
    private long experienceCount;
    private long penaltyCount;
    private boolean available = true;

    /**
     * Constructor vacío necesario para Jackson/JSON.
     */
    public DatabaseStatsDTO() {
    }

    /**
     * Constructor con contadores de usuarios, experiencia y penalizaciones.
     *
     * @param userCount       Número de usuarios
     * @param experienceCount Número de experiencias
     * @param penaltyCount    Número de penalizaciones
     */
    public DatabaseStatsDTO(long userCount, long experienceCount, long penaltyCount) {
        this.userCount = userCount;
        this.experienceCount = experienceCount;
        this.penaltyCount = penaltyCount;
    }

    /**
     * Constructor con todos los campos.
     *
     * @param userCount       Número de usuarios
     * @param experienceCount Número de experiencias
     * @param penaltyCount    Número de penalizaciones
     * @param available       Disponibilidad de la base de datos
     */
    public DatabaseStatsDTO(long userCount, long experienceCount, long penaltyCount, boolean available) {
        this.userCount = userCount;
        this.experienceCount = experienceCount;
        this.penaltyCount = penaltyCount;
        this.available = available;
    }

    /**
     * Obtiene el número de usuarios.
     *
     * @return Número de usuarios
     */
    public long getUserCount() {
        return userCount;
    }

    /**
     * Establece el número de usuarios.
     *
     * @param userCount Número de usuarios
     */
    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }

    /**
     * Obtiene el número de experiencias.
     *
     * @return Número de experiencias
     */
    public long getExperienceCount() {
        return experienceCount;
    }

    /**
     * Establece el número de experiencias.
     *
     * @param experienceCount Número de experiencias
     */
    public void setExperienceCount(long experienceCount) {
        this.experienceCount = experienceCount;
    }

    /**
     * Obtiene el número de penalizaciones.
     *
     * @return Número de penalizaciones
     */
    public long getPenaltyCount() {
        return penaltyCount;
    }

    /**
     * Establece el número de penalizaciones.
     *
     * @param penaltyCount Número de penalizaciones
     */
    public void setPenaltyCount(long penaltyCount) {
        this.penaltyCount = penaltyCount;
    }

    /**
     * Indica si la base de datos está disponible.
     *
     * @return true si está disponible, false si no
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Establece la disponibilidad de la base de datos.
     *
     * @param available true si está disponible, false si no
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

}