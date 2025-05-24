package backend.repositories;

import java.util.List;

import backend.models.Penalizacion;

import java.time.LocalDateTime;

/**
 * Repositorio para operaciones sobre penalizaciones de usuarios.
 * Permite buscar penalizaciones por usuario, admin/mod, tipo y rango de fechas.
 * 
 * @author PelayoPS
 */
public interface PenalizacionRepository extends Repository<Penalizacion, Long> {
    /**
     * Busca penalizaciones por ID de usuario.
     * 
     * @param idUsuario ID del usuario
     * @return Lista de penalizaciones encontradas
     */
    List<Penalizacion> findByIdUsuario(Long idUsuario);

    /**
     * Busca penalizaciones por ID de administrador/moderador.
     * 
     * @param idAdminMod ID del admin/mod
     * @return Lista de penalizaciones encontradas
     */
    List<Penalizacion> findByIdAdminMod(Long idAdminMod);

    /**
     * Busca penalizaciones por tipo.
     * 
     * @param tipo Tipo de penalización
     * @return Lista de penalizaciones encontradas
     */
    List<Penalizacion> findByTipo(String tipo);

    /**
     * Busca penalizaciones en un rango de fechas.
     * 
     * @param inicio Fecha de inicio
     * @param fin    Fecha de fin
     * @return Lista de penalizaciones encontradas
     */
    List<Penalizacion> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}